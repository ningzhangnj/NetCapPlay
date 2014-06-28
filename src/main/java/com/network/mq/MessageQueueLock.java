package com.network.mq;

import java.util.concurrent.locks.ReentrantLock;

/**
 * MessageQueueLock.
 *
 * @author ningzhangnj
 */
public class MessageQueueLock extends ReentrantLock {
    public MessageQueueLock() {
        super();
    }

    @Override
    public void lock() {
        startProtect();
    }

    @Override
    public void unlock() {
        stopProtect();
    }

    public void startProtect() {
        super.lock();
    }

    public void stopProtect() {
        super.unlock();
    }
}
