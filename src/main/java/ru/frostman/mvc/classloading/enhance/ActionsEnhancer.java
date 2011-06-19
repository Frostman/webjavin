package ru.frostman.mvc.classloading.enhance;

import javassist.*;
import ru.frostman.mvc.annotation.Action;
import ru.frostman.mvc.classloading.FrostyClass;
import ru.frostman.mvc.util.HttpMethod;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.frostman.mvc.classloading.enhance.EnhancerUtil.getCtClass;
import static ru.frostman.mvc.classloading.enhance.EnhancerUtil.getDeclaredMethodsAnnotatedWith;

/**
 * @author slukjanov aka Frostman
 */
public class ActionsEnhancer {
    private static final AtomicInteger actionMethodsCount = new AtomicInteger(1);

    public static void enhance(Map<String, FrostyClass> classes, ClassPool classPool, CtClass controller) {
        for (CtMethod method : getDeclaredMethodsAnnotatedWith(Action.class, controller)) {
            try {
                Action actionAnnotation = (Action) method.getAnnotation(Action.class);
                String[] urls = actionAnnotation.value();
                HttpMethod[] methods = actionAnnotation.method();
                boolean async = actionAnnotation.async();

                CtClass actionInvoker = generateActionInvoker(classPool, method, async);

                FrostyClass generated = new FrostyClass();
                generated.setName(actionInvoker.getName());
                generated.setEnhancedBytecode(actionInvoker.toBytecode());
                generated.setGenerated(true);

                classes.put(actionInvoker.getName(), generated);
            } catch (Exception e) {
                //todo impl
                throw new RuntimeException(e);
            }
        }
    }

    private static CtClass generateActionInvoker(ClassPool classPool, CtMethod method, boolean async)
            throws CannotCompileException, ClassNotFoundException {
        final CtClass controller = method.getDeclaringClass();
        CtClass actionInvoker = classPool.makeClass(controller.getName() + "$action$" + method.getName()
                + "$" + actionMethodsCount.getAndIncrement());
        actionInvoker.setSuperclass(getCtClass(classPool, "ru.frostman.mvc.dispatch.ActionInvoker"));

        generateActionInvokerConstructor(classPool, actionInvoker);

        generateActionInvokerBefore(classPool, actionInvoker, controller);
        generateActionInvokerAction(classPool, actionInvoker, controller);
        generateActionInvokerAfter(classPool, actionInvoker, controller);

        generateActionInvokerCatchError(classPool, actionInvoker, controller);
        generateActionInvokerIsAsync(classPool, actionInvoker, async);

        return actionInvoker;
    }

    private static void generateActionInvokerConstructor(ClassPool classPool, CtClass actionInvoker)
            throws CannotCompileException {
        CtConstructor constructor = new CtConstructor(new CtClass[]{
                getCtClass(classPool, "javax.servlet.http.HttpServletRequest"),
                getCtClass(classPool, "javax.servlet.http.HttpServletResponse")
        }, actionInvoker);

        constructor.setBody("super($$);");

        actionInvoker.addConstructor(constructor);
    }

    private static void generateActionInvokerBefore(ClassPool classPool, CtClass actionInvoker, CtClass controller)
            throws CannotCompileException {
        CtMethod method = new CtMethod(CtClass.voidType, "before", new CtClass[]{}, actionInvoker);

        method.setBody("{System.out.println(\"before: " + actionInvoker.getName() + "\");}");

        actionInvoker.addMethod(method);
    }

    private static void generateActionInvokerAction(ClassPool classPool, CtClass actionInvoker, CtClass controller)
            throws CannotCompileException {
        CtMethod method = new CtMethod(CtClass.voidType, "action", new CtClass[]{}, actionInvoker);

        method.setBody("{System.out.println(\"action: " + actionInvoker.getName() + "\");}");

        actionInvoker.addMethod(method);
    }

    private static void generateActionInvokerAfter(ClassPool classPool, CtClass actionInvoker, CtClass controller)
            throws CannotCompileException {
        CtMethod method = new CtMethod(CtClass.voidType, "after", new CtClass[]{}, actionInvoker);

        method.setBody("{System.out.println(\"after: " + actionInvoker.getName() + "\");}");

        actionInvoker.addMethod(method);
    }

    private static void generateActionInvokerCatchError(ClassPool classPool, CtClass actionInvoker, CtClass controller)
            throws CannotCompileException {
        CtMethod method = new CtMethod(CtClass.voidType, "catchError",
                new CtClass[]{getCtClass(classPool, "java.lang.Throwable")}, actionInvoker);

        method.setBody("{System.out.println(\"before: " + actionInvoker.getName() + "\");}");

        actionInvoker.addMethod(method);
    }

    private static void generateActionInvokerIsAsync(ClassPool classPool, CtClass actionInvoker, boolean async)
            throws CannotCompileException {
        CtMethod method = new CtMethod(CtClass.booleanType, "isAsync", new CtClass[]{}, actionInvoker);

        method.setBody("return " + async + ";");

        actionInvoker.addMethod(method);
    }
}
