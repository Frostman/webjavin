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

package ru.frostman.web.classloading.enhance;

import com.google.common.collect.Lists;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;
import ru.frostman.web.annotation.JsonParam;
import ru.frostman.web.annotation.Param;
import ru.frostman.web.annotation.Pjax;
import ru.frostman.web.inject.InjectionRule;
import ru.frostman.web.plugin.JavinPlugins;
import ru.frostman.web.thr.ActionEnhancerException;

import java.util.List;

import static ru.frostman.web.classloading.enhance.ClassConstants.*;
import static ru.frostman.web.classloading.enhance.EnhancerUtil.getCtClass;
import static ru.frostman.web.classloading.enhance.EnhancerUtil.isAnnotatedWith;

/**
 * @author slukjanov aka Frostman
 */
public class InjectEnhancer {

    public static StringBuilder resolveParameters(ClassPool classPool, CtBehavior behavior, StringBuilder body)
            throws ClassNotFoundException, NotFoundException {
        Object[][] annotations = behavior.getParameterAnnotations();
        int idx = 0;
        boolean requestBodyJsonParsed = false;
        StringBuilder parameters = new StringBuilder();
        for (CtClass parameterType : behavior.getParameterTypes()) {
            if (resolveServletObjects(classPool, body, parameterType, idx)) {
                // no operations
            } else if (parameterType.equals(getCtClass(classPool, MODEL))) {
                // ru.frostman.web.controller.Model type resolved as current secure
                body.append(MODEL).append(" $param$").append(idx).append(" = mav.getModel();");
            } else if (parameterType.equals(getCtClass(classPool, JAVIN_SESSION))) {
                // ru.frostman.web.session.JavinSession type resolved as current current session
                body.append(JAVIN_SESSION).append(" $param$").append(idx).append(" = " + JAVIN_SESSIONS
                        + ".getSession(request, response);");
            } else if (isAnnotatedWith(annotations[idx], Param.class) != null) {
                resolveParam(classPool, body, behavior, annotations[idx], idx, parameterType);
            } else if (isAnnotatedWith(annotations[idx], JsonParam.class) != null) {
                requestBodyJsonParsed = resolveJsonParam(body, annotations[idx], idx, requestBodyJsonParsed, parameterType);
            } else if (isAnnotatedWith(annotations[idx], Pjax.class) != null) {
                resolvePjax(classPool, behavior, body, idx, parameterType);
            } else if (!resolveCustomInjections(classPool, behavior, body, idx, parameterType)) {
                throw new ActionEnhancerException("Unsupported auto injected method argument type " + parameterType
                        + ": " + behavior.getLongName());
            }

            parameters.append("$param$").append(idx);
            if (idx < annotations.length - 1) {
                parameters.append(",");
            }
            idx++;
        }

        return parameters;
    }

    private static boolean resolveCustomInjections(ClassPool classPool, CtBehavior behavior, StringBuilder body
            , int idx, CtClass parameterType) throws ClassNotFoundException {
        List<InjectionRule> injectionRules = JavinPlugins.get().getCustomInjections();

        for (InjectionRule rule : injectionRules) {
            if (rule.classNames().contains(parameterType.getName())) {
                List<String> annotations = Lists.newLinkedList();
                for (Object obj : behavior.getAnnotations()) {
                    annotations.add(obj.getClass().getName());
                }

                if (annotations.containsAll(rule.annotations())) {
                    body.append(parameterType.getName()).append(" $param$").append(idx)
                            .append(" = ").append(rule.initCode()).append(";");

                    return true;
                }
            }
        }

        return false;
    }

    private static void resolvePjax(ClassPool classPool, CtBehavior behavior, StringBuilder body, int idx, CtClass parameterType) {
        if (parameterType.equals(CtClass.booleanType)) {
            body.append(parameterType.getName()).append(" $param$").append(idx)
                    .append(" = request.getHeader(\"HTTP_X_PJAX\") != null;");
        } else if (parameterType.equals(getCtClass(classPool, JAVA_LANG_BOOLEAN))) {
            body.append(parameterType.getName()).append(" $param$").append(idx)
                    .append(" = java.lang.Boolean.valueOf(request.getHeader(\"HTTP_X_PJAX\") != null);");
        } else {
            throw new ActionEnhancerException("@Pjax annotation should mark Boolean or boolean param, but not: "
                    + parameterType.getName() + " in " + behavior.getLongName());
        }
    }

