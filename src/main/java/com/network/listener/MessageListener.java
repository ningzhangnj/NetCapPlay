package com.network.listener;

/**
 * MessageListener.
 *
 * @author enigzhg
 */
public interface MessageListener<T> {
    public void onClientReceiveMessage(T msg);

    public void onServerReceiveMessage(T msg);
}
