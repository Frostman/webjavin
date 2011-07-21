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

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import javassist.ClassPool;
import javassist.CtClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.frostman.web.classloading.AppClass;
import ru.frostman.web.config.JavinConfig;
import ru.frostman.web.thr.JavinPluginException;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Javin framework plugins manager.
 *
 * @author slukjanov aka Frostman
 */
public class JavinPlugins extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(JavinPlugins.class);
    private static Plugin instance;

    private final Set<Plugin> plugins;

    private JavinPlugins(Set<Plugin> plugins) {
        super(0);

        this.plugins = plugins;
    }

    /**
     * Reload all Javin plugins and return aggregated plugin to run some handlers.
     *
     * @return aggregated plugin to run handlers
     */
    public static Plugin reload() {
        List<String> pluginClassNames = JavinConfig.get().getPlugins();

        Set<Plugin> newPlugins = Sets.newTreeSet();
        for (String pluginClassName : pluginClassNames) {
            Class pluginRawClass;
            try {
                pluginRawClass = Class.forName(pluginClassName);
            } catch (ClassNotFoundException e) {
                log.warn("Unable to load plugin with main class: " + pluginClassName);
                continue;
            }

            if (Plugin.class.isAssignableFrom(pluginRawClass)) {
                Plugin plugin;
                try {
                    plugin = (Plugin) pluginRawClass.newInstance();
                } catch (Exception e) {
                    throw new JavinPluginException("Exception while instantiating plugin with main class: " + pluginClassName, e);
                }

                newPlugins.add(plugin);
            } else {
                throw new JavinPluginException("There is class not inherited from Plugin in plugins list: " + pluginClassName);
            }
        }

        instance = new JavinPlugins(ImmutableSortedSet.copyOf(newPlugins));
        instance.onLoad();

        return instance;
    }

    /**
     * @return current aggregated plugin to run handlers
     */
    public static Plugin get() {
        return instance;
    }

    private List<String> appPackages;

    @Override
    public void onLoad() {
        appPackages = Lists.newLinkedList();

        for (Plugin plugin : plugins) {
            try {
                plugin.onLoad();

                //todo write in docs that it works like this
                appPackages.addAll(plugin.getAppClassesPackages());
            } catch (Exception e) {
                throw new JavinPluginException("Exception while executing onLoad() on plugin with main class: "
                        + plugin.getClass().getName(), e);
            }
        }
    }

    @Override
    public void beforeClassesEnhance(Map<String, AppClass> classes) {
        for (Plugin plugin : plugins) {
            try {
                plugin.beforeClassesEnhance(classes);
            } catch (Exception e) {
                throw new JavinPluginException("Exception while executing beforeClassesEnhance() on plugin with main class: "
                        + plugin.getClass().getName(), e);
            }
        }
    }

    @Override
    public void afterClassesEnhance(Map<String, AppClass> classes) {
        for (Plugin plugin : plugins) {
            try {
                plugin.afterClassesEnhance(classes);
            } catch (Exception e) {
                throw new JavinPluginException("Exception while executing afterClassesEnhance() on plugin with main class: "
                        + plugin.getClass().getName(), e);
            }
        }
    }

    @Override
    public void enhanceClass(Map<String, AppClass> classes, ClassPool classPool, CtClass ctClass) {
        for (Plugin plugin : plugins) {
            try {
                plugin.enhanceClass(classes, classPool, ctClass);
            } catch (Exception e) {
                throw new JavinPluginException("Exception while executing enhanceClass() on plugin with main class: "
                        + plugin.getClass().getName(), e);
            }
        }
    }

    @Override
    public List<String> getAppClassesPackages() {
        return appPackages;
    }
}
