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

package ru.frostman.web.mongo.secure;

import com.mongodb.MongoException;
import ru.frostman.web.mongo.BaseEntity;
import ru.frostman.web.secure.JavinSecurityManager;
import ru.frostman.web.secure.thr.UsernameAlreadyTakenException;
import ru.frostman.web.secure.userdetails.Credentials;
import ru.frostman.web.secure.userdetails.UserDetails;
import ru.frostman.web.secure.userdetails.UserService;
import ru.frostman.web.session.JavinSessions;
import ru.frostman.web.thr.JavinRuntimeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author slukjanov aka Frostman
 */
public class MongoUserService extends BaseEntity implements UserService {

    private final MongoUserDao dao = new MongoUserDao();

    @Override
    public UserDetails extract(HttpServletRequest request, HttpServletResponse response) {
        return (MongoUser) JavinSessions.getSession(request, response).getAttribute(JavinSecurityManager.USER_DETAILS_ATTR);
    }

    @Override
    public UserDetails getUser(String username) {
        return dao.getByName(username);
    }

    @Override
    public void addUser(UserDetails userDetails) throws UsernameAlreadyTakenException {
        if (userDetails instanceof MongoUser) {
            try {
                dao.save((MongoUser) userDetails);
            } catch (MongoException.DuplicateKey e) {
                throw new UsernameAlreadyTakenException(userDetails.getUsername());
            }
        } else {
            throw new JavinRuntimeException("Can not add User that is not instance of MongoUser");
        }
    }

    @Override
    public UserDetails authenticate(Credentials credentials) {
        //todo add to plugins 'customCredentialsSupport'
        return null;
    }
}
