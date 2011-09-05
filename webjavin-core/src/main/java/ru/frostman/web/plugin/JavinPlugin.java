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

package ru.frostman.web.plugin;

import com.google.common.collect.Lists;
import ru.frostman.web.aop.MethodInterceptor;
import ru.frostman.web.classloading.AppClass;
import ru.frostman.web.inject.InjectionRule;

import java.util.List;
import java.util.Map;

import static ru.frostman.web.cache.CacheSupport.findCacheDeclarations;
import static ru.frostman.web.inject.InjectionSupport.findInjectionRules;
import static ru.frostman.web.secure.inject.SecureSupport.buildSecureInjectionRules;

/**
 * @author slukjanov aka Frostman
 */
class JavinPlugin extends Plugin {

    private final List<InjectionRule> injectionRules = Lists.newLinkedList();
    private final List<MethodInterceptor> methodInterceptors = Lists.newLinkedList();

    JavinPlugin() {
        super(0);
    }

    @Override
    public void beforeClassesEnhance(Map<String, AppClass> classes) {
        findInjectionRules(classes, injectionRules);
        findCacheDeclarations(classes, methodInterceptors);
        buildSecureInjectionRules(classes, injectionRules);
    }

    @Override
    public List<InjectionRule> getInjections() {
        return injectionRules;
    }

    @Override
    public List<MethodInterceptor> getMethodInterceptors() {
        return methodInterceptors;
    }
}
