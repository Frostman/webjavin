package ru.frostman.mvc.aop;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import ru.frostman.mvc.thr.FrostyRuntimeException;

import java.util.List;

/**
 * @author slukjanov aka Frostman
 */
public class AopEnhancer {
    public static void enhance(ClassPool classPool, CtClass ctClass, List<MethodWrapper> methodWrappers) {
        try {
            //todo make AOP like filters in servlet-api or see AOP libs like guice

            ctClass.instrument(new ExprEditor() {
                @Override
                public void edit(MethodCall methodCall) throws CannotCompileException {
                    //todo check package, className, methodName, param types == SIGNATURE

                    if (!methodCall.getMethodName().equals("test")) {
                        return;
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append("");

                    methodCall.replace(sb.toString());
                }
            });
        } catch (Exception e) {
            //todo impl
            throw new FrostyRuntimeException(e);
        }
    }
}
