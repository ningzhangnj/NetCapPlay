package com.network.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * ThreadDispatcher.
 *
 * @author ningzhangnj
 */
public class ThreadDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(ThreadDispatcher.class);

    public static final int CORE_POOL_SIZE = 0;
    //
    private static final int KEEP_ALIVE = 10 * 60;
    //
    public static final int MAX_ACTIVE_TASKS = 500;

    private static ThreadFactory tFactory = new NetThreadFactory(
            ThreadDispatcher.class, "Thread dispatcher (" + CORE_POOL_SIZE + ")");

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAX_ACTIVE_TASKS, KEEP_ALIVE, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(), tFactory);

    public static void dispatch(RunnableDecorator r) {
        executor.execute(r);
    }

    public static Future<?> dispatch2(RunnableDecorator r) {
        return executor.submit(r);
    }


    public static int getEventDispatcherMaxThreads() {
        return executor.getLargestPoolSize();
    }

    public static int getActivethreads() {
        return executor.getActiveCount();
    }

    public static int getCorethreadpoolSize() {
        return executor.getCorePoolSize();
    }

    public static int getPoolSize() {
        return executor.getPoolSize();
    }

    public static int getQueueSize() {
        return executor.getQueue().size();
    }

    public static long getTaskcount() {
        return executor.getTaskCount();
    }

    public static long getTaskrunned() {
        return executor.getCompletedTaskCount();
    }

    /**
     * Causes the currently executing thread to sleep for the specified number of milliseconds.
     * @param millis  the length of time to sleep in milliseconds.
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.debug("Sleep " + millis + "ms is interrupted by " + e.getMessage());
        }
    }
}
