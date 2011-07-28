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

import com.google.common.collect.Maps;
import ru.frostman.web.session.JavinSession;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * @author slukjanov aka Frostman
 */
public class InMemorySession implements JavinSession {
    private InMemorySessionManager creator;

    //todo think about id generation strategy
    private String id = UUID.randomUUID().toString();
    private long creationTime;
    private long lastAccessedTime;
    private Map<String, Object> attributes = Maps.newLinkedHashMap();

    public InMemorySession(InMemorySessionManager creator) {
        this.creator = creator;
    }

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
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return new AttrNamesEnumeration(attributes.keySet().iterator());
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public void invalidate() {
        creator.removeSession(this);
    }

    private static class AttrNamesEnumeration implements Enumeration<String> {
        private final Iterator<String> iterator;

        private AttrNamesEnumeration(Iterator<String> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasMoreElements() {
            return iterator.hasNext();
        }

        @Override
        public String nextElement() {
            return iterator.next();
        }
    }
}
