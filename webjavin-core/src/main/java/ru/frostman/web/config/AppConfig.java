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

package ru.frostman.web.config;

import com.google.common.base.Objects;

import java.util.UUID;

/**
 * @author slukjanov aka Frostman
 */
public class AppConfig {
    //todo think about default value and checking user value
    private String secret = UUID.randomUUID().toString();
    private int asyncQueueLength = 100;
    private int maxForwardsCount = 5;
    private String serverHeader = "WebJavin";
    private String sessionManager = "ru.frostman.web.session.impl.ServletSessionManager";
    private PoolConfig pool = new PoolConfig();

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getAsyncQueueLength() {
        return asyncQueueLength;
    }

    public void setAsyncQueueLength(int asyncQueueLength) {
        this.asyncQueueLength = asyncQueueLength;
    }

    public int getMaxForwardsCount() {
        return maxForwardsCount;
    }

    public void setMaxForwardsCount(int maxForwardsCount) {
        this.maxForwardsCount = maxForwardsCount;
    }

    public String getServerHeader() {
        return serverHeader;
    }

    public void setServerHeader(String serverHeader) {
        this.serverHeader = serverHeader;
    }

    public String getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager(String sessionManager) {
        this.sessionManager = sessionManager;
    }

    public PoolConfig getPool() {
        return pool;
    }

    public void setPool(PoolConfig pool) {
        this.pool = pool;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AppConfig) {
            AppConfig config = (AppConfig) obj;

            return Objects.equal(secret, config.secret)
                    && asyncQueueLength == config.asyncQueueLength
                    && maxForwardsCount == config.maxForwardsCount
                    && Objects.equal(serverHeader, config.serverHeader)
                    && Objects.equal(sessionManager, config.sessionManager)
                    && Objects.equal(pool, config.pool);
        }

        return false;
    }
}
