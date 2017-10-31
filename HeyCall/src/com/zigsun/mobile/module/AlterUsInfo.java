package com.zigsun.mobile.module;

/**
 * Created by Luo on 2015/7/10.
 */
public class AlterUsInfo {
    public enum Code {
        MODIFY_USERNAME_ALREADY_EXIST, MODIFY_SYSTEM_ERROR, MODIFY_SYSTEM_SUCCESS,UNKNOWN
    }

    public Code getC() {
        return c;
    }

    private Code c =Code.UNKNOWN;


    public AlterUsInfo(int ret) {
        switch (ret) {
            case 10:
                c = Code.MODIFY_USERNAME_ALREADY_EXIST;
                break;
            case 11:
                c = Code.MODIFY_SYSTEM_ERROR;
                break;
            case 12:
                c = Code.MODIFY_SYSTEM_SUCCESS;
                break;
        }
    }
}
