package com.zigsun.luo.projection.share;

/**
 * Created by Luo on 2015/6/1.
 */
public class ShareEvent {
//    public enum ShareType{
//        SHARE_START,SHARE_STOP
//    }
    public final static int SHARE_START = 1;
    public final static int SHARE_STOP = 2;
    public static int type;

    public ShareEvent(int type) {
        ShareEvent.type = type;
    }

    public int getType() {
        return ShareEvent.type;
    }
}
