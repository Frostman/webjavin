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
import com.google.common.collect.Sets;
import javassist.*;
import ru.frostman.web.annotation.*;
import ru.frostman.web.classloading.AppClass;
import ru.frostman.web.controller.Controllers;
import ru.frostman.web.dispatch.ActionDefinition;
import ru.frostman.web.dispatch.url.UrlPattern;
import ru.frostman.web.dispatch.url.UrlPatternType;
import ru.frostman.web.thr.ActionEnhancerException;
import ru.frostman.web.util.HttpMethod;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.frostman.web.classloading.enhance.EnhancerUtil.*;

/**
 * @author slukjanov aka Frostman
 */
class ActionsEnhancer {
    private static final AtomicInteger actionMethodsCount = new AtomicInteger(1);
    private static final String INSTANCE = "$instance";

    private static final String HTTP_SERVLET_REQUEST = "javax.servlet.http.HttpServletRequest";
    private static final String SERVLET_REQUEST = "javax.servlet.ServletRequest";
    private static final String HTTP_SERVLET_RESPONSE = "javax.servlet.http.HttpServletResponse";
    private static final String SERVLET_RESPONSE = "javax.servlet.ServletResponse";
    private static final String ASYNC_CONTEXT = "javax.servlet.AsyncContext";
    private static final String VIEW = "ru.frostman.web.controller.View";
    private static final String MODEL = "ru.frostman.web.controller.Model";
    private static final String MODEL_AND_VIEW = "ru.frostman.web.controller.ModelAndView";
    private static final String JAVA_LANG_STRING = "java.lang.String";
    private static final String THROWABLE = "java.lang.Throwable";
    private static final String DEFAULT_ACTION_CATCH = "ru.frostman.web.thr.DefaultActionCatch";
    private static final String ACTION_EXCEPTION = "ru.frostman.web.dispatch.ActionException";
    private static final String JSON_UTIL = "ru.frostman.web.util.JsonUtil";
    private static final String JSON_NODE = "org.codehaus.jackson.JsonNode";
    private static final String REQUEST_BODY_JSON = "requestBodyJson";
    private static final String PARAMETER_REQUIRED_EXCEPTION = "ru.frostman.web.thr.ParameterRequiredException";
    private static final String JAVA_LANG_BOOLEAN = "java.lang.Boolean";
    private static final String JAVIN_SESSION = "ru.frostman.web.session.JavinSession";
    private static final String JAVIN_SESSIONS = "ru.frostman.web.session.JavinSessions";

    public static void enhance(Map<String, AppClass> classes, ClassPool classPool, CtClass controller,
                               List<ActionDefinition> actionDefinitions) {
        for (CtMethod actionMethod : getDeclaredMethodsAnnotatedWith(Action.class, controller)) {
            try {
                Action actionAnnotation = (Action) actionMethod.getAnnotation(Action.class);
                String[] urls = normalizeUrls(actionAnnotation.value());
                HttpMethod[] methods = actionAnnotation.method();
                boolean async = actionAnnotation.async();

                if (CtClass.voidType == actionMethod.getReturnType()) {
                    throw new ActionEnhancerException("Action method should return some value (not void method): "
                            + actionMethod.getLongName());
                }

                CtClass actionInvoker = generateActionInvoker(classPool, actionMethod, async);

                AppClass generated = new AppClass();
                generated.setName(actionInvoker.getName());
                generated.setEnhancedBytecode(actionInvoker.toBytecode());
                generated.setGenerated(true);

                classes.put(actionInvoker.getName(), generated);

                List<UrlPattern> patterns = Lists.newLinkedList();
                for (String url : urls) {
                    patterns.add(UrlPatternType.get(url, UrlPatternType.SERVLET));
                }

                actionDefinitions.add(new ActionDefinition(patterns, Sets.newHashSet(methods), actionInvoker.getName()));
            } catch (Exception e) {
                throw new ActionEnhancerException("Error while enhancing action: " + actionMethod.getLongName(), e);
            }
        }
    }

    private static CtClass generateActionInvoker(ClassPool classPool, CtMethod actionMethod, boolean async)
            throws CannotCompileException, ClassNotFoundException, NotFoundException {

        final CtClass controller = actionMethod.getDeclaringClass();

        CtClass actionInvoker = classPool.makeClass(controller.getName() + "$action$" + actionMethod.getName()
                + "$" + actionMethodsCount.getAndIncrement());
        actionInvoker.setSuperclass(getCtClass(classPool, "ru.frostman.web.dispatch.ActionInvoker"));

        CtField instanceField = new CtField(controller, "$instance", actionInvoker);
        actionInvoker.addField(instanceField);

        generateActionInvokerConstructor(classPool, actionInvoker, controller);

        generateActionInvokerBefore(classPool, actionInvoker, controller);
        generateActionInvokerAction(classPool, actionInvoker, actionMethod);
        generateActionInvokerAfter(classPool, actionInvoker, controller);

        generateActionInvokerCatchError(classPool, actionInvoker, controller);
        generateActionInvokerIsAsync(classPool, actionInvoker, async);

        return actionInvoker;
    }

