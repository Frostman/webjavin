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

package ru.frostman.web.config;

import com.google.common.base.Objects;

/**
 * @author slukjanov aka Frostman
 */
public class TemplatesConfig {
    private String path;
    private int updateInterval = 1000;

    private int maxCacheStrongSize = 25;
    private int maxCacheSoftSize = 250;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    public int getMaxCacheStrongSize() {
        return maxCacheStrongSize;
    }

    public void setMaxCacheStrongSize(int maxCacheStrongSize) {
        this.maxCacheStrongSize = maxCacheStrongSize;
    }

    public int getMaxCacheSoftSize() {
        return maxCacheSoftSize;
    }

    public void setMaxCacheSoftSize(int maxCacheSoftSize) {
        this.maxCacheSoftSize = maxCacheSoftSize;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TemplatesConfig) {
            TemplatesConfig config = (TemplatesConfig) obj;

            return Objects.equal(path, config.path)
                    && updateInterval == config.updateInterval
                    && maxCacheStrongSize == config.maxCacheStrongSize
                    && maxCacheSoftSize == config.maxCacheSoftSize;
        }

        return false;
    }
}
