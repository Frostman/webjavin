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

import java.util.List;

/**
 * @author slukjanov aka Frostman
 */
public class ClassesConfig {
    private List<String> packages;
    private long updateInterval;

    public List<String> getPackages() {
        return packages;
    }

    public void setPackages(List<String> packages) {
        this.packages = packages;
    }

    public long getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(long updateInterval) {
        this.updateInterval = updateInterval;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClassesConfig) {
            ClassesConfig config = (ClassesConfig) obj;

            return Objects.equal(packages, config.packages) && updateInterval == config.updateInterval;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = packages != null ? packages.hashCode() : 0;
        result = 31 * result + (int) (updateInterval ^ (updateInterval >>> 32));
        return result;
    }
}