    private static void generateActionInvokerConstructor(ClassPool classPool, CtClass actionInvoker, CtClass controller)
            throws CannotCompileException, ClassNotFoundException, NotFoundException {
        CtConstructor constructor = new CtConstructor(new CtClass[]{
                getCtClass(classPool, "javax.servlet.http.HttpServletRequest"),
                getCtClass(classPool, "javax.servlet.http.HttpServletResponse")
        }, actionInvoker);

        StringBuilder body = new StringBuilder();
        // invoke super class constructor
        body.append("{super($$);");

        CtConstructor[] constructors = controller.getConstructors();
        if (constructors.length != 1) {
            throw new ActionEnhancerException("Only one constructor should be in controller: " + controller.getName());
        }

        StringBuilder parameters = resolveParameters(classPool, constructors[0], body);
        // instantiate controller class (with resolved parameters)
        body.append(INSTANCE).append(" = new ").append(controller.getName()).append("(").append(parameters).append(");}");

        constructor.setBody(body.toString());

        actionInvoker.addConstructor(constructor);
    }

    private static void generateActionInvokerBefore(ClassPool classPool, CtClass actionInvoker, CtClass controller)
            throws CannotCompileException, NotFoundException, ClassNotFoundException {
        CtMethod method = new CtMethod(CtClass.voidType, "before", new CtClass[]{}, actionInvoker);

        List<CtMethod> methodList = getMethodsAnnotatedWith(Before.class, controller);

        generateActionInvokerMethodInvocations(classPool, method, methodList);

        actionInvoker.addMethod(method);
    }

    private static void generateActionInvokerAction(ClassPool classPool, CtClass actionInvoker,
                                                    CtMethod actionMethod)
            throws CannotCompileException, NotFoundException, ClassNotFoundException {

        if (!isPublicAndNonStatic(actionMethod)) {
            throw new ActionEnhancerException("Action method should be public and non static: " + actionMethod.getLongName());
        }

        CtMethod method = new CtMethod(CtClass.voidType, "action", new CtClass[]{}, actionInvoker);

        StringBuilder body = new StringBuilder("{");
        StringBuilder parameters = resolveParameters(classPool, actionMethod, body);

        // invoke action method in controller (with resolved parameters)
        body.append("try{ Object result = ").append(INSTANCE).append(".")
                .append(actionMethod.getName()).append("(").append(parameters).append(");");

        CtClass returnType = actionMethod.getReturnType();
        if (returnType.equals(getCtClass(classPool, VIEW))) {
            // iff return type is View then change current ModelAndView's view
            body.append("mav.setView((").append(VIEW).append(") result").append(");");
        } else if (returnType.equals(getCtClass(classPool, MODEL_AND_VIEW))) {
            // iff return type is ModelAndView then change current ModelAndView
            body.append("mav = (").append(MODEL_AND_VIEW).append(") result;");
        } else if (returnType.equals(getCtClass(classPool, JAVA_LANG_STRING))) {
            // iff return type is String then change ModelAndView's view to resolved view by name
            body.append("mav.setView(ru.frostman.web.Javin.getViews().getViewByName((")
                    .append(JAVA_LANG_STRING).append(") result").append("));");
        } else if (actionMethod.getAnnotation(JsonResponse.class) != null) {
            // iff return type is some class then change ModelAndView's view to JsonModelView
            body.append("mav.setView(new ru.frostman.web.view.json.JsonValueView(")
                    .append("result").append("));");
        } else {
            throw new ActionEnhancerException("Action method can't return specified type: " + actionMethod.getLongName());
        }

        // append catch section with ActionException
        body.append("}catch(Throwable th){throw new " + ACTION_EXCEPTION + "(th);}");

        method.setBody(body.append("}").toString());

        actionInvoker.addMethod(method);
    }

    private static void generateActionInvokerAfter(ClassPool classPool, CtClass actionInvoker, CtClass controller)
            throws CannotCompileException, NotFoundException, ClassNotFoundException {
        CtMethod method = new CtMethod(CtClass.voidType, "after", new CtClass[]{}, actionInvoker);

        List<CtMethod> methodList = getMethodsAnnotatedWith(After.class, controller);

        generateActionInvokerMethodInvocations(classPool, method, methodList);

        actionInvoker.addMethod(method);
    }

