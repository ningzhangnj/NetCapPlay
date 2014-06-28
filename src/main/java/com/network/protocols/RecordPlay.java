package com.network.protocols;

import com.network.gui.MainModel;

/**
 * RecordPlay.
 *
 * @author ningzhangnj
 */
public interface RecordPlay {
    void startRecord(MainModel model);

    void stopRecord();

    void startPlay(MainModel model);

    void stopPlay();
}
