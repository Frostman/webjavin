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

package ru.frostman.mvc.controller;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class Model {
    public static final String REQUEST = "request";
    public static final String RESPONSE = "response";

    private final Map<String, Object> map = Maps.newLinkedHashMap();

    public Model(HttpServletRequest request, HttpServletResponse response) {
        map.put(REQUEST, request);
        map.put(RESPONSE, response);
    }

    public HttpServletRequest getHttpServletRequest() {
        return (HttpServletRequest) map.get(REQUEST);
    }

    public HttpServletResponse getHttpServletResponse() {
        return (HttpServletResponse) map.get(RESPONSE);
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public Object get(String key) {
        return map.get(key);
    }

    public Object getAndPut(String key, Object value) {
        return map.put(key, value);
    }

    public Model put(String key, Object value) {
        map.put(key, value);

        return this;
    }

    public Model get(String key, Object value) {
        map.put(key, value);

        return this;
    }

    public Object getAndRemove(String key) {
        return map.remove(key);
    }

    public Model remove(String key) {
        map.remove(key);

        return this;
    }

    public Model putAll(Map<? extends String, ?> m) {
        map.putAll(m);

        return this;
    }

    /**
     * key1, value1, key2, value2, etc
     *
     * @param args
     *
     * @return
     */
    public Model putAll(Object... args) {
        Preconditions.checkArgument(args.length % 2 == 0, "Model#putAll wait for key-value parameters");

        String name = null;
        int idx = 0;
        for (Object arg : args) {
            if (idx % 2 == 0) {
                Preconditions.checkArgument(arg instanceof String);
                name = (String) arg;
            } else {
                put(name, arg);
            }

            idx++;
        }

        return this;
    }

    public Model clear() {
        map.clear();

        return this;
    }

    public Map<String, Object> toMap() {
        return Maps.newHashMap(map);
    }
}
