package com.zigsun.luo.projection.share;

/**
 * Created by Luo on 2015/6/10.
 */
@Deprecated
public class ShareScreenMessage {
    public ShareScreenMessage(Action action) {
        this.action = action;
    }

    public Action action;
    public enum Action{START_SHARE, END_SHARE, UNKNOWN}
}
