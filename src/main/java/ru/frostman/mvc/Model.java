package ru.frostman.mvc;

import com.google.common.collect.Maps;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class Model {
    private static final String REQUEST = "request";
    private static final String RESPONSE = "response";

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

    public Model clear() {
        map.clear();

        return this;
    }
}
