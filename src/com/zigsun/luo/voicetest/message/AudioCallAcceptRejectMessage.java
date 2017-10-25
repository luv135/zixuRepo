package com.zigsun.luo.voicetest.message;

/**
 * Created by Luo on 2015/6/10.
 */
public class AudioCallAcceptRejectMessage {
    public boolean isAccept() {
        return isAccept;
    }

    private final boolean isAccept;

    public long getUlUserID() {
        return ulUserID;
    }

    private final long ulUserID;

    public AudioCallAcceptRejectMessage(long ulUserID, boolean isAccept) {
        this.ulUserID = ulUserID;
        this.isAccept = isAccept;
    }
}
