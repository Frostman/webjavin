/******************************************************************************
 * Frosty - MVC framework.                                                    *
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

package ru.frostman.mvc.view;

import freemarker.cache.MruCacheStorage;
import freemarker.template.Configuration;
import ru.frostman.mvc.Frosty;
import ru.frostman.mvc.config.FrostyConfig;
import ru.frostman.mvc.controller.View;
import ru.frostman.mvc.thr.FrostyRuntimeException;

import java.io.File;
import java.io.IOException;

/**
 * @author slukjanov aka Frostman
 */
public class FrostyViews {
    private static final String FORWARD = "forward:";
    private static final String REDIRECT = "redirect:";
    private final Configuration freemarker;

    public FrostyViews() {
        freemarker = new Configuration();

        int updateInterval = FrostyConfig.getCurrentConfig().getMode().isProductionMode()
                ? FrostyConfig.getCurrentConfig().getTemplates().getUpdateInterval() : 0;
        freemarker.setTemplateUpdateDelay(updateInterval);
        freemarker.setDefaultEncoding("utf-8");
        freemarker.setOutputEncoding("utf-8");

        int maxStrongSize = FrostyConfig.getCurrentConfig().getTemplates().getMaxCacheStrongSize();
        int maxSoftSize = FrostyConfig.getCurrentConfig().getTemplates().getMaxCacheSoftSize();
        freemarker.setCacheStorage(new MruCacheStorage(maxStrongSize, maxSoftSize));

        try {
            freemarker.setDirectoryForTemplateLoading(new File(Frosty.getApplicationPath()
                    + FrostyConfig.getCurrentConfig().getTemplates().getPath()));
        } catch (IOException e) {
            throw new FrostyRuntimeException("Exception while initializing FrostyViews", e);
        }
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
            throw new FrostyRuntimeException("Exception while instantiating FreemarkerView");
        }
    }
}
