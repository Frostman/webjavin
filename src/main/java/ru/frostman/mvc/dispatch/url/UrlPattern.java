package ru.frostman.mvc.dispatch.url;

/**
 * @author slukjanov aka Frostman
 */
public interface UrlPattern {
    boolean matches(String url);

    UrlPatternType getType();
}
