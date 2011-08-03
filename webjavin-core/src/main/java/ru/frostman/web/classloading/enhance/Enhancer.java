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

import com.google.common.collect.Lists;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import ru.frostman.web.aop.AopEnhancer;
import ru.frostman.web.aop.MethodInterceptor;
import ru.frostman.web.classloading.AppClass;
import ru.frostman.web.classloading.AppClasses;
import ru.frostman.web.dispatch.ActionDefinition;
import ru.frostman.web.plugin.JavinPlugins;
import ru.frostman.web.thr.EnhancerException;
import ru.frostman.web.thr.JavinRuntimeException;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class Enhancer {
    private static final ClassPool classPool = new ClassPool();

    static {
        classPool.appendClassPath(new ClassClassPath(AppClasses.class));
        classPool.appendSystemPath();
    }

    public static void prepareClasses(Map<String, AppClass> classes) {
        for (AppClass appClass : Lists.newLinkedList(classes.values())) {
            appClass.setEnhancedBytecode(null);

            if (appClass.isGenerated()) {
                classes.remove(appClass.getName());
                continue;
            }

            CtClass ctClass;
            try {
                ctClass = classPool.makeClass(new ByteArrayInputStream(appClass.getBytecode()));
            } catch (Exception e) {
                throw new EnhancerException(e);
            }

            appClass.setCtClass(ctClass);
        }

    }

    public static void enhance(Map<String, AppClass> classes, AppClass appClass,
                               List<ActionDefinition> actionDefinitions, List<MethodInterceptor> methodInterceptors) {
        if (appClass.isGenerated() || appClass.getEnhancedBytecode() != null) {
            return;
        }

        CtClass ctClass = appClass.getCtClass();

        try {
            CtClass superclass = ctClass.getSuperclass();
            if (classes.containsKey(superclass.getName())) {
                Enhancer.enhance(classes, classes.get(superclass.getName()), actionDefinitions, methodInterceptors);
            }
        } catch (NotFoundException e) {
            throw new JavinRuntimeException(e);
        }

        // enhance actions
        ActionsEnhancer.enhance(classes, classPool, ctClass, actionDefinitions);

        // enhance in plugins
        JavinPlugins.get().enhanceClass(classes, classPool, ctClass);

        // AOP enhance
        AopEnhancer.enhance(classPool, ctClass, methodInterceptors);

        // enhance security
        SecurityEnhancer.enhance(classPool, ctClass);

        try {
            appClass.setEnhancedBytecode(ctClass.toBytecode());
        } catch (Exception e) {
            throw new EnhancerException(e);
        }

        ctClass.defrost();
        ctClass.detach();
    }

}
