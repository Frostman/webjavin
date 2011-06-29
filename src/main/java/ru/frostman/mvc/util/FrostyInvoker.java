package ru.frostman.mvc.util;

import java.util.concurrent.*;

/**
 * @author slukjanov aka Frostman
 */
public class FrostyInvoker {
    //todo remove hard code
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() - 1, Runtime.getRuntime().availableProcessors() - 1,
            10, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(), new FrostyThreadFactory("frosty-executor")
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
