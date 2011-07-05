package ru.frostman.mvc.config;

/**
 * @author slukjanov aka Frostman
 */
public class AppConfig {
    private int asyncQueueLength;

    public int getAsyncQueueLength() {
        return asyncQueueLength;
    }

    public void setAsyncQueueLength(int asyncQueueLength) {
        this.asyncQueueLength = asyncQueueLength;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AppConfig) {
            AppConfig config = (AppConfig) obj;

            return asyncQueueLength == config.asyncQueueLength;
        }

        return false;
    }
}
