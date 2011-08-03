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
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import ru.frostman.web.thr.JavinRuntimeException;

import java.util.List;

/**
 * @author slukjanov aka Frostman
 */
public class AopEnhancer {
    private static final String METHOD_INVOCATION = "ru.frostman.web.aop.MethodInvocation";
    private static final String METHOD_INTERCEPTOR = "ru.frostman.web.aop.MethodInterceptor";
    private static final String METHOD_INTERCEPTORS = "ru.frostman.web.aop.MethodInterceptors";

    public static void enhance(ClassPool classPool, CtClass ctClass, List<MethodInterceptor> methodInterceptors) {
        try {
            for (CtMethod method : ctClass.getDeclaredMethods()) {
                if (method.getAnnotation(Interceptor.class) != null) {
                    continue;
                }

                CtMethod wrappedMethod = new CtMethod(method, ctClass, null);
                String wrappedName = "$javin$" + method.getName();
                wrappedMethod.setName(wrappedName);
                ctClass.addMethod(wrappedMethod);

                StringBuilder body = new StringBuilder("{");

                body.append(METHOD_INVOCATION).append(" mi = new ").append(METHOD_INVOCATION).append("(")
                        .append("\"").append(ctClass.getName()).append("\", ");

                if (Modifier.isStatic(method.getModifiers())) {
                    body.append("null");
                } else {
                    body.append("$0");
                }
                body.append(", ");

                body.append("\"").append(method.getName()).append("\", new Class");

                CtClass[] parameterTypes = method.getParameterTypes();

                if (parameterTypes.length != 0) {
                    body.append("[]{");
                    int i = 0;
                    for (CtClass argType : parameterTypes) {
                        body.append(argType.getName()).append(".class");

                        if (i < parameterTypes.length - 1) {
                            body.append(", ");
                        }
                        i++;
                    }
                    body.append("}");
                } else {
                    body.append("[0]");
                }

                body.append(", ");

                body.append("$args, new ").append(METHOD_INTERCEPTOR);

                List<MethodInterceptor> interceptors = Lists.newLinkedList();

                for (MethodInterceptor interceptor : methodInterceptors) {
                    if (interceptor.matches(method)) {
                        interceptors.add(interceptor);
                    }
                }

                if (interceptors.size() != 0) {
                    body.append("[]{");

                    int i = 0;
                    for (MethodInterceptor methodInterceptor : interceptors) {
                        body.append(METHOD_INTERCEPTORS).append(".getInterceptor(\"")
                                .append(methodInterceptor.getLongName()).append("\")");

                        if (i < interceptors.size() - 1) {
                            body.append(", ");
                        }
                        i++;
                    }

                    body.append("}");
                } else {
                    body.append("[0]");
                }

                body.append(");");

                if (!method.getReturnType().equals(CtClass.voidType)) {
                    body.append("return ");
                }
                body.append("($r) mi.proceed();");

                method.setBody(body.append("}").toString());
            }
        } catch (Exception e) {
            throw new JavinRuntimeException(e);
        }
    }
}
