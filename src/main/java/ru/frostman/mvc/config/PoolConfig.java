package ru.frostman.mvc.config;

import com.google.common.base.Objects;

import java.util.concurrent.TimeUnit;

/**
 * @author slukjanov aka Frostman
 */
public class PoolConfig {
    private int corePoolSize = Runtime.getRuntime().availableProcessors() - 1;
    private int maximumPoolSize = Runtime.getRuntime().availableProcessors() - 1;
    private long keepAliveTime = 10;
    private TimeUnit timeUnit = TimeUnit.MINUTES;

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PoolConfig) {
            PoolConfig config = (PoolConfig) obj;

            return corePoolSize == config.corePoolSize
                    && maximumPoolSize == config.maximumPoolSize
                    && keepAliveTime == config.keepAliveTime
                    && Objects.equal(timeUnit, config.timeUnit);
        }

        return false;
    }
}
