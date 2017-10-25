package com.zigsun.luo.projection.battery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class BatteryChangeReceiver extends BroadcastReceiver {

    private static final String TAG = BatteryChangeReceiver.class.getSimpleName();
    private Context context;

    private enum Power {CONNECTED, DISCONNECTED, DEFAULT}

    //标志位,防止相同消息重复发生.
    private Power power = Power.DEFAULT;

    public static class Helper {
        //        private static Context context;   //此处持有,内存泄露??
        private static BatteryChangeReceiver receiver = new BatteryChangeReceiver();

        //        private static IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        public static void registerReceiver(Activity activity) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
            filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            filter.addAction(Intent.ACTION_BATTERY_CHANGED);
            if (activity instanceof IBatteryListener) {
                //直接赋值
                receiver.listener = (IBatteryListener) activity;
//                receiver.setListener((IBatteryListener) activity);
            } else {
                throw new ClassCastException("must implement IBatteryListener interface ");
            }
            activity.registerReceiver(receiver, filter);
        }

        public static void unregisterReceiver(Activity activity) {
            activity.unregisterReceiver(receiver);
            receiver.power = Power.DEFAULT;
        }

    }

    public void setListener(IBatteryListener listener) {
        this.listener = listener;
    }

    private IBatteryListener listener;




    public BatteryChangeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "batter state: " + intent.getAction());
        String action = intent.getAction();
        switch (action) {
            case Intent.ACTION_BATTERY_CHANGED:
                int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                Log.d(TAG, "plugged: " + plugged);
                if (plugged == 0) {
                    powerDisconnected();
                } else {
                    powerConnected();
                }

                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                powerDisconnected();
                break;
            case Intent.ACTION_POWER_CONNECTED:
                powerConnected();
        }
    }

    private void powerConnected() {
        if (power == Power.CONNECTED) return;
        power = Power.CONNECTED;
        listener.powerConnected();
    }

    private void powerDisconnected() {
        if (power == Power.DISCONNECTED) return;
        power = Power.DISCONNECTED;
        listener.powerDisconnected();
    }
}
