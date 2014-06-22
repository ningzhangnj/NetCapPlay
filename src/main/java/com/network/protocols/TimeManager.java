package com.network.protocols;

/**
 * TimeManager.
 *
 * @author enigzhg
 */
public class TimeManager {

    private static TimeManager instance = null;

    private long lastTime;

    private boolean started = false;

    private TimeManager() {}

    public static synchronized TimeManager getInstance() {
        if (instance == null) {
            instance = new TimeManager();
        }
        return instance;
    }

    public synchronized void startCount() {
        if (!started) {
            started = true;
            lastTime = System.currentTimeMillis();
        }
    }

    public synchronized long getCurrentTimeDiff() {
        long currentTime = System.currentTimeMillis();
        long result = currentTime - lastTime;
        lastTime = currentTime;
        return result;
    }

    public synchronized void stopCount() {
        started = false;
    }

    public synchronized boolean isStarted() {
        return started;
    }
}
