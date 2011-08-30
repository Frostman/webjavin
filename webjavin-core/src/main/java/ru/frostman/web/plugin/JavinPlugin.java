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
import javassist.CtClass;
import javassist.CtConstructor;
import ru.frostman.web.annotation.Component;
import ru.frostman.web.classloading.AppClass;
import ru.frostman.web.classloading.enhance.InjectEnhancer;
import ru.frostman.web.inject.BaseInjection;
import ru.frostman.web.inject.InjectionRule;
import ru.frostman.web.thr.ActionEnhancerException;
import ru.frostman.web.thr.BytecodeManipulationException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
class JavinPlugin extends Plugin {

    private final List<InjectionRule> injectionRules = Lists.newLinkedList();

    JavinPlugin() {
        super(0);
    }

    @Override
    public void beforeClassesEnhance(Map<String, AppClass> classes) {
        for (Map.Entry<String, AppClass> entry : classes.entrySet()) {
            AppClass appClass = entry.getValue();
            try {
                CtClass ctClass = appClass.getCtClass();
                Component componentAnn = (Component) ctClass.getAnnotation(Component.class);

                if (componentAnn == null) {
                    continue;
                }

                String name = componentAnn.value();
                List<String> classNames = Lists.newLinkedList();
                classNames.add(appClass.getName());
                classNames.addAll(Arrays.asList(componentAnn.implement()));
                List<String> annotationsNames = Lists.newLinkedList(Arrays.asList(componentAnn.annotated()));

                StringBuilder body = new StringBuilder();
                StringBuilder initCode = new StringBuilder();

                CtConstructor[] constructors = ctClass.getConstructors();
                if (constructors.length != 1) {
                    throw new ActionEnhancerException("Only one constructor should be in component: " + ctClass.getName());
                }

                //todo impl singleton mode
                StringBuilder parameters = InjectEnhancer.resolveParameters(constructors[0], body);
                initCode.append("new ").append(ctClass.getName()).append("(").append(parameters).append(")");

                injectionRules.add(new BaseInjection(annotationsNames, classNames, name, initCode.toString(), body.toString()));
            } catch (Exception e) {
                throw new BytecodeManipulationException("Exception while searching components in class: " + appClass.getName(), e);
            }
        }
    }

    @Override
    public List<InjectionRule> getCustomInjections() {
        return injectionRules;
    }
}
