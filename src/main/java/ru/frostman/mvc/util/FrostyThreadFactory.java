package ru.frostman.mvc.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author slukjanov aka Frostman
 */
public class FrostyThreadFactory implements ThreadFactory {

    private AtomicInteger threadNumber = new AtomicInteger();
    private ThreadGroup group;
    private final String threadNamePrefix;

    public FrostyThreadFactory(String poolName) {
        SecurityManager s = System.getSecurityManager();
        ThreadGroup parentGroup = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        group = new ThreadGroup(parentGroup, poolName);
        threadNamePrefix = poolName + "-thread-";
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(group, runnable, threadNamePrefix + threadNumber.getAndIncrement());

        if (thread.isDaemon()) {
            thread.setDaemon(false);
        }

        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }

        return thread;
    }
}
