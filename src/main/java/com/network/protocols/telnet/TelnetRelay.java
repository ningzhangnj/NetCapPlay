package com.network.protocols.telnet;

import com.network.listener.MessageListener;
import com.network.mq.MessageEvent;
import com.network.worker.RunnableDecorator;
import com.network.worker.ThreadDispatcher;
import io.netty.channel.Channel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * TelnetRelay.
 *
 * @author enigzhg
 */
public class TelnetRelay implements MessageListener<TelnetMessage> {
    private Channel clientChannel;

    private Channel serverChannel;

    private BlockingQueue<TelnetMessage> clientMsgQueue = new LinkedBlockingQueue<TelnetMessage>();

    private BlockingQueue<TelnetMessage> serverMsgQueue = new LinkedBlockingQueue<TelnetMessage>();

    private String cache = "";

    public TelnetRelay() {
        init();
    }

    public TelnetRelay(Channel client, Channel server) {
        this.clientChannel = client;
        this.serverChannel = server;
        init();
    }

    private void init() {
        ThreadDispatcher.dispatch(new RunnableDecorator("Client message loop handling.", new Runnable() {

            @Override
            public void run() {
                clientMessageHandlingLoop();
            }
        }));

        ThreadDispatcher.dispatch(new RunnableDecorator("Server message loop handling.", new Runnable() {

            @Override
            public void run() {
                serverMessageHandlingLoop();
            }
        }));

    }

    private void clientMessageHandlingLoop() {
        while (true) {
            try {
                TelnetMessage msg = this.clientMsgQueue.take();
                if (serverChannel != null) {
                    serverChannel.write(msg.getMsgContent() + "\r\n");
                    serverChannel.flush();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
    }

    private void serverMessageHandlingLoop() {
        while (true) {
            try {
                TelnetMessage msg = this.serverMsgQueue.take();
                if (clientChannel != null) {
                    clientChannel.write(msg.getMsgContent() + "\r\n");
                    clientChannel.flush();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public Channel getClientChannel() {
        return clientChannel;
    }

    public void setClientChannel(Channel clientChannel) {
        this.clientChannel = clientChannel;
    }

    public Channel getServerChannel() {
        return serverChannel;
    }

    public void setServerChannel(Channel serverChannel) {
        this.serverChannel = serverChannel;
    }

    @Override
    public void onClientReceiveMessage(TelnetMessage msg) {
        this.clientMsgQueue.offer(msg);
    }

    @Override
    public void onServerReceiveMessage(TelnetMessage msg) {
        this.serverMsgQueue.offer(msg);
    }
}
