package com.network.protocols.telnet;

/**
 * TelnetMessage.
 *
 * @author enigzhg
 */
public class TelnetMessage {
    //unit: ms
    private long waitTime;

    private String msgContent;

    public TelnetMessage(long waitTime, String msgContent) {
        this.waitTime = waitTime;
        this.msgContent = msgContent;
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
}
