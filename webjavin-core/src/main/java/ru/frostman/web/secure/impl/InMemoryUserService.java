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

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import ru.frostman.web.secure.thr.UsernameAlreadyTakenException;
import ru.frostman.web.secure.userdetails.Credentials;
import ru.frostman.web.secure.userdetails.UserDetails;
import ru.frostman.web.secure.userdetails.UserService;

import java.util.List;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class InMemoryUserService implements UserService {
    private static final Map<String, UserDetails> users = Maps.newHashMap();

    @Override
    public synchronized UserDetails getUser(String username) {
        Preconditions.checkNotNull(username);

        return users.get(username);
    }

    @Override
    public synchronized void addUser(UserDetails userDetails) throws UsernameAlreadyTakenException {
        Preconditions.checkNotNull(userDetails);
        Preconditions.checkNotNull(userDetails.getUsername());

        final String username = userDetails.getUsername();

        if (users.containsKey(username)) {
            throw new UsernameAlreadyTakenException(username);
        }

        users.put(username, userDetails);
    }

    @Override
    public synchronized UserDetails authenticate(Credentials credentials) {
        Preconditions.checkNotNull(credentials);

        for (Map.Entry<String, UserDetails> entry : users.entrySet()) {
            final UserDetails userDetails = entry.getValue();
            final List<Credentials> credentialsList = userDetails.getCredentials();

            if (credentialsList == null || credentialsList.size() == 0) {
                continue;
            }

            for (Credentials userCredentials : credentialsList) {
                if (userCredentials.equals(credentials)) {
                    return userDetails;
                }
            }
        }

        return null;
    }
}
