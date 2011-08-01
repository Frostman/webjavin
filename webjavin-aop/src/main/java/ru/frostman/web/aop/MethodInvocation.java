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
import ru.frostman.web.aop.thr.AopException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * @author slukjanov aka Frostman
 */
public class MethodInvocation {
    private String className;
    private Class<?> targetClass;

    private Object thisObject;

    private String methodName;
    private Class<?>[] argTypes;

    private Method method;
    private Method targetMethod;

    private Object[] args;

    private LinkedList<MethodInterceptor> interceptors = Lists.newLinkedList();

    public MethodInvocation(String className, Object thisObject, String methodName, Class<?>[] argTypes,
                            Object[] args, MethodInterceptor... interceptors) {
        this.className = className;
        this.thisObject = thisObject;
        this.methodName = methodName;
        this.argTypes = argTypes;
        this.args = args;

        for (MethodInterceptor interceptor : interceptors) {
            this.interceptors.addLast(interceptor);
        }
    }

    /**
     * Proceeds to the next interceptor in the chain
     *
     * @return
     */
    public Object proceed() {
        MethodInterceptor interceptor;
        try {
            interceptor = interceptors.removeFirst();
        } catch (NoSuchElementException ex) {
            try {
                return getTargetMethod().invoke(getThis(), getArgs());
            } catch (Exception e) {
                throw new AopException("Exception while invoking target method: "
                        + getClassName() + "#" + getMethodName(), e);
            }
        }

        return interceptor.invoke(this);
    }

    private String getClassName() {
        return className;
    }

    public Class getTargetClass() {
        if (targetClass == null) {
            if (thisObject != null) {
                targetClass = thisObject.getClass();
            } else {
                try {
                    targetClass = Thread.currentThread().getContextClassLoader().loadClass(className);
                } catch (ClassNotFoundException e) {
                    throw new AopException("Exception while loading target class: " + className, e);
                }
            }
        }

        return targetClass;
    }

    public Object getThis() {
        return thisObject;
    }

    private String getMethodName() {
        return methodName;
    }

    public Class<?>[] getArgTypes() {
        return argTypes;
    }

    public Method getMethod() {
        if (method == null) {
            method = getMethodByName(getMethodName());
        }

        return method;
    }

    private Method getTargetMethod() {
        if (targetMethod == null) {
            targetMethod = getMethodByName("$javin$" + getMethodName());
        }

        return targetMethod;
    }


    private Method getMethodByName(String methodName) {
        try {
            Method method = getTargetClass().getDeclaredMethod(methodName, getArgTypes());
            method.setAccessible(true);

            return method;
        } catch (NoSuchMethodException e) {
            throw new AopException("Method not found: " + getMethodName() + " " + Arrays.toString(getArgTypes()), e);
        }
    }

    public Object[] getArgs() {
        return args;
    }
}
