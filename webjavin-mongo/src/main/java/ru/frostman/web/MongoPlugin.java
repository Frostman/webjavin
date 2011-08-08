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

package ru.frostman.web;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;
import com.google.common.collect.Lists;
import com.mongodb.Mongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.frostman.web.classloading.AppClass;
import ru.frostman.web.mongo.config.MongoConfig;
import ru.frostman.web.mongo.secure.LoginPasswordCredentials;
import ru.frostman.web.mongo.secure.User;
import ru.frostman.web.mongo.secure.UserRole;
import ru.frostman.web.plugin.Plugin;
import ru.frostman.web.thr.JavinRuntimeException;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * @author slukjanov aka Frostman
 */
public class MongoPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(MongoPlugin.class);
    private static boolean firstLoad = true;

    private static Mongo mongo;
    private static Morphia morphia;

    public MongoPlugin() {
        super(1);
    }

    @Override
    public boolean reload() {
        if (firstLoad) {
            MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);
            firstLoad = false;
        }

        return MongoConfig.update();
    }

    @Override
    public void afterClassesEnhance(Map<String, AppClass> classes) {
        List<Class> morphiaClasses = Lists.newLinkedList();
        Morphia morphia = new Morphia();

        for (Map.Entry<String, AppClass> entry : classes.entrySet()) {
            Class<?> clazz = entry.getValue().getJavaClass();

            if (clazz.getAnnotation(Entity.class) != null
                    || clazz.getAnnotation(Embedded.class) != null) {
                morphiaClasses.add(clazz);
                morphia.map(clazz);
            }
        }

        morphia.map(User.class).map(UserRole.class).map(LoginPasswordCredentials.class);

        try {
            mongo = new Mongo(MongoConfig.getCurrentConfig().getMongoReplicaSet());

            String mongoVersion = mongo.getVersion();
            log.debug("Successfully connected to MongoDB v." + mongoVersion);
        } catch (UnknownHostException e) {
            throw new JavinRuntimeException("Can't connect to MongoDB", e);
        }
        MongoPlugin.morphia = morphia;

        log.info("Mongo plugin loaded successfully");
    }

    public static Mongo getMongo() {
        return mongo;
    }

    public static Morphia getMorphia() {
        return morphia;
    }
}
