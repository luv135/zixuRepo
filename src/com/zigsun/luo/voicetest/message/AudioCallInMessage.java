package com.zigsun.luo.voicetest.message;

/**
 * Created by Luo on 2015/6/10.
 */
public class AudioCallInMessage {
    public long getUlUserID() {
        return ulUserID;
    }

    private final long ulUserID;

    public AudioCallInMessage(long ulUserID) {
        this.ulUserID = ulUserID;
    }
}
