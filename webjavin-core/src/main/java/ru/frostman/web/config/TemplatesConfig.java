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

/**
 * @author slukjanov aka Frostman
 */
public class TemplatesConfig {

    /**
     * Path to templates, root is the classpath
     */
    private String path;

    /**
     * Templates update interval, used only in production mode
     */
    private int updateInterval = 36000000; // 10h

    /**
     * The maximum number of strongly referenced templates
     */
    private int maxCacheStrongSize = 25;

    /**
     * The maximum number of softly referenced templates
     */
    private int maxCacheSoftSize = 250;

    /**
     * @return path to templates, root is the classpath
     */
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return templates update interval in ms, used only in production mode
     */
    public int getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    /**
     * @return the maximum number of strongly referenced templates
     */
    public int getMaxCacheStrongSize() {
        return maxCacheStrongSize;
    }

    public void setMaxCacheStrongSize(int maxCacheStrongSize) {
        this.maxCacheStrongSize = maxCacheStrongSize;
    }

    /**
     * @return the maximum number of softly referenced templates
     */
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

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + updateInterval;
        result = 31 * result + maxCacheStrongSize;
        result = 31 * result + maxCacheSoftSize;
        return result;
    }
}
