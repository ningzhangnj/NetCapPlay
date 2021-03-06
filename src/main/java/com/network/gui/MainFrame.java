package com.network.gui;

import com.network.model.UserConfiguration;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import java.awt.*;

/**
 * MainFrame.
 *
 * @author ningzhangnj
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

    private JScrollPane scrollPanel;

    private UserConfiguration userConf;

    public MainFrame() {
        setupSwing();
        setTitle("Network Capture&Play");
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        loadConfiguration();
        init();
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

        scrollPanel = new JScrollPane();
        scrollPanel.setViewportView(cmdsText);
        scrollPanel.getViewport().setPreferredSize(cmdsText.getPreferredSize());
        scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
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

    public void updateMessage(String message) {
        cmdsText.append(message);
        int  height= 10 ;
        Point p = new  Point();
        p.setLocation(0 , cmdsText.getLineCount()*height);
        scrollPanel.getViewport().setViewPosition(p);
    }

    public void clearMessage() {
        cmdsText.setText("");
    }

    public static void main(String[] args) throws Exception {
        MainFrame mainFrame = new MainFrame();
    }
}
