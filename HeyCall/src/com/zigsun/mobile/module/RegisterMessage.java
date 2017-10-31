package com.zigsun.mobile.module;

/**
 * Created by Luo on 2015/6/17.
 */
public class RegisterMessage {
    public RegisterCode getCode() {
        return code;
    }

    private RegisterCode code;

    public enum RegisterCode {
        REGISTER_SYSTEM_SUCCESS, REGISTER_SYSTEM_ERROR, REGISTER_SYSTEM_ALREADY_EXIST
    }

    /**
     * Ret 定义在CommandType.h
     * REGISTER_SYSTEM_SUCCESS：7
     * REGISTER_SYSTEM_ERROR:8
     * REGISTER_SYSTEM_ALREADY_EXIST:9
     */
    public RegisterMessage(int ret) {
        switch (ret) {
            case 7:
                code = RegisterCode.REGISTER_SYSTEM_SUCCESS;
                break;
            case 8:
                code = RegisterCode.REGISTER_SYSTEM_ERROR;
                break;
            case 9:
                code = RegisterCode.REGISTER_SYSTEM_ALREADY_EXIST;
                break;
        }
    }

}
