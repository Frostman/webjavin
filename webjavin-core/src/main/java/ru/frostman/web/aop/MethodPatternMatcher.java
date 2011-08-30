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
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExecutableStatement;
import ru.frostman.web.thr.JavinRuntimeException;

import java.lang.annotation.Annotation;
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

    public MethodPatternMatcher(String methodPattern) {
        try {
            ParserContext context = new ParserContext();
            context.setStrongTyping(true);

            context.addInput("any", boolean.class);

            context.addImport("package", MethodPatternMatcher.class.getMethod("_package", String.class));
            context.addImport("class", MethodPatternMatcher.class.getMethod("_class", String.class));
            context.addImport("method", MethodPatternMatcher.class.getMethod("_method", String.class));
            context.addImport("return", MethodPatternMatcher.class.getMethod("_return", String.class));
            context.addImport("annotation", MethodPatternMatcher.class.getMethod("_annotation", String.class));
            context.addImport("params", MethodPatternMatcher.class.getMethod("_params", String.class));

            if (methodPattern.trim().length() == 0) {
                methodPattern = "any";
            }
            expression = (ExecutableStatement) MVEL.compileExpression(methodPattern, context);

            if (expression.getKnownEgressType() != boolean.class && expression.getKnownEgressType() != Boolean.class) {
                throw new JavinRuntimeException("Aop method interceptor pattern should return boolean");
            }
        } catch (Exception e) {
            throw new JavinRuntimeException("Exception while compiling method interceptor pattern: " + methodPattern, e);
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

    public static boolean _package(String packageName) {
        return isMatches(packageName, currentMethod.get().getDeclaringClass().getPackageName());
    }

    public static boolean _class(String className) {
        return isMatches(className, currentMethod.get().getDeclaringClass().getSimpleName());
    }

    public static boolean _method(String methodName) {
        return isMatches(methodName, currentMethod.get().getName());
    }

    public static boolean _return(String returnType) {
        try {
            return isMatches(returnType, currentMethod.get().getReturnType().getName());
        } catch (NotFoundException e) {
            return false;
        }
    }

    public static boolean _annotation(String annotationName) {
        Object[] annotationObjects = new Object[0];
        try {
            annotationObjects = currentMethod.get().getAnnotations();
        } catch (ClassNotFoundException e) {
            return false;
        }
        for (Object object : annotationObjects) {
            Annotation annotation = (Annotation) object;
            if (isMatches(annotationName, annotation.annotationType().getName())) {
                return true;
            }
        }

        return false;
    }

    public static boolean _params(String paramsString) {
        String[] params = paramsString.split(",");
        CtClass[] parameterTypes;
        try {
            parameterTypes = currentMethod.get().getParameterTypes();
        } catch (NotFoundException e) {
            return false;
        }

        if (parameterTypes.length != params.length) {
            return false;
        }

        int idx = 0;
        for (String param : params) {
            if (!isMatches(param, parameterTypes[idx].getName())) {
                return false;
            }
            idx++;
        }

        return true;
    }

    //todo make tests for this method
    @SuppressWarnings({"StringEquality", "ConstantConditions"})
    static boolean isMatches(String pattern, String str) {
        if (pattern == str || pattern.equals(str)) {
            return true;
        }

        if (pattern == null) {
            return false;
        }

        pattern = pattern.trim();
        str = str.trim();

        if (pattern.startsWith("*")) {
            if (pattern.endsWith("*")) {
                return str.contains(pattern.substring(1, Math.max(1, pattern.length() - 1)));
            } else {
                return str.endsWith(pattern.substring(1));
            }
        }

        return pattern.endsWith("*") && str.startsWith(pattern.substring(0, Math.max(0, pattern.length() - 1)));
    }
}
