package com.network.mq;

/**
 * MessageEvent.
 *
 * @author ningzhangnj
 */
public interface MessageEvent {
    /**
     * Implement this method to handle the message.
     * @return the result of message handing. Currently it only make sense when triggering one immediate message.
     */
    public Object handle();

    /**
     * This method is similar to {@link Object#equals(Object)}. One special case.
     * @param msg one {@link MessageEvent} instance to compare.
     * @return  true if the {@link MessageEvent} instance to compare is same message.
     */
    public boolean isSame(MessageEvent msg);
}
