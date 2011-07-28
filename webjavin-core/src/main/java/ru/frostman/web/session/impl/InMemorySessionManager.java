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

package ru.frostman.web.session.impl;

import com.google.common.collect.MapMaker;
import ru.frostman.web.session.JavinSession;
import ru.frostman.web.session.SessionManager;
import ru.frostman.web.util.Http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @author slukjanov aka Frostman
 */
public class InMemorySessionManager extends SessionManager {
    public static final String SESSION_ID_COOKIE = "sid";

    private ConcurrentMap<String, JavinSession> sessions = new MapMaker()
            //todo remove hard code
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .makeMap();

    @Override
    public boolean checkSession(HttpServletRequest request, HttpServletResponse response) {
        // todo impl
        return true;
    }

    @Override
    public JavinSession getSession(HttpServletRequest request, HttpServletResponse response, boolean create) {
        String sid = Http.getCookieValue(SESSION_ID_COOKIE, request);

        if (sid == null && !create) {
            return null;
        }

        if (sid != null) {
            //todo think about expired sessions and invalidated
            return sessions.get(sid);
        }

        JavinSession session = new InMemorySession(this);
        sessions.put(session.getId(), session);

        return session;
    }

    protected void removeSession(InMemorySession session) {
        sessions.remove(session.getId());
    }
}
