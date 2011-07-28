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

package ru.frostman.web.util;

import com.google.common.base.Objects;

import javax.annotation.Nullable;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author slukjanov aka Frostman
 */
public class Http {

    public static String getCookieValue(String name, @Nullable String defaultValue, HttpServletRequest request) {
        return getCookieValue(name, defaultValue, request.getCookies());
    }

    public static String getCookieValue(String name, @Nullable String defaultValue, Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (Objects.equal(name, cookie.getName())) {
                return cookie.getValue();
            }
        }

        return defaultValue;
    }

    public static String getCookieValue(String name, HttpServletRequest request) {
        return getCookieValue(name, null, request.getCookies());
    }

    public static String getCookieValue(String name, Cookie[] cookies) {
        return getCookieValue(name, null, cookies);
    }
}
