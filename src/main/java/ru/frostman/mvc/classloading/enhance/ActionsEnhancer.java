package ru.frostman.mvc.classloading.enhance;

import com.google.common.collect.Sets;
import javassist.*;
import ru.frostman.mvc.annotation.*;
import ru.frostman.mvc.classloading.FrostyClass;
import ru.frostman.mvc.dispatch.ActionDefinition;
import ru.frostman.mvc.dispatch.url.UrlPatternType;
import ru.frostman.mvc.util.HttpMethod;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.frostman.mvc.classloading.enhance.EnhancerUtil.*;

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
    private static final String VIEW = "ru.frostman.mvc.View";
    private static final String MODEL = "ru.frostman.mvc.Model";
    private static final String MODEL_AND_VIEW = "ru.frostman.mvc.ModelAndView";
    private static final String JAVA_LANG_STRING = "java.lang.String";
    private static final String THROWABLE = "java.lang.Throwable";

    public static void enhance(Map<String, FrostyClass> classes, ClassPool classPool, CtClass controller,
                               List<ActionDefinition> actionDefinitions) {
        for (CtMethod actionMethod : getDeclaredMethodsAnnotatedWith(Action.class, controller)) {
            try {
                Action actionAnnotation = (Action) actionMethod.getAnnotation(Action.class);
                String[] urls = actionAnnotation.value();
                HttpMethod[] methods = actionAnnotation.method();
                boolean async = actionAnnotation.async();

                if (CtClass.voidType == actionMethod.getReturnType()) {
                    //todo impl
                    throw new RuntimeException();
                }

                CtClass actionInvoker = generateActionInvoker(classPool, actionMethod, async);

                FrostyClass generated = new FrostyClass();
                generated.setName(actionInvoker.getName());
                generated.setEnhancedBytecode(actionInvoker.toBytecode());
                generated.setGenerated(true);

                classes.put(actionInvoker.getName(), generated);

                for (String url : urls) {
                    actionDefinitions.add(new ActionDefinition(UrlPatternType.get(url, UrlPatternType.SERVLET,
                            Sets.newHashSet(methods)), actionInvoker.getName()));
                }
            } catch (Exception e) {
                //todo impl
                throw new RuntimeException(e);
            }
        }
    }

    private static CtClass generateActionInvoker(ClassPool classPool, CtMethod actionMethod, boolean async)
            throws CannotCompileException, ClassNotFoundException, NotFoundException {

        final CtClass controller = actionMethod.getDeclaringClass();

        CtClass actionInvoker = classPool.makeClass(controller.getName() + "$action$" + actionMethod.getName()
                + "$" + actionMethodsCount.getAndIncrement());
        actionInvoker.setSuperclass(getCtClass(classPool, "ru.frostman.mvc.dispatch.ActionInvoker"));

        CtField instanceField = new CtField(controller, "$instance", actionInvoker);
        actionInvoker.addField(instanceField);

        generateActionInvokerConstructor(classPool, actionInvoker, controller);

        generateActionInvokerBefore(classPool, actionInvoker, controller);
        generateActionInvokerAction(classPool, actionInvoker, controller, actionMethod);
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
        body.append("{super($$);");

        CtConstructor[] constructors= controller.getConstructors();
        if(constructors.length!=1) {
            //todo impl
            throw new RuntimeException("Only one constructor must be in controller");
        }

        StringBuilder parameters = resolveParameters(classPool, constructors[0], body);
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

    private static void generateActionInvokerAction(ClassPool classPool, CtClass actionInvoker, CtClass controller,
                                                    CtMethod actionMethod)
            throws CannotCompileException, NotFoundException, ClassNotFoundException {

        if (!isPublicAndNonStatic(actionMethod)) {
            //todo impl
            throw new RuntimeException();
        }

        CtMethod method = new CtMethod(CtClass.voidType, "action", new CtClass[]{}, actionInvoker);

        StringBuilder body = new StringBuilder("{");
        StringBuilder parameters = resolveParameters(classPool, actionMethod, body);

        body.append("try{");
        body.append("Object result = ").append(INSTANCE).append(".")
                .append(actionMethod.getName()).append("(").append(parameters).append(");");

        //todo add comments for each generated line of code
        CtClass returnType = actionMethod.getReturnType();
        if (returnType.equals(getCtClass(classPool, VIEW))) {
            body.append("mav.setView((").append(VIEW).append(") result").append(");");
        } else if (returnType.equals(getCtClass(classPool, MODEL_AND_VIEW))) {
            body.append("mav = (").append(MODEL_AND_VIEW).append(") result;");
        } else if (returnType.equals(getCtClass(classPool, JAVA_LANG_STRING))) {
            body.append("mav.setView(ru.frostman.mvc.Frosty.getViews().getViewByName((")
                    .append(JAVA_LANG_STRING).append(") result").append("));");
        } else {
            //todo impl
            throw new RuntimeException();
        }

        body.append("}catch(Throwable th){throw new ru.frostman.mvc.dispatch.ActionException(th);}");

        method.setBody(body.append("}").toString());

        //todo print to trace action specification for each action method

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
            //todo think about this
            //todo impl
            throw new RuntimeException();
        }

        StringBuilder body = new StringBuilder("{");
        for (CtMethod invokeMethod : methods) {
            if (invokeMethod.getReturnType() != CtClass.voidType) {
                //todo impl
                throw new RuntimeException("!=void type");
            } else if (!isPublicAndNonStatic(invokeMethod)) {
                //todo impl
                throw new RuntimeException("static || non public");
            } else if (invokeMethod.getParameterTypes().length < 1 ||
                    (!invokeMethod.getParameterTypes()[0].getName().equals(THROWABLE))) {
                //todo impl
                throw new RuntimeException("First arg isn't throwable");
            }


            StringBuilder parameters = resolveParameters(classPool, invokeMethod, body);

            body.append("{").append(INSTANCE).append(".")
                    .append(invokeMethod.getName())
                    .append("($1,").append(parameters).append(");")
                    .append("}");
        }

        if (methods.size() == 0) {
            //todo think about this
            body.append("{throw new RuntimeException($1);}");
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
                //todo impl
                throw new RuntimeException("!=void type");
            } else if (!isPublicAndNonStatic(invokeMethod)) {
                //todo impl
                throw new RuntimeException("static || non public");
            }

            StringBuilder parameters = resolveParameters(classPool, invokeMethod, body);

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
        StringBuilder parameters = new StringBuilder();
        for (CtClass parameterType : behavior.getParameterTypes()) {
            //todo add comments for each generated line of code
            if (parameterType.equals(getCtClass(classPool, HTTP_SERVLET_REQUEST))) {
                body.append(HTTP_SERVLET_REQUEST).append(" $param$").append(idx).append(" = request;");
            } else if (parameterType.equals(getCtClass(classPool, SERVLET_REQUEST))) {
                body.append(SERVLET_REQUEST).append(" $param$").append(idx).append(" = request;");
            } else if (parameterType.equals(getCtClass(classPool, HTTP_SERVLET_RESPONSE))) {
                body.append(HTTP_SERVLET_RESPONSE).append(" $param$").append(idx).append(" = response;");
            } else if (parameterType.equals(getCtClass(classPool, SERVLET_RESPONSE))) {
                body.append(SERVLET_RESPONSE).append(" $param$").append(idx).append(" = response;");
            } else if (parameterType.equals(getCtClass(classPool, ASYNC_CONTEXT))) {
                body.append(ASYNC_CONTEXT).append(" $param$").append(idx).append(" = asyncContext;");
            } else if (parameterType.equals(getCtClass(classPool, MODEL))) {
                body.append(MODEL).append(" $param$").append(idx).append(" = mav.getModel();");
            } else if (isAnnotatedWith(annotations[idx], Param.class) != null) {
                if (!parameterType.equals(getCtClass(classPool, "java.lang.String"))) {
                    //todo impl auto converting or exception
                    //todo impl
                    throw new RuntimeException("Currently unsupported");
                }

                Param paramAnnotation = isAnnotatedWith(annotations[idx], Param.class);
                body.append("String $param$").append(idx).append(" = request.getParameter(\"")
                        .append(paramAnnotation.value()).append("\");");
                if (paramAnnotation.required()) {
                    body.append("if($param$").append(idx).append(" == null) {" +
                            //todo impl new exception
                            "throw new RuntimeException(\"required\");"
                            + "}");
                }
            } else {
                throw new RuntimeException("Unsupported parameter type!! (and not marked as @Param)");
            }

            parameters.append("$param$").append(idx);
            if (idx < annotations.length - 1) {
                parameters.append(",");
            }
            idx++;
        }
        return parameters;
    }

    private static boolean isPublicAndNonStatic(CtMethod method) {
        int mod = method.getModifiers();

        return Modifier.isPublic(mod) && (!Modifier.isStatic(mod));
    }
}

