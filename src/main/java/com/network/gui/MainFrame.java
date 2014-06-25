package com.network.gui;

import com.network.listener.MessageListener;
import com.network.model.UserConfiguration;
import com.network.mq.MessageEvent;
import com.network.mq.MessageQueue;
import com.network.mq.MessageQueueLock;
import com.network.protocols.TimeManager;
import com.network.protocols.telnet.TelnetClient;
import com.network.protocols.telnet.TelnetMessage;
import com.network.protocols.telnet.TelnetRelay;
import com.network.protocols.telnet.TelnetServer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * MainFrame.
 *
 * @author enigzhg
 */
public class MainFrame extends JFrame {
    public static final int DEFAULT_WIDTH = 600;
    public static final int DEFAULT_HEIGHT = 600;
    public static final int DEFAULT_GAP = 20;

    private GridBagLayout gridBagLayout = new GridBagLayout();
    private GridBagLayout gridBagLayout1 = new GridBagLayout();

    private JPanel jPanel1 = new JPanel();

    private JLabel jLabel1 = new JLabel();
    private JLabel jLabel2 = new JLabel();
    private JLabel jLabel3 = new JLabel();

    private JTextField jLocalPort;
    private JTextField jRemotePort;
    private JTextField jRemoteHost;

    private JButton recordBtn;
    private JButton playBtn;
    private JButton stopBtn;

    private JTextArea cmdsText;

    private UserConfiguration userConf;

    private List<String> cmds = new ArrayList<String>();

    private TelnetServer server;

    private TelnetClient client;

    private int playIndex = 0;

    MessageQueue mq1 = new MessageQueue(100,new MessageQueueLock());

    public MainFrame() {
        setupSwing();
        setTitle("Network Capture&Play");
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        loadConfiguration();
        init();
        mq1.start();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void loadConfiguration() {
        userConf = new UserConfiguration();
    }

    public void saveConfiguration() {
        userConf.setLocalPort(this.getLocalPort());
        userConf.setRemoteIp(this.getRemoteHost());
        userConf.setRemotePort(this.getRemotePort());
        userConf.save();
    }

    private void  init() {
        this.setLayout(gridBagLayout);

        jLabel1.setText("Local port:");
        jLabel2.setText("Remote host:");
        jLabel3.setText("Remote port:");

        jLocalPort = new JTextField(userConf.getLocalPort());
        jRemoteHost = new JTextField(userConf.getRemoteIp());
        jRemotePort = new JTextField(userConf.getRemotePort());

        recordBtn = new JButton("Record");
        playBtn = new JButton("Play");
        stopBtn = new JButton("Stop");

        jPanel1.setLayout(gridBagLayout1);

        this.add(jPanel1, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(
                5, 5, 5, 5), 0, 0));
        jPanel1.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
                5, 5, 5), 0, 0));
        jPanel1.add(jLocalPort, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
                5, 5, 5), 200, 0));
        jPanel1.add(jLabel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
                5, 5, 5), 0, 0));
        jPanel1.add(jRemoteHost, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
                5, 5, 5), 200, 0));
        jPanel1.add(jLabel3, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
                5, 5, 5), 0, 0));
        jPanel1.add(jRemotePort, new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
                5, 5, 5), 200, 0));
        jPanel1.add(recordBtn, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
                5, 5, 5), 0, 0));
        jPanel1.add(playBtn, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
                5, 5, 5), 50, 0));
        jPanel1.add(stopBtn, new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
                5, 5, 5), 50, 0));

        cmdsText = new JTextArea();
        cmdsText.setSize(400, 400);

        JScrollPane scrollPanel = new JScrollPane();
        scrollPanel.setViewportView(cmdsText);
        scrollPanel.getViewport().setPreferredSize(cmdsText.getPreferredSize());
        this.add(scrollPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(
                5, 5, 5, 5), 400, 400));


    }

    public String getLocalPort() {
        return jLocalPort.getText();
    }

    public String getRemotePort() {
        return jRemotePort.getText();
    }

    public String getRemoteHost() {
        return jRemoteHost.getText();
    }

    public JButton getRecordBtn() {
        return recordBtn;
    }

    public JButton getPlayBtn() {
        return playBtn;
    }

    public JButton getStopBtn() {
        return stopBtn;
    }

    public void startRecord() {
        TelnetRelay relay =  new TelnetRelay();
        server = new TelnetServer(Integer.valueOf(getLocalPort()), relay);
        client = new TelnetClient(getRemoteHost(), Integer.valueOf(getRemotePort()), relay);
        cmdsText.removeAll();
        cmds.clear();

        server.addMessageListener(new MessageListener<TelnetMessage>() {

            @Override
            public void onClientReceiveMessage(TelnetMessage msg) {

            }

            @Override
            public void onServerReceiveMessage(TelnetMessage msg) {
                String result =  String.format("<<<%7d<<< %s\n", msg.getWaitTime(), msg.getMsgContent());
                cmdsText.append(result);
                cmds.add(result);
            }
        });
        client.addMessageListener(new MessageListener<TelnetMessage>() {

            @Override
            public void onClientReceiveMessage(TelnetMessage msg) {
                String result = String.format(">>>%7d>>> %s\n", msg.getWaitTime(), msg.getMsgContent());
                cmdsText.append(result);
                cmds.add(result);
            }

            @Override
            public void onServerReceiveMessage(TelnetMessage msg) {

            }
        });
        server.start();
        client.start();
    }

    public void startPlay() {
        File file = null;
        BufferedReader in = null;
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CAP txt file", "captxt");
        fileChooser.setFileFilter(filter);
        List<String> cachedCmds =  new ArrayList<String>();
        int sel = fileChooser.showOpenDialog(null);

        if (sel == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        }

        if (file != null) {
            try {
                in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line;
                while ((line = in.readLine()) != null) {
                    cachedCmds.add(line);
                }
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        }
        final List<TelnetMessage> records = new ArrayList<TelnetMessage>();
        for (String cachedCmd : cachedCmds) {
            records.add(parseCmd(cachedCmd));
        }

        TelnetRelay relay =  new TelnetRelay();
        server = new TelnetServer(Integer.valueOf(getLocalPort()), relay);
        cmdsText.removeAll();
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
                cmdsText.append(result);
            }
        });
        server.start();
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
            cmdsText.append(result);
            relay.respond(message);
            return message;
        }

        @Override
        public boolean isSame(MessageEvent msg) {
            return false;
        }
    }

    public void stopPlay() {
        server.stop();
    }

    public void stopRecord() {
        server.stop();
        client.stop();
        File file = null;
        OutputStream out = null;
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CAP txt file", "captxt");
        fileChooser.setFileFilter(filter);
        int sel = fileChooser.showSaveDialog(null);

        if (sel == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        }

        if (file != null) {
            try {
                out = new FileOutputStream(file, false);
                for (String cmd:cmds) {
                    out.write(cmd.getBytes());
                }
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        }
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

    private void setupSwing() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel("de.muntjak.tinylookandfeel.TinyLookAndFeel");
            } catch (Exception e2) {
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                } catch (Exception e1) {
                    MetalLookAndFeel.setCurrentTheme(new OceanTheme());
                }
            }
        }
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
    }

    public static void main(String[] args) throws Exception {
        MainFrame mainFrame = new MainFrame();
    }
}