    private static void generateActionInvokerCatchError(ClassPool classPool, CtClass actionInvoker, CtClass controller)
            throws CannotCompileException, NotFoundException, ClassNotFoundException {
        CtMethod method = new CtMethod(CtClass.voidType, "catchError",
                new CtClass[]{getCtClass(classPool, "java.lang.Throwable")}, actionInvoker);

        List<CtMethod> methods = getMethodsAnnotatedWith(Catch.class, controller);
        if (methods.size() > 1) {
            throw new ActionEnhancerException("Only one method in controller should be marked with @Catch: "
                    + controller.getName());
        }

        StringBuilder body = new StringBuilder("{");
        for (CtMethod invokeMethod : methods) {
            if (invokeMethod.getReturnType() != CtClass.voidType) {
                throw new ActionEnhancerException("Method marked with @Catch should return void: " + invokeMethod.getLongName());
            } else if (!isPublicAndNonStatic(invokeMethod)) {
                throw new ActionEnhancerException("Method marked with @Catch should be public and non static: "
                        + invokeMethod.getLongName());
            } else if (invokeMethod.getParameterTypes().length < 1 ||
                    (!invokeMethod.getParameterTypes()[0].getName().equals(THROWABLE))) {
                throw new ActionEnhancerException("First argument of method marked with @Catch should be java.lang.Throwable: "
                        + invokeMethod.getLongName());
            }

            StringBuilder parameters = resolveParameters(classPool, invokeMethod, body);

            // invoke method with resolved parameters
            body.append("{").append(INSTANCE).append(".")
                    .append(invokeMethod.getName())
                    .append("($1,").append(parameters).append(");")
                    .append("}");
        }

        if (methods.size() == 0) {
            // append throwing default action exception
            body.append("{throw new " + DEFAULT_ACTION_CATCH + "($1);}");
        }

        method.setBody(body.append("}").toString());

        actionInvoker.addMethod(method);
    }

    private static void generateActionInvokerIsAsync(ClassPool classPool, CtClass actionInvoker, boolean async)
            throws CannotCompileException {
        CtMethod method = new CtMethod(CtClass.booleanType, "isAsync", new CtClass[]{}, actionInvoker);

        method.setBody("return " + async + ";");

        actionInvoker.addMethod(method);
    }

    private static void generateActionInvokerMethodInvocations(ClassPool classPool, CtMethod method, List<CtMethod> methods)
            throws NotFoundException, CannotCompileException, ClassNotFoundException {
        StringBuilder body = new StringBuilder("{");
        for (CtMethod invokeMethod : methods) {
            if (invokeMethod.getReturnType() != CtClass.voidType) {
                throw new ActionEnhancerException("Method marked with @After or @Before should return void: "
                        + invokeMethod.getLongName());
            } else if (!isPublicAndNonStatic(invokeMethod)) {
                throw new ActionEnhancerException("Method marked with @After or @Before should be public and non static: "
                        + invokeMethod.getLongName());
            }

            StringBuilder parameters = resolveParameters(classPool, invokeMethod, body);

            // invoke method with resolved parameters
            body.append("{").append(INSTANCE).append(".")
                    .append(invokeMethod.getName())
                    .append("(").append(parameters).append(");")
                    .append("}");
        }

        method.setBody(body.append("}").toString());
    }

    private static StringBuilder resolveParameters(ClassPool classPool, CtBehavior behavior, StringBuilder body)
            throws ClassNotFoundException, NotFoundException {
        Object[][] annotations = behavior.getParameterAnnotations();
        int idx = 0;
        boolean requestBodyJsonParsed = false;
        StringBuilder parameters = new StringBuilder();
        for (CtClass parameterType : behavior.getParameterTypes()) {
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
            } else if (parameterType.equals(getCtClass(classPool, MODEL))) {
                // ru.frostman.web.controller.Model type resolved as current model
                body.append(MODEL).append(" $param$").append(idx).append(" = mav.getModel();");
            } else if (parameterType.equals(getCtClass(classPool, JAVIN_SESSION))) {
                // ru.frostman.web.session.JavinSession type resolved as current current session
                body.append(JAVIN_SESSION).append(" $param$").append(idx).append(" = " + JAVIN_SESSIONS
                        + ".getSession(request, response);");
            } else if (isAnnotatedWith(annotations[idx], Param.class) != null) {
                // iff annotated with @Param but not String
                if (!parameterType.equals(getCtClass(classPool, JAVA_LANG_STRING))) {
                    throw new ActionEnhancerException("Auto converted method argument type " + parameterType.getName()
                            + " is currently unsupported: " + behavior.getLongName());
                }

                Param paramAnnotation = isAnnotatedWith(annotations[idx], Param.class);
                body.append("String $param$").append(idx).append(" = request.getParameter(\"")
                        .append(paramAnnotation.value()).append("\");");
                if (paramAnnotation.required()) {
                    // append checking parameter for not null
                    body.append("if($param$").append(idx).append(" == null) {" +
                            "throw new " + PARAMETER_REQUIRED_EXCEPTION + "(\"required param: \"+" + idx + ");"
                            + "}");
                }
            } else if (isAnnotatedWith(annotations[idx], JsonParam.class) != null) {
                JsonParam paramAnnotation = isAnnotatedWith(annotations[idx], JsonParam.class);

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
                    // append checking parameter for not null
                    body.append("if($param$").append(idx).append(" == null) {" +
                            "throw new " + PARAMETER_REQUIRED_EXCEPTION + "(\"required param: \"+" + idx + ");"
                            + "}");
                }
            } else if (isAnnotatedWith(annotations[idx], Pjax.class) != null) {
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
            } else {
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

    private static String[] normalizeUrls(String[] urls) {
        for (int i = 0; i < urls.length; i++) {
            urls[i] = Controllers.url(urls[i]);
        }

        return urls;
    }
}

