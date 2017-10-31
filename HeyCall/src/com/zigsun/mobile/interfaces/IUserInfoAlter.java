package com.zigsun.mobile.interfaces;

/**
 * Created by Luo on 2015/7/7.
 */
public interface IUserInfoAlter {
    void alterEmail(String string);

    void alterTel(String string);

    void alterCompany(String string);

    void alterDepart(String string);

    enum CallBackCode{
        Success, NetError, TextError
    }
    enum Who{
        Email,Tel,Company,  Depart
    }
    interface CallBack{
        /**
         *
         * @param now current value,  only code is success, otherwise null.
         */
        void alterResult(CallBackCode code,Who who, String now);
    }
}
