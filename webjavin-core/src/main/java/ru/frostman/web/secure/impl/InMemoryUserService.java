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

package ru.frostman.web.secure.impl;

import com.google.common.collect.MapMaker;
import ru.frostman.web.secure.JavinSecurityManager;
import ru.frostman.web.secure.thr.UsernameAlreadyTakenException;
import ru.frostman.web.secure.userdetails.Credentials;
import ru.frostman.web.secure.userdetails.UserDetails;
import ru.frostman.web.secure.userdetails.UserService;
import ru.frostman.web.session.JavinSession;
import ru.frostman.web.session.JavinSessions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class InMemoryUserService implements UserService {

    private final Map<String, UserDetails> users = new MapMaker().makeMap();

    @Override
    public synchronized UserDetails extract(HttpServletRequest request, HttpServletResponse response) {
        JavinSession session = JavinSessions.getSession(request, response);
        return (UserDetails) session.getAttribute(JavinSecurityManager.USER_DETAILS_ATTR);
    }

    @Override
    public synchronized UserDetails getUser(String username) {
        return users.get(username);
    }

    @Override
    public synchronized void addUser(UserDetails userDetails) throws UsernameAlreadyTakenException {
        String username = userDetails.getUsername();
        if (users.containsKey(username)) {
            throw new UsernameAlreadyTakenException(username);
        }

        users.put(username, userDetails);
    }

    @Override
    public synchronized UserDetails authenticate(HttpServletRequest request, HttpServletResponse response, Credentials credentials) {
        //todo impl
        return null;
    }
}
