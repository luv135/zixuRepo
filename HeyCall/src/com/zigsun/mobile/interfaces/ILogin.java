package com.zigsun.mobile.interfaces;

/**
 * Created by Luo on 2015/6/17.
 * 登陆接口
 */
public interface ILogin extends IService {

    enum FailureCode {
       UserNameOrPasswordError, UnknownError
    }

    /**
     * 登陆回调
     */
    interface LoginListener extends IService.ServiceListener{
        void loginSuccess();

        void loginFailure(FailureCode code, String msg);
    }

    /**
     * 登陆
     */
    void login(String userName, String password,String domain);
}

