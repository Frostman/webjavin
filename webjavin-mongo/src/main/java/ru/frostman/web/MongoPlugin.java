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

import com.google.code.morphia.Morphia;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.common.collect.Lists;
import ru.frostman.web.classloading.AppClass;
import ru.frostman.web.plugin.Plugin;

import java.util.List;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class MongoPlugin extends Plugin {

    public MongoPlugin() {
        super(1);
    }

    @Override
    public void afterClassesEnhance(Map<String, AppClass> classes) {
        List<Class> morphiaClasses = Lists.newLinkedList();
        Morphia morphia = new Morphia();

        for (Map.Entry<String, AppClass> entry : classes.entrySet()) {
            Class<?> clazz = entry.getValue().getJavaClass();

            if(clazz.getAnnotation(Entity.class)!=null
                    ||clazz.getAnnotation(Embedded.class)!=null) {
                morphiaClasses.add(clazz);
                morphia.map(clazz);
            }
        }

        //todo store morphia to public bean storage
    }
}
