package com.network.worker;

import java.util.concurrent.ThreadFactory;

/**
 * NetThreadFactory.
 *
 * @author enigzhg
 */
public class NetThreadFactory implements ThreadFactory {
    private String className;

    private int cnt;

    private ThreadGroup threadGroup = new ThreadGroup("RTS");

    public static final Thread.UncaughtExceptionHandler exHandler = new NetUncaughtExceptionHandler();

    public NetThreadFactory(Class<?> clazz, String threadGroupName) {
        this.threadGroup = new ThreadGroup(threadGroupName);
        this.className = clazz.getSimpleName();
    }

    @Override
    public Thread newThread(Runnable r) {
        String name = className + "-" + (cnt++);
        Thread t = new Thread(threadGroup, r, name);
        t.setUncaughtExceptionHandler(exHandler);
        return t;
    }
}
