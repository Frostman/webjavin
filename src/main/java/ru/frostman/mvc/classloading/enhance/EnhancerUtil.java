/******************************************************************************
 * Frosty - MVC framework.                                                    *
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

package ru.frostman.mvc.classloading.enhance;

import com.google.common.collect.Lists;
import javassist.*;
import ru.frostman.mvc.thr.BytecodeManipulationException;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author slukjanov aka Frostman
 */
public class EnhancerUtil {

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

    public static CtClass getCtClass(ClassPool classPool, String className) throws BytecodeManipulationException {
        try {
            return classPool.get(className);
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

    /**
     * Find all non-private inherited methods annotated with specified annotation
     * and any methods in current class annotated with specified annotation.
     *
     * @param annotation to filter methods
     * @param ctClass    class where to search methods
     *
     * @return list of annotated methods including inherited
     *
     * @throws ru.frostman.mvc.thr.BytecodeManipulationException
     *          iff any error
     */
    public static List<CtMethod> getMethodsAnnotatedWith(Class<? extends Annotation> annotation
            , CtClass ctClass) throws BytecodeManipulationException {
        try {
            List<CtMethod> annotated = Lists.newLinkedList();

            for (CtMethod method : ctClass.getMethods()) {
                if (method.getAnnotation(annotation) != null) {
                    annotated.add(method);
                }
            }

            for (CtMethod method : ctClass.getDeclaredMethods()) {
                if (method.getAnnotation(annotation) != null && (!annotated.contains(method))) {
                    annotated.add(method);
                }
            }

            return annotated;
        } catch (Exception e) {
            throw new BytecodeManipulationException("Error in filtering methods of class", e);
        }
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Annotation> T isAnnotatedWith(Object[] annotations, Class<T> expected) {
        for (Object annotation : annotations) {
            if (((Annotation) annotation).annotationType() == expected) {
                return (T) annotation;
            }
        }

        return null;
    }

    public static boolean isPublicAndNonStatic(CtMethod method) {
        int mod = method.getModifiers();

        return Modifier.isPublic(mod) && (!Modifier.isStatic(mod));
    }

    public static boolean isPublicAndStatic(CtMethod method) {
        int mod = method.getModifiers();

        return Modifier.isPublic(mod) && Modifier.isStatic(mod);
    }

}
