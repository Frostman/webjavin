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

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @author slukjanov aka Frostman
 */
public class DependenciesInfo {
    private Set<String> before = Sets.newHashSet();
    private Set<String> after = Sets.newHashSet();
    private Set<String> exists = Sets.newHashSet();

    public DependenciesInfo() {
    }

    public Set<String> getBefore() {
        return before;
    }

    public void setBefore(Set<String> before) {
        this.before = before;
    }

    public Set<String> getAfter() {
        return after;
    }

    public void setAfter(Set<String> after) {
        this.after = after;
    }

    public Set<String> getExists() {
        return exists;
    }

    public void setExists(Set<String> exists) {
        this.exists = exists;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("before", before)
                .add("after", after)
                .add("exists", exists)
                .toString();
    }
}
