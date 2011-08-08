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

package ru.frostman.web.view;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MruCacheStorage;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.frostman.web.Javin;
import ru.frostman.web.config.JavinConfig;
import ru.frostman.web.controller.View;
import ru.frostman.web.thr.JavinRuntimeException;
import ru.frostman.web.view.freemarker.FreemarkerView;

import java.io.IOException;

/**
 * @author slukjanov aka Frostman
 */
public class AppViews {
    private static final Logger log = LoggerFactory.getLogger(AppViews.class);

    public static final String JAVIN_PLUGINS_VIEW = "/javin/plugins/view";

    private static final String FORWARD = "forward:";
    private static final String REDIRECT = "redirect:";

    private final Configuration freemarker;

    public AppViews() {
        freemarker = new Configuration();

        int updateInterval = JavinConfig.get().getMode().isProductionMode()
                ? JavinConfig.get().getTemplates().getUpdateInterval() : 0;
        freemarker.setTemplateUpdateDelay(updateInterval);

        log.debug("Freemarker :: template update interval: {}", updateInterval);

        freemarker.setDefaultEncoding("utf-8");
        freemarker.setOutputEncoding("utf-8");

        int maxStrongSize = JavinConfig.get().getTemplates().getMaxCacheStrongSize();
        int maxSoftSize = JavinConfig.get().getTemplates().getMaxCacheSoftSize();
        freemarker.setCacheStorage(new MruCacheStorage(maxStrongSize, maxSoftSize));

        log.debug("Freemarker :: MRU cache {} -- {}", maxStrongSize, maxSoftSize);

        try {
            ClassTemplateLoader appTemplateLoader = new ClassTemplateLoader(Javin.class, JavinConfig.get().getTemplates().getPath());
            ClassTemplateLoader pluginsTemplateLoader = new ClassTemplateLoader(Javin.class, JAVIN_PLUGINS_VIEW);

            freemarker.setTemplateLoader(new MultiTemplateLoader(new TemplateLoader[]{appTemplateLoader, pluginsTemplateLoader}));
        } catch (Exception e) {
            throw new JavinRuntimeException("Exception while initializing AppViews", e);
        }

        log.debug("Javin views successfully initialized.");
    }

    public View getViewByName(String name) {
        if (name.startsWith(FORWARD)) {
            return new ForwardView(name.substring(FORWARD.length()).trim());
        } else if (name.startsWith(REDIRECT)) {
            return new RedirectView(name.substring(REDIRECT.length()).trim());
        }

        try {
            return new FreemarkerView(freemarker.getTemplate(name));
        } catch (IOException e) {
            throw new JavinRuntimeException("Exception while instantiating FreemarkerView");
        }
    }
}
