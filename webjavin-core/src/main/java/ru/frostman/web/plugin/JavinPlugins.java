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
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.frostman.web.classloading.AppClass;
import ru.frostman.web.config.JavinConfig;
import ru.frostman.web.thr.JavinPluginException;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
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

    public static void reload() {
        List<String> pluginClassNames = JavinConfig.getCurrentConfig().getPlugins();

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
            }
        }

        instance = new JavinPlugins(ImmutableSortedSet.copyOf(newPlugins));
        instance.onLoad();
    }

    public static Plugin get() {
        return instance;
    }

    @Override
    public void onLoad() {
        for (Plugin plugin : plugins) {
            try {
                plugin.onLoad();
            } catch (Exception e) {
                throw new JavinPluginException("Exception while executing onLoad() on plugin with main class: " + plugin.getClass().getName(), e);
            }
        }
    }

    @Override
    public void beforeClassesEnhance(Map<String, AppClass> classes) {

    }
}
