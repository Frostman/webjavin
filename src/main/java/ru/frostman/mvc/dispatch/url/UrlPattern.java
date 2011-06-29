package ru.frostman.mvc.dispatch.url;

import ru.frostman.mvc.util.HttpMethod;

/**
 * @author slukjanov aka Frostman
 */
public interface UrlPattern {
    boolean matches(String url, HttpMethod method);

    UrlPatternType getType();
}
