package ru.frostman.mvc.config;

import com.google.common.base.Objects;

/**
 * @author slukjanov aka Frostman
 */
public class TemplatesConfig {
    private String path;
    private int updateInterval = 1000;

    private int maxCacheStrongSize = 25;
    private int maxCacheSoftSize = 250;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    public int getMaxCacheStrongSize() {
        return maxCacheStrongSize;
    }

    public void setMaxCacheStrongSize(int maxCacheStrongSize) {
        this.maxCacheStrongSize = maxCacheStrongSize;
    }

    public int getMaxCacheSoftSize() {
        return maxCacheSoftSize;
    }

    public void setMaxCacheSoftSize(int maxCacheSoftSize) {
        this.maxCacheSoftSize = maxCacheSoftSize;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TemplatesConfig) {
            TemplatesConfig config = (TemplatesConfig) obj;

            return Objects.equal(path, config.path)
                    && updateInterval == config.updateInterval
                    && maxCacheStrongSize == config.maxCacheStrongSize
                    && maxCacheSoftSize == config.maxCacheSoftSize;
        }

        return false;
    }
}
