package com.network.protocols;

/**
 * RelayMessage.
 *
 * @author enigzhg
 */
public interface RelayMessage<T> {
    void relay(T message);
}
