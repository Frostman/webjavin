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
import ru.frostman.web.annotation.Cacheable;
import ru.frostman.web.aop.MethodInterceptor;
import ru.frostman.web.aop.MethodInvocation;
import ru.frostman.web.thr.JavinRuntimeException;

/**
 * @author slukjanov aka Frostman
 */
public class CacheableMethodInterceptor extends MethodInterceptor {

    public CacheableMethodInterceptor() {
        super("webjavin-cacheable-method-interceptor");
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) {
        return cacheable(methodInvocation);
    }

    @Override
    public boolean matches(CtMethod method) {
        try {
            return method.getAnnotation(Cacheable.class) != null;
        } catch (ClassNotFoundException e) {
            throw new JavinRuntimeException("Can't load class while matching interceptor for @Cacheable", e);
        }
    }

    protected static Object cacheable(MethodInvocation methodInvocation) {
        Cacheable cacheableAnn = methodInvocation.getMethod().getAnnotation(Cacheable.class);
        String cacheName = cacheableAnn.value();
        String cacheKey = cacheableAnn.key();
        String cacheCondition = cacheableAnn.condition();
        //todo we need cache it

        //todo !!!! may be create new MethodInterceptor for each annotated method ????
        //todo it can improve performance but eat more memory

        //todo impl caching
        return methodInvocation.proceed();
    }
}
