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

package ru.frostman.web.mongo;

import com.google.code.morphia.dao.BasicDAO;
import org.bson.types.ObjectId;
import ru.frostman.web.MongoPlugin;
import ru.frostman.web.mongo.thr.UnsupportedImplementationException;
import ru.frostman.web.secure.thr.UsernameAlreadyTakenException;
import ru.frostman.web.secure.userdetails.Credentials;
import ru.frostman.web.secure.userdetails.UserDetails;
import ru.frostman.web.secure.userdetails.UserService;

/**
 * @author slukjanov aka Frostman
 */
public class MongoUserService extends BasicDAO<User, ObjectId> implements UserService {

    public MongoUserService() {
        //todo remove hard code
        super(User.class, MongoPlugin.getMongo(), MongoPlugin.getMorphia(), "test2");

        //todo think about this
        ensureIndexes();
    }

    @Override
    public UserDetails getUser(String username) {
        return findOne(createQuery().field("username").equal(username));
    }

    @Override
    public void addUser(UserDetails userDetails) throws UsernameAlreadyTakenException {
        if (userDetails == null || userDetails.getUsername() == null) {
            throw new NullPointerException("UserDetails and username field should not be null");
        }

        if(!(userDetails instanceof User)) {
            throw new UnsupportedImplementationException("UserDetails should be inherited from ru.frostman.web.mongo.User");
        }
        User user = (User) userDetails;

        final Class<? extends UserDetails> userDetailsClass = userDetails.getClass();
        if (!MongoPlugin.getMorphia().isMapped(userDetailsClass)) {
            throw new UnsupportedImplementationException("UserDetails implementation should be mapped in Morphia: "
                    + userDetailsClass.getName());
        }

        save(user);
    }

    @Override
    public UserDetails authenticate(Credentials credentials) {
        //todo i think that it's not working
        return findOne(createQuery().field("credentials").hasThisOne(credentials));
    }
}
