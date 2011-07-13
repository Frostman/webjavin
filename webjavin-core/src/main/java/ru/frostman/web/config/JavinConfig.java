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

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import ru.frostman.web.JavinMode;
import ru.frostman.web.thr.JavinRuntimeException;

import java.io.InputStream;
import java.util.List;

/**
 * @author slukjanov aka Frostman
 */
public class JavinConfig {
    private static JavinConfig currentConfig;

    private JavinMode mode = JavinMode.DEV;
    private List<String> plugins = Lists.newLinkedList();

    private ClassesConfig classes = new ClassesConfig();
    private TemplatesConfig templates = new TemplatesConfig();
    private AppConfig app = new AppConfig();

    static {
        update();
    }

    public synchronized static boolean update() {
        try {
            Yaml yaml = new Yaml(new Constructor(JavinConfig.class));
            JavinConfig config = (JavinConfig) yaml.load(getConfigStream());

            boolean changed = false;
            if (!config.equals(currentConfig)) {
                changed = true;

                currentConfig = config;
            }

            return changed;
        } catch (Exception e) {
            throw new JavinRuntimeException("Can't load framework configuration", e);
        }
    }

    public static JavinConfig getCurrentConfig() {
        return currentConfig;
    }

    private static InputStream getConfigStream() {
        return JavinConfig.class.getResourceAsStream("/javin.yaml");
    }

    public JavinMode getMode() {
        return mode;
    }

    public void setMode(JavinMode mode) {
        this.mode = mode;
    }

    public List<String> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<String> plugins) {
        this.plugins = plugins;
    }

    public ClassesConfig getClasses() {
        return classes;
    }

    public void setClasses(ClassesConfig classes) {
        this.classes = classes;
    }

    public TemplatesConfig getTemplates() {
        return templates;
    }

    public void setTemplates(TemplatesConfig templates) {
        this.templates = templates;
    }

    public AppConfig getApp() {
        return app;
    }

    public void setApp(AppConfig app) {
        this.app = app;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JavinConfig) {
            JavinConfig config = (JavinConfig) obj;

            return mode == config.mode
                    && Objects.equal(plugins, config.plugins)
                    && Objects.equal(classes, config.classes)
                    && Objects.equal(templates, config.templates)
                    && Objects.equal(app, config.app);
        }

        return false;
    }
}
