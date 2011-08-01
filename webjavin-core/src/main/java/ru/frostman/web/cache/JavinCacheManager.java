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

package ru.frostman.web.cache;

import com.google.common.base.Objects;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import ru.frostman.web.config.CacheConfig;
import ru.frostman.web.config.JavinConfig;

/**
 * @author slukjanov aka Frostman
 */
public class JavinCacheManager {

    private static CacheManager manager;

    private static CacheConfig currentCacheConfig;

    public static boolean update() {
        CacheConfig cacheConfig = JavinConfig.get().getCache();
        if (!Objects.equal(currentCacheConfig, cacheConfig)) {
            if (manager != null) {
                manager.removalAll();
            }

            currentCacheConfig = cacheConfig;

            CacheConfiguration defaultCacheConfiguration = new CacheConfiguration()
                    .maxElementsInMemory(currentCacheConfig.getMaxElementsInMemory())
                    .eternal(currentCacheConfig.isEternal())
                    .timeToIdleSeconds(currentCacheConfig.getTimeToIdleSeconds())
                    .timeToLiveSeconds(currentCacheConfig.getTimeToLiveSeconds())
                    .overflowToDisk(currentCacheConfig.isOverflowToDisk())
                    .maxElementsOnDisk(currentCacheConfig.getMaxElementsOnDisk())
                    .diskPersistent(currentCacheConfig.isDiskPersistent())
                    .diskSpoolBufferSizeMB(currentCacheConfig.getDiskSpoolBufferSizeMB())
                    .diskExpiryThreadIntervalSeconds(currentCacheConfig.getDiskExpiryThreadIntervalSeconds())
                    .memoryStoreEvictionPolicy(currentCacheConfig.getMemoryStoreEvictionPolicy());

            manager = new CacheManager(new Configuration()
                    .name(currentCacheConfig.getName())
                    .diskStore(new DiskStoreConfiguration().path(currentCacheConfig.getDiskStorePath()))
                    .defaultCache(defaultCacheConfiguration));

            return true;
        }

        return false;
    }


}
