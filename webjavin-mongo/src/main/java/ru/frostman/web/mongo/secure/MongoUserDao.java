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

import com.google.code.morphia.dao.BasicDAO;
import org.bson.types.ObjectId;
import ru.frostman.web.MongoPlugin;
import ru.frostman.web.annotation.Component;
import ru.frostman.web.mongo.config.MongoConfig;

/**
 * @author slukjanov aka Frostman
 */
@Component
public class MongoUserDao extends BasicDAO<MongoUser, ObjectId> {
    public MongoUserDao() {
        super(MongoUser.class, MongoPlugin.getMongo(), MongoPlugin.getMorphia(), MongoConfig.get().getDbName());

        //todo think about this, it will be invoke each time constructor called, dao should be singleton
        ensureIndexes();
    }

    public MongoUser getByName(String username) {
        return find(createQuery().field("username").equal(username)).get();
    }
}
