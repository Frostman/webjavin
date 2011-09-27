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

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import ru.frostman.web.Javin;
import ru.frostman.web.thr.FastInitializationException;
import ru.frostman.web.thr.InitializationException;
import ru.frostman.web.util.Resource;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Throwables.propagate;

/**
 * @author slukjanov aka Frostman
 */
public class Plugins {

    private static final ClassLoader CLASS_LOADER = Javin.class.getClassLoader();

    public static CorePlugin load() {
        List<String> requiredPlugins = Lists.newLinkedList(Javin.getConfig().getPlugins().getList());
        List<PluginInfo> availablePlugins = getAllAvailable();
        Map<String, PluginInfo> availablePluginsMap = Maps.newHashMap();
        for (PluginInfo pluginInfo : availablePlugins) {
            String name = pluginInfo.getName();
            availablePluginsMap.put(name, pluginInfo);
        }

        List<PluginInfo> plugins = Lists.newLinkedList();
        for (String pluginName : requiredPlugins) {
            if (!availablePluginsMap.containsKey(pluginName)) {
                requiredPlugins.removeAll(availablePluginsMap.keySet());
                throw new FastInitializationException("In plugins list (in javin.yml) declared non-available plugins: "
                        + Joiner.on(',').join(requiredPlugins));
            }
            plugins.add(availablePluginsMap.get(pluginName));
        }

        return new CorePlugin(instantiatePlugins(plugins));
    }

    private static List<PluginInfo> getAllAvailable() {
        Yaml yaml = new Yaml(new Constructor(PluginInfo.class));

        List<PluginInfo> pluginInfos = Lists.newLinkedList();
        for (InputStream inputStream : Resource.getAllAsStreams("javin.plugin")) {
            try {
                Iterable<?> list = yaml.loadAll(inputStream);
                for (Object obj : list) {
                    if (obj instanceof PluginInfo) {
                        PluginInfo pluginInfo = (PluginInfo) obj;
                        if (isNullOrEmpty(pluginInfo.getPlugin()) || isNullOrEmpty(pluginInfo.getVersion())
                                || isNullOrEmpty(pluginInfo.getName())) {
                            continue;
                        }

                        pluginInfos.add(pluginInfo);
                    }
                }
            } catch (Exception e) {
                throw propagate(e);
            }
        }

        return pluginInfos;
    }

    private static void checkDependencies(List<PluginInfo> pluginInfos) {
        Set<String> existsBefore = Sets.newHashSet();
        //todo add system plugins existsBefore.addAll(Arrays.asList())
        existsBefore.addAll(Javin.getConfig().getPlugins().getList());

        Set<String> shouldBeAfter = Sets.newHashSet();

        Set<String> allPlugins = Sets.newHashSet();
        for (PluginInfo pluginInfo : pluginInfos) {
            allPlugins.add(pluginInfo.getName());
        }

        for (PluginInfo pluginInfo : pluginInfos) {
            String name = pluginInfo.getName();
            DependenciesInfo dependencies = pluginInfo.getDependencies();
            if (dependencies != null) {
                Set<String> before = dependencies.getBefore();
                if (before != null && !existsBefore.containsAll(before)) {
                    before.removeAll(existsBefore);
                    throw new FastInitializationException("Plugin '" + name + "' declare that it requires '"
                            + Joiner.on(',').join(before) + "' before it");
                }

                Set<String> after = dependencies.getAfter();
                if (after != null) {
                    shouldBeAfter.addAll(after);
                }

                Set<String> exists = dependencies.getExists();
                if (!allPlugins.containsAll(exists)) {
                    exists.removeAll(allPlugins);
                    throw new FastInitializationException("Plugin '" + name + "' declare that it requires '"
                            + Joiner.on(',').join(exists) + "'");
                }
            }

            shouldBeAfter.remove(name);
            existsBefore.add(name);
        }

        if (!shouldBeAfter.isEmpty()) {
            throw new FastInitializationException("Some plugins declare that they require '"
                    + Joiner.on(',').join(shouldBeAfter) + "' after them");
        }
    }

    @SuppressWarnings("unchecked")
    private static List<JavinPlugin<?>> instantiatePlugins(List<PluginInfo> pluginInfos) {
        List<JavinPlugin<?>> plugins = Lists.newLinkedList();
        for (PluginInfo pluginInfo : pluginInfos) {
            String name = pluginInfo.getName();
            Class<? extends JavinPlugin<?>> pluginClass;
            try {
                pluginClass = (Class<? extends JavinPlugin<?>>) CLASS_LOADER.loadClass(pluginInfo.getPlugin());
            } catch (Exception e) {
                throw new InitializationException("Can't load plugin's class of '" + name + "'", e);
            }
            JavinPlugin<?> plugin;
            try {
                java.lang.reflect.Constructor<? extends JavinPlugin<?>> constructor = pluginClass.getConstructor(String.class);
                plugin = constructor.newInstance(pluginInfo.getVersion());
            } catch (Exception e) {
                throw new InitializationException("Can't instantiate plugin '" + name + "'", e);
            }
            plugins.add(plugin);
        }

        return plugins;
    }
}
