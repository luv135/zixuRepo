package com.zigsun.mobile.interfaces;

import javax.crypto.spec.IvParameterSpec;

/**
 * Created by Luo on 2015/6/19.
 */
public interface IDialMeeting extends IAudio, IVideo, IMeetingManager {
    /**
     * 拨号
     *
     * @param number 对方号码
     */
    void dial(String number);

    /**
     * 接受对方呼叫
     *
     * @param ulMeetingID
     * @param ulUserID
     * @param meetingType
     */
    void accept(long ulMeetingID, long ulUserID, long meetingType);


    /**
     * 拒绝对方呼叫
     *
     * @param ulMeetingID
     * @param ulUserID
     * @param strNickName
     */
    void reject(long ulMeetingID, long ulUserID, String strNickName);

    /**
     * 挂断
     */
    void hangUp();

    /**
     * 状态回调接口
     */
    interface DialingStatus extends UserStatus ,IVideo.VideoListener{
        /**
         * 连接中
         *
         * @param number 谁连接中
         */
        void DialingWithNumber(String number);

        /**
         * 接受连接
         *
         * @param number 谁连接上
         */
        void acceptedWithNumber(String number);

        /**
         * 拒绝连接
         *
         * @param number 谁拒绝连接
         */
        void rejectedWithNumber(String number);

        /**
         * 连接已断开
         * 挂断/信号中断
         *
         * @param number 谁已断开
         */
        void hangUpWithNumber(String number);


    }
}
