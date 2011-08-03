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

package ru.frostman.web.cache;

import javassist.CtMethod;
import ru.frostman.web.annotation.CacheEvict;
import ru.frostman.web.aop.MethodInterceptor;
import ru.frostman.web.aop.MethodInvocation;
import ru.frostman.web.thr.JavinRuntimeException;

/**
 * @author slukjanov aka Frostman
 */
public class CacheEvictMethodInterceptor extends MethodInterceptor {

    public CacheEvictMethodInterceptor() {
        super("webjavin-cache-evict-method-interceptor");
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) {
        return cacheEvict(methodInvocation);
    }

    @Override
    public boolean matches(CtMethod method) {
        try {
            return method.getAnnotation(CacheEvict.class) != null;
        } catch (ClassNotFoundException e) {
            throw new JavinRuntimeException("Can't load class while matching interceptor for @CacheEvict", e);
        }
    }

    protected static Object cacheEvict(MethodInvocation methodInvocation) {
        CacheEvict cacheEvictAnn = methodInvocation.getMethod().getAnnotation(CacheEvict.class);

        //todo impl
        return methodInvocation.proceed();
    }
}
