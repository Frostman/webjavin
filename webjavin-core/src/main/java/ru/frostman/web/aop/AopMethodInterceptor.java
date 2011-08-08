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

import javassist.CtMethod;
import ru.frostman.web.aop.thr.AopException;

import java.lang.reflect.Method;

/**
 * @author slukjanov aka Frostman
 */
public class AopMethodInterceptor implements MethodInterceptor {
    protected final String interceptorClassName;
    protected final String interceptorMethodName;
    protected final String methodPattern;

    protected final String longName;

    private Method wrapperMethod;

    public AopMethodInterceptor(String interceptorClassName, String interceptorMethodName, String methodPattern, String longName) {
        this.interceptorClassName = interceptorClassName;
        this.interceptorMethodName = interceptorMethodName;
        this.methodPattern = methodPattern;
        this.longName = longName;
    }

    private Method getWrapperMethod() {
        if (wrapperMethod == null) {
            try {
                Class<?> wrapperClass = Thread.currentThread().getContextClassLoader().loadClass(interceptorClassName);
                Method wrapper = wrapperClass.getDeclaredMethod(interceptorMethodName, MethodInvocation.class);
                wrapper.setAccessible(true);

                wrapperMethod = wrapper;
            } catch (Exception e) {
                throw new AopException("Exception while searching for wrapper method: " + interceptorClassName + "#"
                        + interceptorMethodName, e);
            }
        }

        return wrapperMethod;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) {
        try {
            return getWrapperMethod().invoke(null, methodInvocation);
        } catch (Exception e) {
            throw new AopException("Exception while invoking method interceptor: "
                    + interceptorClassName + "#" + interceptorMethodName, e);
        }
    }

    @Override
    public boolean matches(CtMethod method) {
        //todo impl / currently only method name checked
        //todo сделать MVEL выражение для фильтрации методов чтобы поддерживать ещё и аннотации и тд
        //todo выполнять это выражение лишь один раз при enhancing

        return methodPattern.length() == 0 || method.getName().equals(methodPattern);
    }

    @Override
    public String getName() {
        return longName;
    }
}
