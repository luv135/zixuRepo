package com.zigsun.mobile.module;

public enum CallStatus {
    InComing, InAccept, InReject,InNoAnswer,
    DialOut,OutAccept, OutReject,OutNoAnswer,
     UNKNOWN


    ;
    public static CallStatus switchStatus(int status) {
        if (status == CallStatus.InComing.ordinal()) {
            return InComing;
        }
        if (status == CallStatus.InAccept.ordinal()) {
            return InAccept;
        }
        if (status == CallStatus.InReject.ordinal()) {
            return InReject;
        }
        if (status == CallStatus.InNoAnswer.ordinal()) {
            return InNoAnswer;
        }
        if (status == CallStatus.DialOut.ordinal()) {
            return DialOut;
        }
        if (status == CallStatus.OutAccept.ordinal()) {
            return OutAccept;
        }
        if (status == CallStatus.OutReject.ordinal()) {
            return OutReject;
        }
        if (status == CallStatus.OutNoAnswer.ordinal()) {
            return OutNoAnswer;
        }
        return CallStatus.UNKNOWN;
    }

    public static int switchStatus(CallStatus status) {
        return status.ordinal();
    }
}