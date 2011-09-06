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

package ru.frostman.web.secure.userdetails;

import ru.frostman.web.secure.thr.UsernameAlreadyTakenException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author slukjanov aka Frostman
 */
public interface UserService {

    UserDetails extract(HttpServletRequest request, HttpServletResponse response);

    UserDetails getUser(String username);

    void addUser(UserDetails userDetails) throws UsernameAlreadyTakenException;

    UserDetails authenticate(HttpServletRequest request, HttpServletResponse response, Credentials credentials);
}
