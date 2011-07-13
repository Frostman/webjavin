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

package ru.frostman.web.secure;

import com.google.common.collect.Maps;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExecutableStatement;
import ru.frostman.web.Javin;
import ru.frostman.web.thr.JavinRuntimeException;

import java.util.List;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
class SecureExpression {
    private String expressionStr;
    private List<String> paramTypes;
    private ExecutableStatement expression;

    SecureExpression(String expressionStr, List<String> paramTypes) {
        this.expressionStr = expressionStr;
        this.paramTypes = paramTypes;
    }

    void compile() {
        try {
            ParserContext context = new ParserContext();
            context.setStrongTyping(true);

            context.addInput("user", User.class);
            context.addInput("role", String.class);

            int idx = 1;
            for (String paramType : paramTypes) {
                context.addInput("param$" + idx, Javin.getClasses().getClassLoader().loadClass(paramType));

                idx++;
            }

            context.addImport("isAuth", AppSecurityManager.class.getMethod("isAuthenticated"));
            context.addImport("hasRole", AppSecurityManager.class.getMethod("hasRole", String.class));

            expression = (ExecutableStatement) MVEL.compileExpression(expressionStr, context);

            if (expression.getKnownEgressType() != boolean.class && expression.getKnownEgressType() != Boolean.class) {
                throw new JavinRuntimeException("Expression should return boolean");
            }
        } catch (Exception e) {
            throw new JavinRuntimeException("Can't compile secure expression: " + expressionStr, e);
        }
    }

    boolean execute(User user, String role, Object... params) {
        try {
            Map<String, Object> vars = Maps.newHashMap();
            vars.put("user", user);
            vars.put("role", role);

            int idx = 1;
            for (Object param : params) {
                vars.put("param$" + idx, param);

                idx++;
            }

            return (Boolean) MVEL.executeExpression(expression, vars);
        } catch (Exception e) {
            throw new JavinRuntimeException("Exception while executing secure expression: " + expressionStr, e);
        }
    }

    public String getExpressionStr() {
        return expressionStr;
    }

    public List<String> getParamTypes() {
        return paramTypes;
    }

    public ExecutableStatement getExpression() {
        return expression;
    }
}
