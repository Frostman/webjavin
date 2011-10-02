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

package ru.frostman.web.template.mvel;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import ru.frostman.web.Javin;
import ru.frostman.web.template.Template;
import ru.frostman.web.template.TemplatesManager;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @author slukjanov aka Frostman
 */
public class MvelTemplatesManager extends TemplatesManager {

    private final ConcurrentMap<String, Template> templatesCache = makeCache();

    @Override
    public Template get(String name) {
        return templatesCache.get(name);
    }

    private ConcurrentMap<String, Template> makeCache() {
        final String path = Javin.getConfig().getTemplates().getPath();
        final Function<String, Template> compute = new Function<String, Template>() {

            @Override
            public Template apply(@Nullable String name) {
                return new MvelTemplate(path + name);
            }
        };

        long updateInterval = Javin.getConfig().getTemplates().getUpdateInterval();
        if (updateInterval < 0) {
            return new MapMaker().makeComputingMap(compute);
        } else if (updateInterval == 0) {
            return new MapMaker()
                    .expireAfterWrite(0, TimeUnit.NANOSECONDS)
                    .makeComputingMap(compute);
        } else {
            return new MapMaker()
                    .expireAfterWrite(updateInterval, TimeUnit.MILLISECONDS)
                    .makeComputingMap(compute);
        }
    }

}
