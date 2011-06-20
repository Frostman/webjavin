package ru.frostman.mvc.classloading.enhance;

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
public class ActionsEnhancer {
    private static final AtomicInteger actionMethodsCount = new AtomicInteger(1);
    private static final String INSTANCE = "$instance";

    private static final String HTTP_SERVLET_REQUEST = "javax.servlet.http.HttpServletRequest";
    private static final String SERVLET_REQUEST = "javax.servlet.ServletRequest";
    private static final String HTTP_SERVLET_RESPONSE = "javax.servlet.http.HttpServletResponse";
    private static final String SERVLET_RESPONSE = "javax.servlet.ServletResponse";
    private static final String ASYNC_CONTEXT = "javax.servlet.AsyncContext";

    public static void enhance(Map<String, FrostyClass> classes, ClassPool classPool, CtClass controller,
                               List<ActionDefinition> actionDefinitions) {
        for (CtMethod actionMethod : getDeclaredMethodsAnnotatedWith(Action.class, controller)) {
            try {
                Action actionAnnotation = (Action) actionMethod.getAnnotation(Action.class);
                String[] urls = actionAnnotation.value();
                HttpMethod[] methods = actionAnnotation.method();
                boolean async = actionAnnotation.async();

                CtClass actionInvoker = generateActionInvoker(classPool, actionMethod, async);

                FrostyClass generated = new FrostyClass();
                generated.setName(actionInvoker.getName());
                generated.setEnhancedBytecode(actionInvoker.toBytecode());
                generated.setGenerated(true);

                classes.put(actionInvoker.getName(), generated);

                for (String url : urls) {
                    //todo impl http methods checking
                    actionDefinitions.add(new ActionDefinition(UrlPatternType.get(url, UrlPatternType.SERVLET),
                            actionInvoker.getName()));
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

        //todo check that all methods are public non static
        generateActionInvokerBefore(classPool, actionInvoker, controller);
        generateActionInvokerAction(classPool, actionInvoker, controller, actionMethod);
        generateActionInvokerAfter(classPool, actionInvoker, controller);

        generateActionInvokerCatchError(classPool, actionInvoker, controller);
        generateActionInvokerIsAsync(classPool, actionInvoker, async);

        return actionInvoker;
    }

    private static void generateActionInvokerConstructor(ClassPool classPool, CtClass actionInvoker, CtClass controller)
            throws CannotCompileException {
        CtConstructor constructor = new CtConstructor(new CtClass[]{
                getCtClass(classPool, "javax.servlet.http.HttpServletRequest"),
                getCtClass(classPool, "javax.servlet.http.HttpServletResponse")
        }, actionInvoker);

        StringBuilder body = new StringBuilder();
        body.append("{super($$);");

        //todo impl constructor parameters injection
        body.append(INSTANCE).append(" = new ").append(controller.getName()).append("();}");

        constructor.setBody(body.toString());

        actionInvoker.addConstructor(constructor);
    }

    private static void generateActionInvokerBefore(ClassPool classPool, CtClass actionInvoker, CtClass controller)
            throws CannotCompileException, NotFoundException {
        CtMethod method = new CtMethod(CtClass.voidType, "before", new CtClass[]{}, actionInvoker);

        List<CtMethod> methodList = getMethodsAnnotatedWith(Before.class, controller);

        generateActionInvokerMethodInvocations(method, methodList);

        actionInvoker.addMethod(method);
    }

    private static void generateActionInvokerAction(ClassPool classPool, CtClass actionInvoker, CtClass controller,
                                                    CtMethod actionMethod)
            throws CannotCompileException, NotFoundException, ClassNotFoundException {
        CtMethod method = new CtMethod(CtClass.voidType, "action", new CtClass[]{}, actionInvoker);

        StringBuilder body = new StringBuilder("{");

        Object[][] annotations = actionMethod.getParameterAnnotations();
        int idx = 0;
        StringBuilder parameters = new StringBuilder();
        for (CtClass parameterType : actionMethod.getParameterTypes()) {
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
            } else if (isAnnotatedWith(annotations[idx], Param.class) != null) {
                if (!parameterType.equals(getCtClass(classPool, "java.lang.String"))) {
                    //todo impl auto converting or exception
                }

                Param paramAnnotation = isAnnotatedWith(annotations[idx], Param.class);
                //todo check iff required
                body.append("String $param$").append(idx).append(" = request.getParameter(\"")
                        .append(paramAnnotation.value()).append("\");");
            } else {
                //todo unsupported param, check that this param required
                throw new RuntimeException("Unsupported parameter type!!");
            }

            parameters.append("$param$").append(idx);
            if (idx < annotations.length - 1) {
                parameters.append(",");
            }
            idx++;
        }

        body.append("try{");
        body.append(INSTANCE).append(".").append(actionMethod.getName()).append("(").append(parameters).append(");");
        body.append("}catch(Throwable th){throw new ru.frostman.mvc.dispatch.ActionException(th);}");

        method.setBody(body.append("}").toString());

        actionInvoker.addMethod(method);
    }

    private static void generateActionInvokerAfter(ClassPool classPool, CtClass actionInvoker, CtClass controller)
            throws CannotCompileException, NotFoundException {
        CtMethod method = new CtMethod(CtClass.voidType, "after", new CtClass[]{}, actionInvoker);

        List<CtMethod> methodList = getMethodsAnnotatedWith(After.class, controller);

        generateActionInvokerMethodInvocations(method, methodList);

        actionInvoker.addMethod(method);
    }

    private static void generateActionInvokerCatchError(ClassPool classPool, CtClass actionInvoker, CtClass controller)
            throws CannotCompileException, NotFoundException {
        CtMethod method = new CtMethod(CtClass.voidType, "catchError",
                new CtClass[]{getCtClass(classPool, "java.lang.Throwable")}, actionInvoker);

        List<CtMethod> methods = getMethodsAnnotatedWith(Catch.class, controller);

        StringBuilder body = new StringBuilder("{");
        for (CtMethod invokeMethod : methods) {
            if (invokeMethod.getReturnType() != CtClass.voidType) {
                //todo impl
                throw new RuntimeException("!=void type");
            }

            //todo add parameters resolving
            body.append("{").append(INSTANCE).append(".")
                    .append(invokeMethod.getName())
                    .append("($1);")
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

    private static void generateActionInvokerMethodInvocations(CtMethod method, List<CtMethod> methods)
            throws NotFoundException, CannotCompileException {
        StringBuilder body = new StringBuilder("{");
        for (CtMethod invokeMethod : methods) {
            if (invokeMethod.getReturnType() != CtClass.voidType) {
                //todo impl
                throw new RuntimeException("!=void type");
            }

            //todo add parameters resolving
            body.append("{").append(INSTANCE).append(".")
                    .append(invokeMethod.getName())
                    .append("();")
                    .append("}");
        }

        method.setBody(body.append("}").toString());
    }
}
