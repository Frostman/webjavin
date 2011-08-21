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

package ru.frostman.web.mongo.config;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.mongodb.ServerAddress;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import ru.frostman.web.thr.JavinRuntimeException;
import ru.frostman.web.util.Resources;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;

/**
 * @author slukjanov aka Frostman
 */
public class MongoConfig {
    private static MongoConfig currentConfig;

    private List<MongoServerAddress> replicaSet = Lists.newArrayList(new MongoServerAddress());
    private String dbName;

    static {
        update();
    }

    public synchronized static boolean update() {
        try {
            Constructor constructor = new Constructor(MongoConfig.class);
            TypeDescription configDescription = new TypeDescription(MongoConfig.class);
            configDescription.putListPropertyType("replicaSet", MongoServerAddress.class);
            constructor.addTypeDescription(configDescription);

            Yaml yaml = new Yaml(constructor);
            MongoConfig config = (MongoConfig) yaml.load(getConfigStream());

            boolean changed = false;
            if (!config.equals(currentConfig)) {
                changed = true;

                currentConfig = config;
            }

            return changed;
        } catch (Exception e) {
            throw new JavinRuntimeException("Can't load mongo configuration", e);
        }
    }

    public static MongoConfig getCurrentConfig() {
        return currentConfig;
    }

    private static InputStream getConfigStream() {
        return Resources.getResourceAsStream("/mongo.yaml");
    }

    public List<ServerAddress> getMongoReplicaSet() throws UnknownHostException {
        List<ServerAddress> addresses = Lists.newLinkedList();

        for (MongoServerAddress address : replicaSet) {
            addresses.add(address.toServerAddress());
        }

        return addresses;
    }

    public List<MongoServerAddress> getReplicaSet() {
        return replicaSet;
    }

    public void setReplicaSet(List<MongoServerAddress> replicaSet) {
        this.replicaSet = replicaSet;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MongoConfig) {
            MongoConfig config = (MongoConfig) obj;

            return Objects.equal(replicaSet, config.replicaSet)
                    && Objects.equal(dbName, config.dbName);
        }

        return false;
    }
}
