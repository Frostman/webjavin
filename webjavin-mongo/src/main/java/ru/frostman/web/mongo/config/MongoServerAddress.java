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
import com.mongodb.DBPort;
import com.mongodb.ServerAddress;

import java.net.UnknownHostException;

/**
 * @author slukjanov aka Frostman
 */
public class MongoServerAddress {
    private String host;
    private int port = DBPort.PORT;

    public MongoServerAddress() {
    }

    public ServerAddress toServerAddress() throws UnknownHostException {
        return new ServerAddress(host, port);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MongoServerAddress) {
            MongoServerAddress address = (MongoServerAddress) obj;

            return port == address.port && Objects.equal(host, address.host);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + port;

        return result;
    }
}
