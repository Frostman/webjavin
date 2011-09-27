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

package ru.frostman.web.config;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import ru.frostman.web.thr.FastRuntimeException;
import ru.frostman.web.util.ObjectMapping;
import ru.frostman.web.util.Resource;

import java.io.File;
import java.io.FileInputStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author slukjanov aka Frostman
 */
public class Config {
    private static File configFile;

    private long creationTime = 0;

    private JavinMode mode = JavinMode.DEV;

    private PluginsConfig plugins = new PluginsConfig();

    public Config() {
        this(true);
    }

    public Config(boolean storeCreationTime) {
        if (storeCreationTime) {
            creationTime = System.currentTimeMillis();
        }
    }

    public static Config load() {
        ensureConfigFile();

        try {
            Yaml yaml = new Yaml(new Constructor(Config.class));
            Config config = (Config) yaml.load(new FileInputStream(configFile));

            if (config == null) {
                //todo warn user
                config = new Config();
            }

            return config;
        } catch (Exception e) {
            throw new FastRuntimeException("Error while loading WebJavin configuration (file: javin.yml)", e);
        }
    }

    public <T> T getPluginConfig(String pluginName, Class<T> pluginConfigClass) {
        checkNotNull(pluginName, "plugin name can't be null");
        checkNotNull(pluginConfigClass, "plugin config class can't be null");

        //todo think about caching plugins config
        try {
            Object pluginConfig = plugins.getConfig().get(pluginName);
            if (pluginConfig == null) {
                //todo warn
                return pluginConfigClass.newInstance();
            }

            return ObjectMapping.convert(pluginConfig, pluginConfigClass);
        } catch (Throwable th) {
            throw new FastRuntimeException("Error while getting configuration for plugin: '" + pluginName + "'", th);
        }
    }

    public static boolean changed(Config config) {
        checkNotNull(config);
        ensureConfigFile();

        if (config.getCreationTime() < configFile.lastModified()) {
            Config newConfig = load();

            return !config.equals(newConfig);
        }

        return false;
    }

    private static void ensureConfigFile() {
        if (configFile == null) {
            configFile = Resource.getAsFile("javin.yml");
            if (configFile == null || !configFile.exists()) {
                throw new FastRuntimeException("Configuration file is not found (file: javin.yml)");
            }
        }
    }

    public long getCreationTime() {
        return creationTime;
    }

    public JavinMode getMode() {
        return mode;
    }

    public void setMode(JavinMode mode) {
        this.mode = mode;
    }

    public PluginsConfig getPlugins() {
        return plugins;
    }

    public void setPlugins(PluginsConfig plugins) {
        this.plugins = plugins;
    }
}
