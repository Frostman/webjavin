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

import com.google.common.base.Preconditions;
import ru.frostman.web.Javin;

import javax.annotation.Nullable;

/**
 * @author slukjanov aka Frostman
 */
public abstract class JavinPlugin<C> {
    protected final String pluginName;
    protected final Class<C> pluginConfigClass;

    protected JavinPlugin(String pluginName, @Nullable Class<C> pluginConfigClass) {
        Preconditions.checkNotNull(pluginName, "Plugin name can't be null");

        this.pluginName = pluginName;
        this.pluginConfigClass = pluginConfigClass;
    }

    public C getConfig() {
        if (pluginConfigClass == null) {
            return null;
        }

        return Javin.getConfig().getPluginConfig(pluginName, pluginConfigClass);
    }

    public String getPluginName() {
        return pluginName;
    }

    public Class<C> getPluginConfigClass() {
        return pluginConfigClass;
    }
}
