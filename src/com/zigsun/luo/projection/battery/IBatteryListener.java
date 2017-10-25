package com.zigsun.luo.projection.battery;

import android.content.Intent;

/**
 * Created by Luo on 2015/6/2.
 */
public interface IBatteryListener {
    /**
     * 电源未连接
     */
    void powerDisconnected();

    /**
     * 电源连接
     */
    void powerConnected();
}
