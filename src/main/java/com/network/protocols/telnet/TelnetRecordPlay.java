package com.network.protocols.telnet;

import com.network.gui.MainModel;
import com.network.protocols.MessageCallBack;
import com.network.listener.MessageListener;
import com.network.mq.MessageEvent;
import com.network.mq.MessageQueue;
import com.network.mq.MessageQueueLock;
import com.network.protocols.RecordPlay;
import com.network.protocols.CommandsUtil;
import com.network.protocols.TimeManager;

import java.util.ArrayList;
import java.util.List;

/**
 * TelnetRecordPlay.
 *
 * @author ningzhangnj
 */
public class TelnetRecordPlay implements RecordPlay {
    private TelnetServer server;

    private TelnetClient client;

    private List<String> cmds = new ArrayList<String>();

    MessageQueue mq1 = new MessageQueue(100,new MessageQueueLock());

    private int playIndex = 0;

    private MessageCallBack callBack;

    public TelnetRecordPlay(MessageCallBack callBack) {
        this.callBack = callBack;
        mq1.start();
    }

    @Override
    public void startRecord(MainModel model) {
        TelnetRelay relay =  new TelnetRelay();
        server = new TelnetServer(model.getLocalPort(), relay);
        client = new TelnetClient(model.getRemoteHost(), model.getRemotePort(), relay);
        if (callBack != null)  callBack.clear();
        cmds.clear();

        server.addMessageListener(new MessageListener<TelnetMessage>() {

            @Override
            public void onClientReceiveMessage(TelnetMessage msg) {

            }

            @Override
            public void onServerReceiveMessage(TelnetMessage msg) {
                String result =  String.format("<<<%7d<<< %s\n", msg.getWaitTime(), msg.getMsgContent());
                if (callBack != null)  callBack.update(result);
                cmds.add(result);
            }
        });
        client.addMessageListener(new MessageListener<TelnetMessage>() {

            @Override
            public void onClientReceiveMessage(TelnetMessage msg) {
                String result = String.format(">>>%7d>>> %s\n", msg.getWaitTime(), msg.getMsgContent());
                if (callBack != null)  callBack.update(result);
                cmds.add(result);
            }

            @Override
            public void onServerReceiveMessage(TelnetMessage msg) {

            }
        });
        server.start();
        client.start();
    }

    @Override
    public void stopRecord() {
        server.stop();
        client.stop();
        CommandsUtil.saveRecords(cmds);
    }

    @Override
    public void startPlay(MainModel model) {
        List<String> cachedCmds = CommandsUtil.loadRecords();
        final List<TelnetMessage> records = new ArrayList<TelnetMessage>();
        for (String cachedCmd : cachedCmds) {
            records.add(parseCmd(cachedCmd));
        }

        TelnetRelay relay =  new TelnetRelay();
        server = new TelnetServer(model.getLocalPort(), relay);
        if (callBack != null)  callBack.clear();
        cmds.clear();

        playIndex = 0;

        server.addMessageListener(new MessageListener<TelnetMessage>() {

            @Override
            public void onClientReceiveMessage(TelnetMessage msg) {

            }

            @Override
            public void onServerReceiveMessage(TelnetMessage msg) {
                playMessage(records, msg);
                String result =  String.format("<<<%7d<<< %s\n", msg.getWaitTime(), msg.getMsgContent());
                if (callBack != null)  callBack.update(result);
            }
        });
        server.start();
    }

    @Override
    public void stopPlay() {
        server.stop();
    }

    private TelnetMessage parseCmd(String cmd) {
        boolean isDirection = false;
        if (cmd.startsWith("<<<")) {
            isDirection = true;
        }

        Integer timeGap =  Integer.parseInt(cmd.substring(3, 10).trim());
        String content = cmd.substring(14);
        return new TelnetMessage(timeGap, content, isDirection);
    }

    private void playMessage(List<TelnetMessage> records, TelnetMessage receiveMsg) {
        boolean isFound = false;
        while (true) {
            if (playIndex > (records.size() -1)) break;

            if (records.get(playIndex).isDirection()
                    && records.get(playIndex).getMsgContent().equals(receiveMsg.getMsgContent())) {
                isFound = true;

                long wait = 0;
                while(true) {
                    playIndex++;
                    if (playIndex > (records.size() -1)) break;
                    if (!records.get(playIndex).isDirection()) {
                        wait += records.get(playIndex).getWaitTime();
                        mq1.send(wait, new ResponseMeassageEvent(records.get(playIndex), server.getRelay()));
                    }
                    else break;
                }

            }
            if (isFound) break;

            playIndex++;
        }
    }

    private class ResponseMeassageEvent implements MessageEvent {
        private TelnetMessage message;
        private TelnetRelay relay;

        private ResponseMeassageEvent(TelnetMessage message, TelnetRelay relay) {
            this.message = message;
            this.relay = relay;
        }

        @Override
        public Object handle() {
            String result =  String.format(">>>%7d>>> %s\n", TimeManager.getInstance().getCurrentTimeDiff(), message.getMsgContent());
            if (callBack != null)  callBack.update(result);
            relay.respond(message);
            return message;
        }

        @Override
        public boolean isSame(MessageEvent msg) {
            return false;
        }
    }
}
