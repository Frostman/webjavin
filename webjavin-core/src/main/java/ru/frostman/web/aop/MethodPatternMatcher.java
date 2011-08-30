/******************************************************************************
 * WebJavin - Java Web Framework.                                             *
 *                                                                            *
 * Copyright (c) 2011 - Sergey "Frosman" Lukjanov, me@frostman.ru             *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package ru.frostman.web.aop;

import com.google.common.collect.Maps;
import javassist.CtMethod;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExecutableStatement;
import ru.frostman.web.thr.JavinRuntimeException;

import java.lang.reflect.Array;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class MethodPatternMatcher {
    private static final ThreadLocal<CtMethod> currentMethod = new ThreadLocal<CtMethod>();
    private static final Map<String, Object> vars = Maps.newHashMap();

    static {
        vars.put("any", true);
    }

    private final ExecutableStatement expression;

    // package('ru.frostman*') && class('*') && method('*') && return('*') && annotation ()

    public MethodPatternMatcher(String methodPattern) {
        try {
            ParserContext context = new ParserContext();
            context.setStrongTyping(true);

            context.addInput("any", boolean.class);

            Class<?> stringArrClass = Array.newInstance(String.class, 0).getClass();
            context.addImport("package", MethodPatternMatcher.class.getMethod("_package", stringArrClass));

            if (methodPattern.trim().length() == 0) {
                methodPattern = "any";
            }
            expression = (ExecutableStatement) MVEL.compileExpression(methodPattern, context);

            if (expression.getKnownEgressType() != boolean.class && expression.getKnownEgressType() != Boolean.class) {
                throw new JavinRuntimeException("Aop method interceptor pattern should return boolean");
            }
        } catch (Exception e) {
            throw new JavinRuntimeException("Exception while compiling method interceptor patern: " + methodPattern, e);
        }
    }

    public boolean matches(CtMethod method) {
        try {
            currentMethod.set(method);

            return (Boolean) MVEL.executeExpression(expression, vars);
        } finally {
            currentMethod.set(null);
        }
    }

    public static boolean _package(String... packages) {
        return true;
    }
}
