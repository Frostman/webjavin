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
import com.google.common.collect.Maps;
import javassist.CtClass;
import javassist.CtMethod;
import ru.frostman.web.aop.thr.AopException;
import ru.frostman.web.classloading.AppClass;
import ru.frostman.web.classloading.enhance.EnhancerUtil;
import ru.frostman.web.plugin.JavinPlugins;
import ru.frostman.web.thr.JavinRuntimeException;

import java.util.List;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class MethodInterceptors {

    private static final String METHOD_INVOCATION = "ru.frostman.web.aop.MethodInvocation";

    private static final Map<String, MethodInterceptor> interceptorsCache = Maps.newHashMap();

    public static List<MethodInterceptor> findInterceptors(Map<String, AppClass> classes) {
        try {
            interceptorsCache.clear();

            List<MethodInterceptor> methodInterceptors = Lists.newLinkedList();

            for (Map.Entry<String, AppClass> entry : classes.entrySet()) {
                CtClass ctClass = entry.getValue().getCtClass();

                for (CtMethod method : EnhancerUtil.getDeclaredMethodsAnnotatedWith(Interceptor.class, ctClass)) {
                    if (EnhancerUtil.isNonPublicAndNonStatic(method)) {
                        throw new AopException("Interceptor method should be public and static");
                    }

                    String longName = method.getLongName();
                    if (!method.getReturnType().getName().equals("java.lang.Object")) {
                        throw new AopException("Interceptor method should be void: " + longName);
                    }

                    CtClass[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length != 1 || !parameterTypes[0].getName().equals(METHOD_INVOCATION)) {
                        throw new AopException("Interceptor method should have correct signature: " + longName);
                    }

                    Interceptor interceptorAnn = (Interceptor) method.getAnnotation(Interceptor.class);

                    AopMethodInterceptor interceptor = new AopMethodInterceptor(ctClass.getName(), method.getName()
                            , interceptorAnn.value(), longName);
                    interceptorsCache.put(longName, interceptor);

                    methodInterceptors.add(interceptor);
                }
            }

            for (MethodInterceptor methodInterceptor : JavinPlugins.get().getMethodInterceptors()) {
                interceptorsCache.put(methodInterceptor.getName(), methodInterceptor);
                methodInterceptors.add(methodInterceptor);
            }

            return methodInterceptors;
        } catch (Exception e) {
            throw new JavinRuntimeException("Exception while searching method wrappers", e);
        }
    }

    public static MethodInterceptor getInterceptor(String name) {
        return interceptorsCache.get(name);
    }
}
