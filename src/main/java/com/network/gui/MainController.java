package com.network.gui;

import com.network.protocols.RecordPlay;
import com.network.protocols.MessageCallBack;
import com.network.protocols.telnet.TelnetRecordPlay;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * MainController.
 *
 * @author ningzhangnj
 */
public class MainController {
    private MainFrame frame;
    private MainModel model;
    private boolean isRecord = false;

    private RecordPlay recordPlay;

    public MainController(MainModel model, MainFrame frame) {
        this.model = model;
        this.frame = frame;
        init();
    }

    private void init() {
        recordPlay = new TelnetRecordPlay(new MessageCallBack() {

            @Override
            public void update(String msg) {
                frame.updateMessage(msg);
            }

            @Override
            public void clear() {
                frame.clearMessage();
            }
        });

        frame.getStopBtn().setEnabled(false);

        frame.getRecordBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshModel();

                frame.getRecordBtn().setEnabled(false);
                frame.getPlayBtn().setEnabled(false);
                frame.getStopBtn().setEnabled(true);

                recordPlay.startRecord(model);
                isRecord = true;
            }
        });

        frame.getPlayBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshModel();

                frame.getRecordBtn().setEnabled(false);
                frame.getPlayBtn().setEnabled(false);
                frame.getStopBtn().setEnabled(true);

                recordPlay.startPlay(model);
                isRecord = false;
            }
        });

        frame.getStopBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshModel();

                frame.getRecordBtn().setEnabled(true);
                frame.getPlayBtn().setEnabled(true);

                if (isRecord) {
                    recordPlay.stopRecord();
                    frame.saveConfiguration();
                } else {
                    recordPlay.stopPlay();
                }

                frame.getStopBtn().setEnabled(false);
            }
        });
    }

    private void refreshModel() {
        model.setLocalPort(Integer.parseInt(frame.getLocalPort()));
        model.setRemoteHost(frame.getRemoteHost());
        model.setRemotePort(Integer.parseInt(frame.getRemotePort()));
    }

    public MainModel getModel() {
        return model;
    }

    public static void main(String[] args) throws Exception {
        MainController controller = new MainController(new MainModel(), new MainFrame());
    }
}
