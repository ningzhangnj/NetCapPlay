package com.network.protocols;

import com.network.listener.MessageListener;

import java.util.ArrayList;
import java.util.List;

/**
 * AbstractProtocol.
 *
 * @author ningzhangnj
 */
public abstract class AbstractProtocol<T> {
    private final List<MessageListener<T>> listeners = new ArrayList<MessageListener<T>>();

    public void addMessageListener(MessageListener<T> listener) {
        listeners.add(listener);
    }

    public void removeMessageListener(MessageListener<T> listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    public void clearMessageListeners() {
        listeners.clear();
    }

    public List<MessageListener<T>> getListeners() {
        return listeners;
    }
}
