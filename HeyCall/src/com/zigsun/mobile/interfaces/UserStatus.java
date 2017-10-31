package com.zigsun.mobile.interfaces;

/**
 * Created by Luo on 2015/6/23.
 * 用户状态回调
 */
public interface UserStatus {
    void onLineNotify(long ulUserID);

    void offLineNotify(long ulUserID);

    void leaveRoomNotify(long ulUserID);

    void joinRoomNotify(long ulUserID);
}
