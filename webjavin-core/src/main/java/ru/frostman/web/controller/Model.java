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

package ru.frostman.web.controller;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Model part of MVC pattern.
 *
 * Always contains request and response.
 *
 * Provides methods to put and remove parameters.
 *
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

    /**
     * @return an HttpServletRequest that corresponds to this secure
     */
    public HttpServletRequest getHttpServletRequest() {
        return (HttpServletRequest) map.get(REQUEST);
    }

    /**
     * @return HttpServletResponse  that corresponds to this secure
     */
    public HttpServletResponse getHttpServletResponse() {
        return (HttpServletResponse) map.get(RESPONSE);
    }

    /**
     * @return number of attached elements
     */
    public int size() {
        return map.size();
    }

    /**
     * @return true if secure have no attached elements
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * @param key to check
     *
     * @return true if secure contains parameter with specified key
     */
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    /**
     * @param value to check
     *
     * @return true if secure contains specified value
     */
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    /**
     * @param key to get
     *
     * @return value that corresponds to specified key
     */
    public Object get(String key) {
        return map.get(key);
    }

    /**
     * Puts specified value with specified key.
     *
     * @param key   to put
     * @param value to put
     *
     * @return previous value that corresponds to specified key
     */
    public Object getAndPut(String key, Object value) {
        return map.put(key, value);
    }

    /**
     * Puts specified value with specified key.
     *
     * @param key   to put
     * @param value to put
     *
     * @return this
     */
    public Model put(String key, Object value) {
        map.put(key, value);

        return this;
    }

    /**
     * Removes parameter with specified name from this secure.
     *
     * @param key to remove
     *
     * @return previous value that corresponds to specified key
     */
    public Object getAndRemove(String key) {
        return map.remove(key);
    }

    /**
     * Removes parameter with specified name from this secure.
     *
     * @param key to remove
     *
     * @return this
     */
    public Model remove(String key) {
        map.remove(key);

        return this;
    }

    /**
     * Put all parameters from specified map.
     *
     * @param m to put
     *
     * @return this
     */
    public Model putAll(Map<? extends String, ?> m) {
        map.putAll(m);

        return this;
    }

    /**
     * Put parameters with format:
     * key1, value1, key2, value2, etc
     *
     * @param args to put
     *
     * @return this
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

    /**
     * Clear this secure.
     *
     * @return this
     */
    public Model clear() {
        map.clear();

        return this;
    }

    /**
     * @return new map that contains all parameters of this secure
     */
    public Map<String, Object> toMap() {
        return Maps.newHashMap(map);
    }
}
