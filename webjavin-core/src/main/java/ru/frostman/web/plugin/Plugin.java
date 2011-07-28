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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import javassist.ClassPool;
import javassist.CtClass;
import ru.frostman.web.classloading.AppClass;

import java.util.List;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public abstract class Plugin implements Comparable<Plugin> {
    private int weight;

    protected Plugin(int weight) {
        Preconditions.checkArgument(weight >= 0, "Plugin weight should be >= 0");

        this.weight = weight;
    }

    public void onLoad() {
    }

    public void beforeClassesEnhance(Map<String, AppClass> classes) {
    }

    public void afterClassesEnhance(Map<String, AppClass> classes) {
    }

    public void enhanceClass(Map<String, AppClass> classes, ClassPool classPool, CtClass ctClass) {
    }

    public List<String> getAppClassesPackages() {
        return Lists.newLinkedList();
    }

    @Override
    public int compareTo(Plugin p) {
        int res = weight < p.weight ? -1 : (weight != p.weight ? 1 : 0);

        if (res != 0) {
            return res;
        }

        res = getClass().getName().compareTo(p.getClass().getName());

        if (res != 0) {
            return res;
        }

        int thisHashCode = System.identityHashCode(this);
        int otherHashCode = System.identityHashCode(p);

        return (thisHashCode < otherHashCode ? -1 : (thisHashCode != otherHashCode ? 1 : 0));
    }
}
