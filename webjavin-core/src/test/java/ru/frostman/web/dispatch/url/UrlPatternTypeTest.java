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

package ru.frostman.web.dispatch.url;

import org.junit.Assert;
import org.junit.Test;

import static ru.frostman.web.dispatch.url.UrlPatternType.SERVLET;
import static ru.frostman.web.dispatch.url.UrlPatternType.get;

/**
 * @author slukjanov aka Frostman
 */
public class UrlPatternTypeTest {
    private void checkUrlPatternMatches(String pattern, String url) {
        UrlPattern urlPattern = get(pattern, SERVLET);
        Assert.assertTrue(urlPattern.matches(url));
    }

    private void checkUrlPatternNonMatches(String pattern, String url) {
        UrlPattern urlPattern = get(pattern, SERVLET);
        Assert.assertFalse(urlPattern.matches(url));
    }

    @Test
    public void testServletStyleEndsWithAny() {
        checkUrlPatternMatches("/*", "/");
        checkUrlPatternMatches("/*", "/some.path?param");
        checkUrlPatternMatches("/*", "/some/long/path?with&params");
    }

    @Test
    public void testServletStyleEndsWithAnyWithPrefix() {
        checkUrlPatternNonMatches("/static/*", "/");
        checkUrlPatternNonMatches("/static/*", "/some.path?param");
        checkUrlPatternNonMatches("/static/*", "/some/long/path?with&params");

        checkUrlPatternMatches("/static/*", "/static/");
        checkUrlPatternMatches("/static/*", "/static/some.path?param");
        checkUrlPatternMatches("/static/*", "/static/some/long/path?with&params");
    }

    @Test
    public void testServletStyleAnyNameWithExtension() {
        checkUrlPatternMatches("*.ext", "/some.path.ext");
    }

}