    private static boolean resolveJsonParam(StringBuilder body, Object[] annotation, int idx, boolean requestBodyJsonParsed, CtClass parameterType) {
        JsonParam paramAnnotation = isAnnotatedWith(annotation, JsonParam.class);

        if (!requestBodyJsonParsed) {
            requestBodyJsonParsed = true;

            body.append(JSON_NODE).append(" ").append(REQUEST_BODY_JSON).append(" = ")
                    .append(JSON_UTIL).append(".parseJsonBody(request);");
        }

        body.append(parameterType.getName()).append(" $param$").append(idx)
                .append(" = (").append(parameterType.getName()).append(")")
                .append(JSON_UTIL).append(".getParam(")
                .append(REQUEST_BODY_JSON).append(", \"").append(parameterType.getName()).append("\"")
                .append(", new ").append(JAVA_LANG_STRING).append("[]{");

        final String[] path = paramAnnotation.name();
        int pathIdx = 0;
        for (String str : path) {
            body.append("\"").append(str).append("\"");
            if (pathIdx < path.length - 1) {
                body.append(",");
            }
        }

        body.append("});");

        if (paramAnnotation.required()) {
            appendRequiredParamCheck(body, idx);

        }

        return requestBodyJsonParsed;
    }

    private static void appendRequiredParamCheck(StringBuilder body, int idx) {
        // append checking parameter for not null
        body.append("if($param$").append(idx).append(" == null) {")
                .append("throw new ").append(PARAMETER_REQUIRED_EXCEPTION)
                .append("(\"required param: \"+").append(idx).append(");" + "}");
    }

    private static void resolveParam(ClassPool classPool, StringBuilder body, CtBehavior behavior, Object[] annotation
            , int idx, CtClass parameterType) {
        Param paramAnnotation = isAnnotatedWith(annotation, Param.class);

        if (parameterType.equals(getCtClass(classPool, JAVA_LANG_STRING))) {
            body.append("String $param$").append(idx).append(" = request.getParameter(\"")
                    .append(paramAnnotation.value()).append("\");");
        } else if (parameterType.equals(CtClass.booleanType)) {
            body.append("boolean $param$").append(idx).append(" = java.lang.Boolean.parseBoolean(request.getParameter(\"")
                    .append(paramAnnotation.value()).append("\"));");
        } else if (parameterType.equals(getCtClass(classPool, JAVA_LANG_BOOLEAN))) {
            body.append("java.lang.Boolean $param$").append(idx).append(" = java.lang.Boolean.valueOf(request.getParameter(\"")
                    .append(paramAnnotation.value()).append("\"));");
        } else {
            throw new ActionEnhancerException("Auto converted @Param method argument type " + parameterType.getName()
                    + " (arg #" + idx + ")"
                    + " is currently unsupported: " + behavior.getLongName());
        }

        if (paramAnnotation.required()) {
            // append checking parameter for not null
            appendRequiredParamCheck(body, idx);
        }
    }

    private static boolean resolveServletObjects(ClassPool classPool, StringBuilder body, CtClass parameterType, int idx) {
        if (parameterType.equals(getCtClass(classPool, HTTP_SERVLET_REQUEST))) {
            // javax.servlet.http.HttpServletRequest type resolved as current request
            body.append(HTTP_SERVLET_REQUEST).append(" $param$").append(idx).append(" = request;");
        } else if (parameterType.equals(getCtClass(classPool, SERVLET_REQUEST))) {
            // javax.servlet.ServletRequest type resolved as current request
            body.append(SERVLET_REQUEST).append(" $param$").append(idx).append(" = request;");
        } else if (parameterType.equals(getCtClass(classPool, HTTP_SERVLET_RESPONSE))) {
            // javax.servlet.http.HttpServletResponse type resolved as current response
            body.append(HTTP_SERVLET_RESPONSE).append(" $param$").append(idx).append(" = response;");
        } else if (parameterType.equals(getCtClass(classPool, SERVLET_RESPONSE))) {
            // javax.servlet.ServletResponse type resolved as current response
            body.append(SERVLET_RESPONSE).append(" $param$").append(idx).append(" = response;");
        } else if (parameterType.equals(getCtClass(classPool, ASYNC_CONTEXT))) {
            // javax.servlet.AsyncContext type resolved as current async context
            body.append(ASYNC_CONTEXT).append(" $param$").append(idx).append(" = asyncContext;");
        } else {
            return false;
        }

        return true;
    }
}
