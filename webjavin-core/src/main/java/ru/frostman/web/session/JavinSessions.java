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

package ru.frostman.web.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.frostman.web.config.JavinConfig;
import ru.frostman.web.thr.JavinRuntimeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author slukjanov aka Frostman
 */
public class JavinSessions {
    private static final Logger log = LoggerFactory.getLogger(JavinSessions.class);

    private static SessionManager sessionManager;

    public static boolean update() {
        String sessionManagerName = JavinConfig.getCurrentConfig().getApp().getSessionManager();

        Class<?> sessionManagerClass = null;
        try {
            sessionManagerClass = Class.forName(sessionManagerName);
        } catch (ClassNotFoundException e) {
            throw new JavinRuntimeException("Can't load session manager class with specified name: " + sessionManagerName, e);
        }

        if (SessionManager.class.isAssignableFrom(sessionManagerClass)) {
            try {
                sessionManager = (SessionManager) sessionManagerClass.newInstance();
            } catch (Throwable th) {
                throw new JavinRuntimeException("Cant instantiate specified session manager: " + sessionManagerName, th);
            }
        } else {
            throw new JavinRuntimeException("Specified session manager isn't inherited from SessionManager: " + sessionManagerName);
        }

        //todo think about this
        return true;
    }

    public static JavinSession getSession(HttpServletRequest request, HttpServletResponse response) {
        return sessionManager.getSession(request, response);
    }

    public static JavinSession getSession(HttpServletRequest request, HttpServletResponse response, boolean create) {
        return sessionManager.getSession(request, response, create);
    }
}
