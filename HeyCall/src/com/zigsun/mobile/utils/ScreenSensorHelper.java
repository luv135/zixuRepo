package com.zigsun.mobile.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by Luo on 2015/7/17.
 */
public class ScreenSensorHelper implements SensorEventListener {

    private static final String TAG = ScreenSensorHelper.class.getSimpleName();
    private Context context;
    private SensorManager _sensorManager;
    private Sensor mProximiny;
    private PowerManager localPowerManager;
    private PowerManager.WakeLock localWakeLock;

    public ScreenSensorHelper(Context context) {
        this.context = context;
        init();
    }

    private void init() {

        _sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mProximiny = _sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        localPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        localWakeLock = this.localPowerManager.newWakeLock(32, "MyPower");//第一个参数为电源锁级别，第二个是日志tag
    }

    public void registerListener(){
        _sensorManager.registerListener(this, mProximiny, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterListener(){
        _sensorManager.unregisterListener(this);
        localWakeLock.release();//释放电源锁，如果不释放finish这个acitivity后仍然会有自动锁屏的效果，不信可以试一试
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[0] == 0.0) {// 贴近手机
            System.out.println("hands up");
            Log.d(TAG, "hands up in calling activity");
            if (localWakeLock.isHeld()) {
                return;
            } else {
                localWakeLock.acquire();// 申请设备电源锁
            }
        } else {// 远离手机
            System.out.println("hands moved");
            Log.d(TAG, "hands moved in calling activity");
            if (localWakeLock.isHeld()) {
                return;
            } else {
                localWakeLock.setReferenceCounted(false);
                localWakeLock.release(); // 释放设备电源锁
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
