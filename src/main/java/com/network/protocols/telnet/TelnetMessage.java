package com.network.protocols.telnet;

/**
 * TelnetMessage.
 *
 * @author ningzhangnj
 */
public class TelnetMessage {
    //unit: ms
    private long waitTime;

    private String msgContent;

    private boolean direction;

    public TelnetMessage(long waitTime, String msgContent) {
        this(waitTime, msgContent, false);
    }

    public TelnetMessage(long waitTime, String msgContent, boolean direction) {
        this.waitTime = waitTime;
        this.msgContent = msgContent;
        this.direction = direction;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public boolean isDirection() {
        return direction;
    }

    public void setDirection(boolean direction) {
        this.direction = direction;
    }
}
