package ru.frostman.mvc.config;

import com.google.common.base.Objects;

import java.util.List;

/**
 * @author slukjanov aka Frostman
 */
public class ClassesConfig {
    private List<String> packages;
    private long updateInterval;

    public List<String> getPackages() {
        return packages;
    }

    public void setPackages(List<String> packages) {
        this.packages = packages;
    }

    public long getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(long updateInterval) {
        this.updateInterval = updateInterval;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClassesConfig) {
            ClassesConfig config = (ClassesConfig) obj;

            return Objects.equal(packages, config.packages) && updateInterval == config.updateInterval;
        }

        return false;
    }
}
