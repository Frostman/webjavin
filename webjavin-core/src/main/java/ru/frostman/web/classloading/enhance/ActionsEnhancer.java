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

import static ru.frostman.web.classloading.enhance.ClassConstants.*;
import static ru.frostman.web.classloading.enhance.Enhancer.classPool;
import static ru.frostman.web.classloading.enhance.EnhancerUtil.*;

/**
 * @author slukjanov aka Frostman
 */
class ActionsEnhancer {
    private static final AtomicInteger actionMethodsCount = new AtomicInteger(1);
    private static final String INSTANCE = "$instance";

    public static void enhance(Map<String, AppClass> classes, CtClass controller,
                               List<ActionDefinition> actionDefinitions) {
        for (CtMethod actionMethod : getDeclaredMethodsAnnotatedWith(Action.class, controller)) {
            try {
                String urlPrefix = extractUrlPrefix(controller);

                Action actionAnnotation = (Action) actionMethod.getAnnotation(Action.class);
                String[] urls = normalizeUrls(actionAnnotation.value(), urlPrefix);
                HttpMethod[] methods = actionAnnotation.method();
                boolean async = actionAnnotation.async();

                if (CtClass.voidType == actionMethod.getReturnType()) {
                    throw new ActionEnhancerException("Action method should return some value (not void method): "
                            + actionMethod.getLongName());
                }

                CtClass actionInvoker = generateActionInvoker(actionMethod, async);

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

    private static String extractUrlPrefix(CtClass controller) throws ClassNotFoundException {
        Controller controllerAnn = (Controller) controller.getAnnotation(Controller.class);
        String urlPrefix = "";
        if (controllerAnn != null) {
            urlPrefix = controllerAnn.value();
        }
        return urlPrefix;
    }

    private static CtClass generateActionInvoker(CtMethod actionMethod, boolean async)
            throws CannotCompileException, ClassNotFoundException, NotFoundException {

        final CtClass controller = actionMethod.getDeclaringClass();

        CtClass actionInvoker = classPool.makeClass(controller.getName() + "$action$" + actionMethod.getName()
                + "$" + actionMethodsCount.getAndIncrement());
        actionInvoker.setSuperclass(getCtClass("ru.frostman.web.dispatch.ActionInvoker"));

        CtField instanceField = new CtField(controller, "$instance", actionInvoker);
        actionInvoker.addField(instanceField);

        generateActionInvokerConstructor(actionInvoker, controller);

        generateActionInvokerBefore(actionInvoker, controller);
        generateActionInvokerAction(actionInvoker, actionMethod);
        generateActionInvokerAfter(actionInvoker, controller);

        generateActionInvokerCatchError(actionInvoker, controller);
        generateActionInvokerIsAsync(actionInvoker, async);

        return actionInvoker;
    }

    private static void generateActionInvokerConstructor(CtClass actionInvoker, CtClass controller)
            throws CannotCompileException, ClassNotFoundException, NotFoundException {
        CtConstructor constructor = new CtConstructor(new CtClass[]{
                getCtClass("javax.servlet.http.HttpServletRequest"),
                getCtClass("javax.servlet.http.HttpServletResponse")
        }, actionInvoker);

        StringBuilder body = new StringBuilder();
        // invoke super class constructor
        body.append("{super($$);");

        CtConstructor[] constructors = controller.getConstructors();
        if (constructors.length != 1) {
            throw new ActionEnhancerException("Only one constructor should be in controller: " + controller.getName());
        }

        StringBuilder parameters = InjectEnhancer.resolveParameters(constructors[0], body);
        // instantiate controller class (with resolved parameters)
        body.append(INSTANCE).append(" = new ").append(controller.getName()).append("(").append(parameters).append(");}");

        constructor.setBody(body.toString());

        actionInvoker.addConstructor(constructor);
    }

    private static void generateActionInvokerBefore(CtClass actionInvoker, CtClass controller)
            throws CannotCompileException, NotFoundException, ClassNotFoundException {
        CtMethod method = new CtMethod(CtClass.voidType, "before", new CtClass[]{}, actionInvoker);

        List<CtMethod> methodList = getMethodsAnnotatedWith(Before.class, controller);

        generateActionInvokerMethodInvocations(method, methodList);

        actionInvoker.addMethod(method);
    }

    private static void generateActionInvokerAction(CtClass actionInvoker, CtMethod actionMethod)
            throws CannotCompileException, NotFoundException, ClassNotFoundException {

        if (!isPublicAndNonStatic(actionMethod)) {
            throw new ActionEnhancerException("Action method should be public and non static: " + actionMethod.getLongName());
        }

        CtMethod method = new CtMethod(CtClass.voidType, "action", new CtClass[]{}, actionInvoker);

        StringBuilder body = new StringBuilder("{");
        StringBuilder parameters = InjectEnhancer.resolveParameters(actionMethod, body);

        if (actionMethod.getAnnotation(CsrfProtected.class) != null) {
            body.append("if(!" + CSRF_PROTECTOR + ".checkToken(request, response)) { throw new "
                    + CSRF_TOKEN_EXCEPTION + "(\"CSRF protection token is not valid\"); }");
        }

        // invoke action method in controller (with resolved parameters)
        body.append("try{ Object result = ").append(INSTANCE).append(".")
                .append(actionMethod.getName()).append("(").append(parameters).append(");");

        CtClass returnType = actionMethod.getReturnType();
        if (returnType.equals(getCtClass(VIEW))) {
            // iff return type is View then change current ModelAndView's view
            body.append("mav.setView((").append(VIEW).append(") result").append(");");
        } else if (returnType.equals(getCtClass(MODEL_AND_VIEW))) {
            // iff return type is ModelAndView then change current ModelAndView
            body.append("mav = (").append(MODEL_AND_VIEW).append(") result;");
        } else if (returnType.equals(getCtClass(JAVA_LANG_STRING))) {
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

    private static void generateActionInvokerAfter(CtClass actionInvoker, CtClass controller)
            throws CannotCompileException, NotFoundException, ClassNotFoundException {
        CtMethod method = new CtMethod(CtClass.voidType, "after", new CtClass[]{}, actionInvoker);

        List<CtMethod> methodList = getMethodsAnnotatedWith(After.class, controller);

        generateActionInvokerMethodInvocations(method, methodList);

        actionInvoker.addMethod(method);
    }

    private static void generateActionInvokerCatchError(CtClass actionInvoker, CtClass controller)
            throws CannotCompileException, NotFoundException, ClassNotFoundException {
        CtMethod method = new CtMethod(CtClass.voidType, "catchError",
                new CtClass[]{getCtClass("java.lang.Throwable")}, actionInvoker);

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

            StringBuilder parameters = InjectEnhancer.resolveParameters(invokeMethod, body);

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

    private static void generateActionInvokerIsAsync(CtClass actionInvoker, boolean async)
            throws CannotCompileException {
        CtMethod method = new CtMethod(CtClass.booleanType, "isAsync", new CtClass[]{}, actionInvoker);

        method.setBody("return " + async + ";");

        actionInvoker.addMethod(method);
    }

    private static void generateActionInvokerMethodInvocations(CtMethod method, List<CtMethod> methods)
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

            StringBuilder parameters = InjectEnhancer.resolveParameters(invokeMethod, body);

            // invoke method with resolved parameters
            body.append("{").append(INSTANCE).append(".")
                    .append(invokeMethod.getName())
                    .append("(").append(parameters).append(");")
                    .append("}");
        }

        method.setBody(body.append("}").toString());
    }

    private static String[] normalizeUrls(String[] urls, String urlPrefix) {
        for (int i = 0; i < urls.length; i++) {
            urls[i] = Controllers.url(urlPrefix + urls[i]);
        }

        return urls;
    }
}

