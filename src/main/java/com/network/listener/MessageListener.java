package com.network.listener;

/**
 * MessageListener.
 *
 * @author ningzhangnj
 */
public interface MessageListener<T> {
    public void onClientReceiveMessage(T msg);

    public void onServerReceiveMessage(T msg);
}
