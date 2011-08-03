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

package ru.frostman.web.classloading.enhance;

import com.google.common.collect.Sets;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import ru.frostman.web.annotation.CacheEvict;
import ru.frostman.web.annotation.Cacheable;

import java.util.Set;

import static ru.frostman.web.classloading.enhance.EnhancerUtil.getDeclaredMethodsAnnotatedWith;

/**
 * @author slukjanov aka Frostman
 */
public class CacheEnhancer {
    public static void enhance(ClassPool classPool, CtClass ctClass) {

        Set<CtMethod> methods = Sets.newLinkedHashSet();
        methods.addAll(getDeclaredMethodsAnnotatedWith(Cacheable.class, ctClass));
        methods.addAll(getDeclaredMethodsAnnotatedWith(CacheEvict.class, ctClass));


        //todo may be rename target method such as in aop
        //todo may be caching as AOP method interceptor or use such technology of copying methods
        //todo think about $_ param that available in javassist
    }
}
