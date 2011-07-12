package ru.frostman.mvc.util;

import ru.frostman.mvc.config.FrostyConfig;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author slukjanov aka Frostman
 */
public class FrostyInvoker {
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            FrostyConfig.getCurrentConfig().getApp().getPool().getCorePoolSize(),
            FrostyConfig.getCurrentConfig().getApp().getPool().getMaximumPoolSize(),
            FrostyConfig.getCurrentConfig().getApp().getPool().getKeepAliveTime(),
            FrostyConfig.getCurrentConfig().getApp().getPool().getTimeUnit(),
            new LinkedBlockingQueue<Runnable>(), new FrostyThreadFactory("frosty-executor")
    );

    public void execute(Runnable command) {
        executor.execute(command);
    }

    public Future<?> submit(Runnable task) {
        return executor.submit(task);
    }

    public <T> Future<T> submit(Runnable task, T result) {
        return executor.submit(task, result);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }

    public int getQueueSize() {
        return executor.getQueue().size();
    }
}
