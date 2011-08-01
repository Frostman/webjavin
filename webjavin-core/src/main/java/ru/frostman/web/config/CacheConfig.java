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

/**
 * @author slukjanov aka Frostman
 */
public class CacheConfig {

    private String name = "javin-cache-manager";

    private String diskStorePath = "user.dir/javin-cache";

    private int maxElementsInMemory = 10000;

    private boolean eternal = false;

    private long timeToIdleSeconds = 120;

    private long timeToLiveSeconds = 120;

    private boolean overflowToDisk = true;

    private int maxElementsOnDisk = 1000000;

    private boolean diskPersistent = false;

    private int diskSpoolBufferSizeMB = 30;

    private long diskExpiryThreadIntervalSeconds = 120;

    private String memoryStoreEvictionPolicy = "LRU";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiskStorePath() {
        return diskStorePath;
    }

    public void setDiskStorePath(String diskStorePath) {
        this.diskStorePath = diskStorePath;
    }

    public int getMaxElementsInMemory() {
        return maxElementsInMemory;
    }

    public void setMaxElementsInMemory(int maxElementsInMemory) {
        this.maxElementsInMemory = maxElementsInMemory;
    }

    public boolean isEternal() {
        return eternal;
    }

    public void setEternal(boolean eternal) {
        this.eternal = eternal;
    }

    public long getTimeToIdleSeconds() {
        return timeToIdleSeconds;
    }

    public void setTimeToIdleSeconds(long timeToIdleSeconds) {
        this.timeToIdleSeconds = timeToIdleSeconds;
    }

    public long getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    public void setTimeToLiveSeconds(long timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }

    public boolean isOverflowToDisk() {
        return overflowToDisk;
    }

    public void setOverflowToDisk(boolean overflowToDisk) {
        this.overflowToDisk = overflowToDisk;
    }

    public int getMaxElementsOnDisk() {
        return maxElementsOnDisk;
    }

    public void setMaxElementsOnDisk(int maxElementsOnDisk) {
        this.maxElementsOnDisk = maxElementsOnDisk;
    }

    public boolean isDiskPersistent() {
        return diskPersistent;
    }

    public void setDiskPersistent(boolean diskPersistent) {
        this.diskPersistent = diskPersistent;
    }

    public int getDiskSpoolBufferSizeMB() {
        return diskSpoolBufferSizeMB;
    }

    public void setDiskSpoolBufferSizeMB(int diskSpoolBufferSizeMB) {
        this.diskSpoolBufferSizeMB = diskSpoolBufferSizeMB;
    }

    public long getDiskExpiryThreadIntervalSeconds() {
        return diskExpiryThreadIntervalSeconds;
    }

    public void setDiskExpiryThreadIntervalSeconds(long diskExpiryThreadIntervalSeconds) {
        this.diskExpiryThreadIntervalSeconds = diskExpiryThreadIntervalSeconds;
    }

    public String getMemoryStoreEvictionPolicy() {
        return memoryStoreEvictionPolicy;
    }

    public void setMemoryStoreEvictionPolicy(String memoryStoreEvictionPolicy) {
        this.memoryStoreEvictionPolicy = memoryStoreEvictionPolicy;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CacheConfig) {
            CacheConfig config = (CacheConfig) obj;

            return Objects.equal(name, config.name)
                    && Objects.equal(diskStorePath, config.diskStorePath)
                    && maxElementsInMemory == config.maxElementsInMemory
                    && eternal == config.eternal
                    && timeToIdleSeconds == config.timeToIdleSeconds
                    && timeToLiveSeconds == config.timeToLiveSeconds
                    && overflowToDisk == config.overflowToDisk
                    && maxElementsOnDisk == config.maxElementsOnDisk
                    && diskPersistent == config.diskPersistent
                    && diskSpoolBufferSizeMB == config.diskSpoolBufferSizeMB
                    && diskExpiryThreadIntervalSeconds == config.diskExpiryThreadIntervalSeconds
                    && Objects.equal(memoryStoreEvictionPolicy, config.memoryStoreEvictionPolicy);
        }

        return false;
    }


}
