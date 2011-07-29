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

package ru.frostman.web;

import javassist.ClassPool;
import javassist.CtClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.frostman.web.aop.AopEnhancer;
import ru.frostman.web.aop.MethodInterceptor;
import ru.frostman.web.aop.MethodInterceptors;
import ru.frostman.web.classloading.AppClass;
import ru.frostman.web.plugin.Plugin;

import java.util.List;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class AopPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(AopPlugin.class);

    private List<MethodInterceptor> methodInterceptors;

    public AopPlugin() {
        super(1);
    }

    @Override
    public void beforeClassesEnhance(Map<String, AppClass> classes) {
        methodInterceptors = MethodInterceptors.findInterceptors(classes);
    }

    @Override
    public void enhanceClass(Map<String, AppClass> classes, ClassPool classPool, CtClass ctClass) {
        AopEnhancer.enhance(classPool, ctClass, methodInterceptors);
    }
}
