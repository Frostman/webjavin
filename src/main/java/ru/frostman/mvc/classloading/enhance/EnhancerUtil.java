package ru.frostman.mvc.classloading.enhance;

import com.google.common.collect.Lists;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author slukjanov aka Frostman
 */
public class EnhancerUtil {

    //todo add Preconditions to check states and params

    public static CtClass createCtClass(ClassPool classPool, String superClassName, String genClassName
            , String... interfaces) throws BytecodeManipulationException {

        CtClass superClass;
        superClass = getCtClass(classPool, superClassName);

        CtClass genCtClass = classPool.makeClass(genClassName);
        try {
            genCtClass.setSuperclass(superClass);
        } catch (CannotCompileException e) {
            throw new BytecodeManipulationException("Error while setting superclass for generated class", e);
        }

        if (interfaces.length > 0) {
            try {
                genCtClass.setInterfaces(classPool.get(interfaces));
            } catch (Exception e) {
                throw new BytecodeManipulationException("Error while setting interfaces for generated class", e);
            }
        }

        return genCtClass;
    }

    public static CtClass getCtClass(ClassPool classPool, String superClassName) throws BytecodeManipulationException {
        try {
            return classPool.get(superClassName);
        } catch (Exception e) {
            throw new BytecodeManipulationException("Error in loading expected class", e);
        }
    }

    public static List<CtMethod> getDeclaredMethodsAnnotatedWith(Class<? extends Annotation> annotation
            , CtClass ctClass) throws BytecodeManipulationException {
        try {
            List<CtMethod> annotated = Lists.newLinkedList();

            for (CtMethod method : ctClass.getDeclaredMethods()) {
                if (method.getAnnotation(annotation) != null) {
                    annotated.add(method);
                }
            }

            return annotated;
        } catch (Exception e) {
            throw new BytecodeManipulationException("Error in filtering methods of class", e);
        }
    }

    public static class BytecodeManipulationException extends RuntimeException {
        public BytecodeManipulationException() {
        }

        public BytecodeManipulationException(String message) {
            super(message);
        }

        public BytecodeManipulationException(String message, Throwable cause) {
            super(message, cause);
        }

        public BytecodeManipulationException(Throwable cause) {
            super(cause);
        }
    }
}
