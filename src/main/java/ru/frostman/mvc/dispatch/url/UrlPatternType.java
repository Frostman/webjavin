package ru.frostman.mvc.dispatch.url;

import com.google.common.base.Preconditions;

import java.util.regex.Pattern;

/**
 * @author slukjanov aka Frostman
 */
public enum UrlPatternType {
    SERVLET, REGEX;

    public static UrlPattern get(String pattern, UrlPatternType type) {
        Preconditions.checkNotNull(pattern, "Pattern can't be null");

        switch (type) {
            case SERVLET:
                return new ServletStyleUrlPattern(pattern);
            case REGEX:
                return new RegexStyleUrlPattern(pattern);
            default:
                throw new IllegalArgumentException("UrlPatternType " + type + " isn't supported");
        }
    }

    private static class ServletStyleUrlPattern implements UrlPattern {
        private final String pattern;
        private final Type type;

        private static enum Type {PREFIX, SUFFIX, LITERAL}

        public ServletStyleUrlPattern(String pattern) {
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

        public boolean matches(String url) {
            Preconditions.checkNotNull(url, "Url can't be null");

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

    private static class RegexStyleUrlPattern implements UrlPattern {
        private final Pattern pattern;

        public RegexStyleUrlPattern(String pattern) {
            this.pattern = Pattern.compile(pattern);
        }

        public boolean matches(String url) {
            Preconditions.checkNotNull(url, "Url can't be null");

            return pattern.matcher(url).matches();
        }

        public UrlPatternType getType() {
            return REGEX;
        }
    }
}
