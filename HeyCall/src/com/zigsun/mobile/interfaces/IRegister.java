package com.zigsun.mobile.interfaces;

/**
 * Created by Luo on 2015/6/17.
 */
public interface IRegister extends IService {
    boolean userNameValid();

    boolean passwordValid();

    void register(String userName, String password);

    /**
     * 请求服务器发送手机验证码
     * @param number
     */
    void sendTelephoneIdentifyingCode(String number);
    void confirmTelephoneIdentifyingCode(String idCode);

    enum RegisterCode {
        UserNameExist, UnknownError,CONFIRM_OK,CONFIRM_NOT,REGISTER_FINISH
    }

    interface RegisterListener extends IService.ServiceListener{
//        void registerSuccess();

        void registerSuccess(RegisterCode code);

        void registerFailure(RegisterCode code);

    }
}
