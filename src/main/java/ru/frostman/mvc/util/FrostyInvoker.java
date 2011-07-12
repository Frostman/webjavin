/******************************************************************************
 * Frosty - MVC framework.                                                    *
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
