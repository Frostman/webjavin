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
import ru.frostman.web.util.Resource;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author slukjanov aka Frostman
 */
public class JavinConfig {
    private static File configFile;

    private long creationTime = 0;

    private JavinMode mode = JavinMode.DEV;

    private PluginsConfig plugins;

    public JavinConfig() {
        this(true);
    }

    public JavinConfig(boolean storeCreationTime) {
        if (storeCreationTime) {
            creationTime = System.currentTimeMillis();
        }
    }

    public static JavinConfig load() {
        ensureConfigFile();

        try {
            Yaml yaml = new Yaml(new Constructor(JavinConfig.class));
            JavinConfig config = (JavinConfig) yaml.load(new FileInputStream(configFile));

            if (config == null) {
                //todo warn user
                config = new JavinConfig();
            }

            return config;
        } catch (Exception e) {
            throw new FastRuntimeException("Error while loading WebJavin configuration (file: javin.yml)", e);
        }
    }

    public static boolean changed(JavinConfig config) {
        ensureConfigFile();

        return config.getCreationTime() < configFile.lastModified();
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
