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

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import javassist.ClassPool;
import javassist.CtClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.frostman.web.aop.MethodInterceptor;
import ru.frostman.web.classloading.AppClass;
import ru.frostman.web.config.JavinConfig;
import ru.frostman.web.inject.InjectionRule;
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

    private static JavinPlugins instance;

    private final List<String> loadedPlugins;

    private final List<Plugin> plugins;

    private JavinPlugins(List<String> loadedPlugins, List<Plugin> plugins) {
        super(0);

        // prepend main Javin plugin

        loadedPlugins.add(0, JavinPlugin.class.getName());
        this.loadedPlugins = loadedPlugins;

        plugins.add(0, new JavinPlugin());
        this.plugins = plugins;
    }

    /**
     * Reload all Javin plugins and return aggregated plugin to run some handlers.
     *
     * @return true if app classes should be reloaded
     */
    public static boolean update() {
        if (instance != null && Objects.equal(instance.loadedPlugins, JavinConfig.get().getPlugins())) {
            return instance.reload();
        } else {
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

            instance = new JavinPlugins(pluginClassNames, Lists.<Plugin>newLinkedList(newPlugins));

            return instance.reload();
        }
    }

    /**
     * @return current aggregated plugin to run handlers
     */
    public static Plugin get() {
        return instance;
    }

    private List<String> appPackages;
    private List<MethodInterceptor> methodInterceptors;
    private List<InjectionRule> customInjections;

    @Override
    public boolean reload() {
        appPackages = Lists.newLinkedList();
        methodInterceptors = Lists.newLinkedList();
        customInjections = Lists.newLinkedList();
        boolean result = false;

        for (Plugin plugin : plugins) {
            try {
                result |= plugin.reload();

                appPackages.addAll(plugin.getAppClassesPackages());
                methodInterceptors.addAll(plugin.getPluginsMethodInterceptors());
                customInjections.addAll(plugin.getCustomInjections());
            } catch (Exception e) {
                throw new JavinPluginException("Exception while executing update() on plugin with main class: "
                        + plugin.getClass().getName(), e);
            }
        }

        return result;
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

    @Override
    public List<MethodInterceptor> getPluginsMethodInterceptors() {
        return methodInterceptors;
    }

    public List<InjectionRule> getCustomInjections() {
        return customInjections;
    }
}
