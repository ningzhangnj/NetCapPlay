package com.network.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NetUncaughtExceptionHandler.
 *
 * @author enigzhg
 */
public class NetUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(NetUncaughtExceptionHandler.class);

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        try {
            e.printStackTrace();
            logger.error(
                    "Uncaught exception in thread " + t.getName(), e);
        } catch (Throwable e1) {
            e1.printStackTrace();
        }
    }
}
