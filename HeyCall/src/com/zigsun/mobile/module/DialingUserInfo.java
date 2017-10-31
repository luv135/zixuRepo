package com.zigsun.mobile.module;

import com.zigsun.bean.UserInfo;

/**
 * Created by Luo on 2015/7/15.
 */
public class DialingUserInfo extends UserInfo {


    public DialingUserInfo() {
        status = UserInfoStatus.UNKNOWN;
    }

    public UserInfoStatus getStatus() {
        return status;
    }

    /**
     * @deprecated use {@linkplain #setStatus(UserInfoStatus)}
     */
    @Deprecated
    @Override
    public void setUcStatus(byte ucStatus) {
        super.setUcStatus(ucStatus);
        UserInfoStatus s;
        switch (ucStatus){
            case 0:
                s= UserInfoStatus.OffLine;
                break;
            case 1:
                s= UserInfoStatus.Online;
                break;
            case 2:
                s= UserInfoStatus.InMeeting;
                break;
            case 3:
                s= UserInfoStatus.LeaveMeeting;
                break;
            default:
                return;
        }
        setStatus(s);
    }

    /**
     *
     * @deprecated use {@linkplain #getStatus()}
     */
    @Deprecated
    @Override
    public byte getUcStatus() {
        return super.getUcStatus();
    }

    public void setStatus(UserInfoStatus status) {
        this.status = status;
    }

    private UserInfoStatus status;
}
