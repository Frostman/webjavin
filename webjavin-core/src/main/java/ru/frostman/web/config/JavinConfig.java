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
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import ru.frostman.web.JavinContextListener;
import ru.frostman.web.JavinMode;
import ru.frostman.web.thr.JavinRuntimeException;
import ru.frostman.web.util.Resources;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Javin framework configuration.
 *
 * @author slukjanov aka Frostman
 */
public class JavinConfig {
    private static final Logger log = LoggerFactory.getLogger(JavinConfig.class);

    private static String configFileName = "/javin.yaml";

    private static JavinConfig currentConfig;

    private JavinMode mode = JavinMode.DEV;
    private String context = null;
    private String address = null;
    private List<String> plugins = Lists.newLinkedList();

    private ClassesConfig classes = new ClassesConfig();
    private TemplatesConfig templates = new TemplatesConfig();
    private AppConfig app = new AppConfig();
    private SecureConfig secure = new SecureConfig();
    private CacheConfig cache = new CacheConfig();
    private I18nConfig i18n = new I18nConfig();

    private Map<String, StaticResource> statics = Maps.newLinkedHashMap();

    /**
     * Updates Javin configuration.
     *
     * @return true if something changes
     */
    public synchronized static boolean update() {
        try {
            Yaml yaml = new Yaml(new Constructor(JavinConfig.class));
            JavinConfig config = (JavinConfig) yaml.load(getConfigStream());

            // some hacks
            ensureContext(config);
            ensureAddress(config);

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

    private static void ensureContext(JavinConfig config) {
        String context = config.context;
        if (context == null) {
            context = JavinContextListener.getServletContext().getContextPath();
        }
        if (context.endsWith("/")) {
            context = context.substring(0, context.length() - 1);
        }
        if (!context.startsWith("/")) {
            context = "/" + context;
        }
        config.context = context;
    }

    private static void ensureAddress(JavinConfig config) {
        String address = config.address;
        if (address == null) {
            throw new JavinRuntimeException("javin.yaml - Address isn't specified");
        }

        if (!address.contains("://")) {
            throw new JavinRuntimeException("javin.yaml - Address should contains schema (http:// for example)");
        }

        if (address.endsWith("/")) {
            address = address.substring(0, address.length() - 1);
        }
        config.address = address;
    }

    /**
     * @return current JavinConfig
     */
    public static JavinConfig get() {
        return currentConfig;
    }

    private static InputStream getConfigStream() {
        return Resources.getResourceAsStream(configFileName);
    }

    public static String getConfigFileName() {
        return configFileName;
    }

    /**
     * Set config file name. Changes will applied at next #update() method invokation.
     *
     * @param configFileName to use
     */
    public static void setConfigFileName(String configFileName) {
        JavinConfig.configFileName = configFileName;
    }

    public JavinMode getMode() {
        return mode;
    }

    public void setMode(JavinMode mode) {
        this.mode = mode;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public List<String> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<String> plugins) {
        this.plugins = plugins;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public SecureConfig getSecure() {
        return secure;
    }

    public void setSecure(SecureConfig secure) {
        this.secure = secure;
    }

    public Map<String, StaticResource> getStatics() {
        return statics;
    }

    public void setStatics(Map<String, StaticResource> statics) {
        this.statics = statics;
    }

    public CacheConfig getCache() {
        return cache;
    }

    public void setCache(CacheConfig cache) {
        this.cache = cache;
    }

    public I18nConfig getI18n() {
        return i18n;
    }

    public void setI18n(I18nConfig i18n) {
        this.i18n = i18n;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JavinConfig) {
            JavinConfig config = (JavinConfig) obj;

            return mode == config.mode
                    && Objects.equal(context, config.context)
                    && Objects.equal(address, config.address)
                    && Objects.equal(plugins, config.plugins)
                    && Objects.equal(classes, config.classes)
                    && Objects.equal(templates, config.templates)
                    && Objects.equal(app, config.app)
                    && Objects.equal(secure, config.secure)
                    && Objects.equal(statics, config.statics)
                    && Objects.equal(cache, config.cache)
                    && Objects.equal(i18n, config.i18n);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = mode != null ? mode.hashCode() : 0;
        result = 31 * result + (context != null ? context.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (plugins != null ? plugins.hashCode() : 0);
        result = 31 * result + (classes != null ? classes.hashCode() : 0);
        result = 31 * result + (templates != null ? templates.hashCode() : 0);
        result = 31 * result + (app != null ? app.hashCode() : 0);
        result = 31 * result + (secure != null ? secure.hashCode() : 0);
        result = 31 * result + (cache != null ? cache.hashCode() : 0);
        result = 31 * result + (i18n != null ? i18n.hashCode() : 0);
        result = 31 * result + (statics != null ? statics.hashCode() : 0);

        return result;
    }
}
