package com.network.worker;

/**
 * RunnableDecorator.
 *
 * @author enigzhg
 */
public class RunnableDecorator implements Runnable {
    private String name;

    private Runnable runnable;

    public RunnableDecorator(String threadName, Runnable r) {
        this.name = threadName;
        this.runnable = r;
    }

    @Override
    public void run() {
        Thread t = Thread.currentThread();
        String oldName = t.getName();

        t.setName(name + " - RUNNING");

        try {
            runnable.run();
        } finally {
            t.setName(oldName);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}