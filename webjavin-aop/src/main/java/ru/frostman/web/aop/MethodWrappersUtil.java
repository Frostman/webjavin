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

package ru.frostman.web.aop;

import com.google.common.collect.Lists;
import javassist.CtClass;
import javassist.CtMethod;
import ru.frostman.web.annotation.Wrapper;
import ru.frostman.web.aop.thr.AopException;
import ru.frostman.web.classloading.AppClass;
import ru.frostman.web.classloading.enhance.EnhancerUtil;
import ru.frostman.web.thr.JavinRuntimeException;

import java.util.List;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class MethodWrappersUtil {
    public static List<MethodWrapper> findWrappers(Map<String, AppClass> classes) {
        try {
            List<MethodWrapper> methodWrappers = Lists.newLinkedList();

            for (Map.Entry<String, AppClass> entry : classes.entrySet()) {
                CtClass ctClass = entry.getValue().getCtClass();

                for (CtMethod method : EnhancerUtil.getDeclaredMethodsAnnotatedWith(Wrapper.class, ctClass)) {
                    if (!EnhancerUtil.isPublicAndStatic(method)) {
                        //todo think about static
                        throw new AopException("Wrapper method should be public and static");
                    }

                    if (!method.getReturnType().getName().equals("java.lang.Object")) {
                        throw new AopException("Wrapper method should return Object: " + method.getLongName());
                    }

                    CtClass[] parameterTypes = method.getParameterTypes();
                    if (!parameterTypes[0].getName().equals("java.lang.String")
                            || !parameterTypes[1].getName().equals("java.lang.Object")
                            || !parameterTypes[2].getName().equals("java.lang.reflect.Method")
                            || !parameterTypes[3].isArray() || parameterTypes[3].getName().equals("java.lang.Object")) {
                        throw new AopException("Wrapper method should have correct signature: " + method.getLongName());
                    }

                    //todo check body

                    //todo add to list
                    //todo method signature: public static Object wrap(String className, Object instance, Method method, Object[] params);

                    Wrapper wrapperAnn = (Wrapper) method.getAnnotation(Wrapper.class);
                    methodWrappers.add(new MethodWrapper(ctClass.getName(), method.getName(), wrapperAnn.value()));
                }
            }

            return methodWrappers;
        } catch (Exception e) {
            throw new JavinRuntimeException("Exception while searching method wrappers", e);
        }
    }
}
