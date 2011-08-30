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

package ru.frostman.web.indigo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import ru.frostman.web.thr.JavinRuntimeException;
import ru.frostman.web.util.Resources;

import java.io.InputStream;

/**
 * @author slukjanov aka Frostman
 */
public class IndigoConfig {
    private static final Logger log = LoggerFactory.getLogger(IndigoConfig.class);

    private static IndigoConfig currentConfig;

    /**
     * Updates Javin configuration.
     *
     * @return true if something changes
     */
    public synchronized static boolean update() {
        try {
            Yaml yaml = new Yaml(new Constructor(IndigoConfig.class));
            IndigoConfig config = (IndigoConfig) yaml.load(getConfigStream());

            boolean changed = false;
            if (!config.equals(currentConfig)) {
                changed = true;

                currentConfig = config;
            }

            return changed;
        } catch (Exception e) {
            throw new JavinRuntimeException("Can't load Indigo configuration", e);
        }
    }

    /**
     * @return current IndigoConfig
     */
    public static IndigoConfig get() {
        return currentConfig;
    }

    private static InputStream getConfigStream() {
        return Resources.getResourceAsStream("indigo.yaml");
    }

}
