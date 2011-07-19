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

package ru.frostman.web.session.impl;

import ru.frostman.web.session.JavinSession;

import java.util.Enumeration;
import java.util.UUID;

/**
 * @author slukjanov aka Frostman
 */
public class InMemorySession implements JavinSession {
    private String id = UUID.randomUUID().toString();
    private long creationTime;
    private long lastAccessedTime;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    @Override
    public Object getAttribute(String name) {
        //todo impl
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        //todo impl
        return null;
    }

    @Override
    public void setAttribute(String name, Object value) {
        //todo impl
    }

    @Override
    public void removeAttribute(String name) {
        //todo impl
    }

    @Override
    public void invalidate() {
        //todo impl
    }
}
