package ru.frostman.mvc.dispatch.url;

import com.google.common.base.Preconditions;
import ru.frostman.mvc.util.HttpMethod;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author slukjanov aka Frostman
 */
public enum UrlPatternType {
    SERVLET, REGEX;

    public static UrlPattern get(String pattern, UrlPatternType type, Set<HttpMethod> methods) {
        Preconditions.checkNotNull(pattern, "Pattern can't be null");

        switch (type) {
            case SERVLET:
                return new ServletStyleUrlPattern(pattern, methods);
            case REGEX:
                return new RegexStyleUrlPattern(pattern, methods);
            default:
                throw new IllegalArgumentException("UrlPatternType " + type + " isn't supported");
        }
    }

    private static abstract class BaseUrlPattern implements UrlPattern {
        private final Set<HttpMethod> methods;

        public BaseUrlPattern(Set<HttpMethod> methods) {
            this.methods = methods;
        }

        protected boolean matchesMethod(HttpMethod method) {
            return methods.contains(method);
        }
    }

    private static class ServletStyleUrlPattern extends BaseUrlPattern {
        private final String pattern;
        private final Type type;

        private static enum Type {PREFIX, SUFFIX, LITERAL}

        public ServletStyleUrlPattern(String pattern, Set<HttpMethod> methods) {
            super(methods);

            if (pattern.startsWith("*")) {
                this.pattern = pattern.substring(1);
                this.type = Type.PREFIX;
            } else if (pattern.endsWith("*")) {
                this.pattern = pattern.substring(0, pattern.length() - 1);
                this.type = Type.SUFFIX;
            } else {
                this.pattern = pattern;
                this.type = Type.LITERAL;
            }
        }

        public boolean matches(String url, HttpMethod method) {
            Preconditions.checkNotNull(url, "Url can't be null");
            Preconditions.checkNotNull(method, "HttpMethod can't be null");

            if (!matchesMethod(method)) {
                return false;
            }

            switch (type) {
                case PREFIX:
                    return url.endsWith(pattern);
                case SUFFIX:
                    return url.startsWith(pattern);
                default:
                    return pattern.equals(url);
            }
        }

        public UrlPatternType getType() {
            return SERVLET;
        }
    }

    private static class RegexStyleUrlPattern extends BaseUrlPattern {
        private final Pattern pattern;

        public RegexStyleUrlPattern(String pattern, Set<HttpMethod> methods) {
            super(methods);
            this.pattern = Pattern.compile(pattern);
        }

        public boolean matches(String url, HttpMethod method) {
            Preconditions.checkNotNull(url, "Url can't be null");
            Preconditions.checkNotNull(method, "HttpMethod can't be null");

            return matchesMethod(method) && pattern.matcher(url).matches();
        }

        public UrlPatternType getType() {
            return REGEX;
        }
    }
}
