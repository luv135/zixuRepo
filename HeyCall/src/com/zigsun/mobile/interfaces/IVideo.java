package com.zigsun.mobile.interfaces;

/**
 * Created by Luo on 2015/6/23.
 */
public interface IVideo {

    void startVideoRecord();

    void stopVideoRecord();

    enum Action {
        OPEN_SUCCESS, OPEN_FAILURE, CLOSE_SUCCESS,CLOSE_FAILURE
    }
    interface VideoListener {
        void videoAction(Action action);
    }

}
