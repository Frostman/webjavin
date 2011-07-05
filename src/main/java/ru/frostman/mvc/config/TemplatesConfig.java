package ru.frostman.mvc.config;

import com.google.common.base.Objects;

/**
 * @author slukjanov aka Frostman
 */
public class TemplatesConfig {
    private String path;
    private long updateInterval;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(long updateInterval) {
        this.updateInterval = updateInterval;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TemplatesConfig) {
            TemplatesConfig config = (TemplatesConfig) obj;

            return Objects.equal(path, config.path) && updateInterval == config.updateInterval;
        }

        return false;
    }
}
