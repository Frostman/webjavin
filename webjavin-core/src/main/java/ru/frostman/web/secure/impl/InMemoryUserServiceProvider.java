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

import com.google.common.collect.Sets;
import ru.frostman.web.secure.userdetails.*;

import java.util.Set;

/**
 * @author slukjanov aka Frostman
 */
public class InMemoryUserServiceProvider implements UserServiceProvider {
    private final UserService userService = new InMemoryUserService();

    @Override
    public UserService get() {
        return userService;
    }

    @Override
    public Class<? extends UserService> getUserServiceClass() {
        return InMemoryUserService.class;
    }

    @Override
    public Class<? extends UserDetails> getUserDetailsClass() {
        return InMemoryUserDetails.class;
    }

    @Override
    public Class<? extends Role> getRoleClass() {
        return InMemoryRole.class;
    }

    @Override
    public Set<Class<? extends Credentials>> getCredentialsClasses() {
        return Sets.newHashSet();
    }
}
