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

package ru.frostman.web.secure;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import ru.frostman.web.session.JavinSession;
import ru.frostman.web.session.JavinSessions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.UUID;

/**
 * @author slukjanov aka Frostman
 */
public class CsrfProtector {
    public static final String CSRF_TOKENS = "csrf_tokens";
    public static final String CSRF_TOKEN = "csrf_token";

    @SuppressWarnings({"unchecked"})
    public static String generateToken(HttpServletRequest request, HttpServletResponse response) {
        Preconditions.checkNotNull(request);
        Preconditions.checkNotNull(response);

        JavinSession session = JavinSessions.getSession(request, response, true);
        String token = UUID.randomUUID().toString();

        Set<String> csrfTokens = (Set<String>) session.getAttribute(CSRF_TOKENS);
        if (csrfTokens == null) {
            csrfTokens = Sets.newHashSet();
            session.setAttribute(CSRF_TOKENS, csrfTokens);
        }
        csrfTokens.add(token);

        return token;
    }

    public static String generateFormParam(HttpServletRequest request, HttpServletResponse response) {
        return "<input type='text' name='" + CSRF_TOKEN + "' value='" + generateToken(request, response) + "'/>";
    }

    @SuppressWarnings({"unchecked"})
    public static boolean checkToken(HttpServletRequest request, HttpServletResponse response) {
        Preconditions.checkNotNull(request);
        Preconditions.checkNotNull(response);

        String token = request.getParameter(CSRF_TOKEN);

        if (token == null) {
            return false;
        }

        JavinSession session = JavinSessions.getSession(request, response, true);
        Set<String> csrfTokens = (Set<String>) session.getAttribute(CSRF_TOKENS);

        return !csrfTokens.isEmpty() && csrfTokens.remove(token);
    }

}
