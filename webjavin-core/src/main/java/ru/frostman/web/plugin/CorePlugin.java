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

import com.google.common.collect.Lists;
import ru.frostman.web.Javin;
import ru.frostman.web.config.Config;

import java.util.List;

/**
 * @author slukjanov aka Frostman
 */
public class CorePlugin extends JavinPlugin<Config> {
    private final List<JavinPlugin<?>> plugins = Lists.newLinkedList();

    public CorePlugin(List<JavinPlugin<?>> plugins) {
        super("core", Javin.getVersion(), Config.class);
        //todo add system plugins this.plugins.add()
        this.plugins.addAll(plugins);
    }

    @Override
    public Config getConfig() {
        return Javin.getConfig();
    }

    @Override
    public boolean changed() {
        for (JavinPlugin<?> plugin : plugins) {
            if (plugin.changed()) {
                return true;
            }
        }

        return false;
    }
}
