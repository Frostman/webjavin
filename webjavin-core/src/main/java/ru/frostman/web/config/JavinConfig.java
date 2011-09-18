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
import ru.frostman.web.thr.JavinRuntimeException;
import ru.frostman.web.util.Resource;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author slukjanov aka Frostman
 */
public class JavinConfig {
    private static File configFile;

    private long creationTime;

    private JavinMode mode;

    public JavinConfig() {
        this(true);
    }

    public JavinConfig(boolean storeCreationTime) {
        if (storeCreationTime) {
            creationTime = System.currentTimeMillis();
        }
    }

    public static JavinConfig load() {
        if (configFile == null) {
            configFile = Resource.getAsFile("javin.yml");
            if (configFile == null || !configFile.exists()) {
                //todo notify user
            }
        }

        try {
            Yaml yaml = new Yaml(new Constructor(JavinConfig.class));
            return (JavinConfig) yaml.load(new FileInputStream(configFile));
        } catch (Exception e) {
            //todo impl
            throw new JavinRuntimeException("Can't load framework configuration", e);
        }
    }

    public static boolean changed(JavinConfig config) {
        return config.getCreationTime() < configFile.lastModified();
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
}
