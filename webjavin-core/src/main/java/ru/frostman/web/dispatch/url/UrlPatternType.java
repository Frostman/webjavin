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

package ru.frostman.web.dispatch.url;

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
