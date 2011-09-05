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

import javassist.CtClass;
import javassist.CtMethod;
import ru.frostman.web.annotation.CacheEvict;
import ru.frostman.web.annotation.Cacheable;
import ru.frostman.web.aop.MethodInterceptor;
import ru.frostman.web.classloading.AppClass;
import ru.frostman.web.thr.BytecodeManipulationException;

import java.util.List;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class CacheSupport {
    public static void findCacheDeclarations(Map<String, AppClass> classes, List<MethodInterceptor> methodInterceptors) {
        for (Map.Entry<String, AppClass> entry : classes.entrySet()) {
            AppClass appClass = entry.getValue();
            try {
                CtClass ctClass = appClass.getCtClass();

                for (CtMethod method : ctClass.getDeclaredMethods()) {
                    Cacheable cacheableAnn = (Cacheable) method.getAnnotation(Cacheable.class);
                    CacheEvict cacheEvictAnn = (CacheEvict) method.getAnnotation(CacheEvict.class);

                    if (cacheableAnn != null) {
                        // todo create interceptor
                    }

                    if (cacheEvictAnn != null) {
                        // todo create interceptor
                    }
                }

            } catch (Exception e) {
                throw new BytecodeManipulationException("Exception while searching cache declarations in: " + appClass.getCtClass(), e);
            }
        }
    }
}
