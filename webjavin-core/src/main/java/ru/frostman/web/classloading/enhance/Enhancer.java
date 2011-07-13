/******************************************************************************
 * Frosty - MVC framework.                                                    *
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
import ru.frostman.web.aop.MethodWrapper;
import ru.frostman.web.classloading.FrostyClass;
import ru.frostman.web.classloading.FrostyClasses;
import ru.frostman.web.dispatch.ActionDefinition;
import ru.frostman.web.thr.EnhancerException;
import ru.frostman.web.thr.FrostyRuntimeException;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class Enhancer {
    private static final ClassPool classPool = new ClassPool();

    static {
        classPool.appendClassPath(new ClassClassPath(FrostyClasses.class));
        classPool.appendSystemPath();
    }

    public static void prepareClasses(Map<String, FrostyClass> classes) {
        for (FrostyClass frostyClass : Lists.newLinkedList(classes.values())) {
            frostyClass.setEnhancedBytecode(null);

            if (frostyClass.isGenerated()) {
                classes.remove(frostyClass.getName());
                continue;
            }

            CtClass ctClass;
            try {
                ctClass = classPool.makeClass(new ByteArrayInputStream(frostyClass.getBytecode()));
            } catch (Exception e) {
                throw new EnhancerException(e);
            }

            frostyClass.setCtClass(ctClass);
        }

    }

    public static void enhance(Map<String, FrostyClass> classes, FrostyClass frostyClass,
                               List<ActionDefinition> actionDefinitions, List<MethodWrapper> methodWrappers) {
        if (frostyClass.isGenerated() || frostyClass.getEnhancedBytecode() != null) {
            return;
        }

        CtClass ctClass = frostyClass.getCtClass();

        try {
            CtClass superclass = ctClass.getSuperclass();
            if (classes.containsKey(superclass.getName())) {
                Enhancer.enhance(classes, classes.get(superclass.getName()), actionDefinitions, methodWrappers);
            }
        } catch (NotFoundException e) {
            throw new FrostyRuntimeException(e);
        }

        ActionsEnhancer.enhance(classes, classPool, ctClass, actionDefinitions);
        AopEnhancer.enhance(classPool, ctClass, methodWrappers);
        SecurityEnhancer.enhance(classPool, ctClass);

        try {
            frostyClass.setEnhancedBytecode(ctClass.toBytecode());
        } catch (Exception e) {
            throw new EnhancerException(e);
        }

        ctClass.defrost();
        ctClass.detach();
    }

}
