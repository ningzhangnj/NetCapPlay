package com.network.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * MainController.
 *
 * @author enigzhg
 */
public class MainController {
    private MainFrame frame;
    private MainModel model;
    private boolean isRecord = false;

    public MainController(MainModel model, MainFrame frame) {
        this.model = model;
        this.frame = frame;
        init();
    }

    private void init() {
        frame.getStopBtn().setEnabled(false);

        frame.getRecordBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.getRecordBtn().setEnabled(false);
                frame.getPlayBtn().setEnabled(false);
                frame.getStopBtn().setEnabled(true);

                frame.startRecord();
                isRecord = true;
            }
        });

        frame.getPlayBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.getRecordBtn().setEnabled(false);
                frame.getPlayBtn().setEnabled(false);
                frame.getStopBtn().setEnabled(true);

                frame.startPlay();
                isRecord = false;
            }
        });

        frame.getStopBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.getRecordBtn().setEnabled(true);
                frame.getPlayBtn().setEnabled(true);

                if (isRecord) {
                    frame.stopRecord();
                    frame.saveConfiguration();
                } else {
                    frame.stopPlay();
                }

                frame.getStopBtn().setEnabled(false);
            }
        });

    }

    public static void main(String[] args) throws Exception {
        MainController controller = new MainController(new MainModel(), new MainFrame());
    }
}
