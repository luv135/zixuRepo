package com.zigsun.mobile.model;

import com.zigsun.mobile.module.CallStatus;

/**
 * Created by Luo on 2015/6/26.
 */
public class MeetingStatusRecorder {
    private CallStatus status;
    private long startTime, endTime;

    public MeetingStatusRecorder(long startTime, CallStatus status) {
        this.startTime = startTime;
        this.status = status;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public CallStatus getStatus() {
        return status;
    }

    public void setStatus(CallStatus status) {
        this.status = status;
    }
}
