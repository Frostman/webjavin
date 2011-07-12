package ru.frostman.mvc.config;

import com.google.common.base.Objects;

/**
 * @author slukjanov aka Frostman
 */
public class AppConfig {
    private int asyncQueueLength = 100;
    private int maxForwardsCount = 5;
    private PoolConfig pool = new PoolConfig();

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

            return asyncQueueLength == config.asyncQueueLength
                    && maxForwardsCount == config.maxForwardsCount
                    && Objects.equal(pool, config.pool);
        }

        return false;
    }
}
