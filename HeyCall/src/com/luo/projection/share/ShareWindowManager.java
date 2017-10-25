package com.zigsun.luo.projection.share;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Luo on 2015/6/1.
 * 共享浮动窗口管理类
 * @deprecated
 * use {@linkplain ShareButtonManager}
 */
public class ShareWindowManager implements IShareWindowS {
    private static final String TAG = ShareWindowManager.class.getSimpleName();

//    public void showShareWindow(){
//        ShareWindowConnection shareWindowConnection = new ShareWindowConnection();
//        bindService(new Intent(getApplication(), ShareWindowService.class), shareWindowConnection, Service.BIND_AUTO_CREATE);
//        startService(new Intent(getApplication(), ShareWindowService.class));
//    }


    private static ShareWindowManager instance;
    private boolean isShowing;  //窗口是否显示
    private boolean canHide;    //是否可以隐藏
    private boolean isSharing;  //是否在分享
    private NotificationManager notificationManager;

    private Context context;
    private ShareWindowService shareWindowService;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            shareWindowService = ((ShareWindowService.ShareBinder) service).getShareService();
            shareWindowService.update(isSharing);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            shareWindowService = null;
        }
    };


    public static ShareWindowManager getDefaultInstance() {
        if (instance == null) throw new NullPointerException("instanceManager(Context) first!");
        return instance;
    }

    public static ShareWindowManager instanceManager(Context applicationContext) {
        if (instance == null) {
            instance = new ShareWindowManager(applicationContext);
        }
        return instance;
    }

    /**
     * @param context application context
     */
    private ShareWindowManager(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification;
    }


    @Override
    public void showShareWindow() {
        if (windowState == WindowState.NOT_CREATE || windowState == WindowState.HIDE) {
            context.bindService(new Intent(context, ShareWindowService.class), conn, Service.BIND_AUTO_CREATE);
            isShowing = true;
            windowState = WindowState.CREATED;
        }
    }

    @Override
    public void hideShareWindow() {
        if (windowState == WindowState.CREATED) {
            context.unbindService(conn);
            windowState = WindowState.NOT_CREATE;
            isShowing = false;
        }
    }

    public void setIsSharing(boolean isSharing) {
        this.isSharing = isSharing;
        if (!isSharing && canHide) {
            hideShareWindow();
        } else {
            showShareWindow();
        }
        if (shareWindowService != null) {
            shareWindowService.update(isSharing);
        }
    }

    /**
     * 关联 {@link com.zigsun.luo.projection.ProjectionActivity} 生命周期<br/>
     * 在 {@link com.zigsun.luo.projection.ProjectionActivity} 浮动窗口会一直显示.
     */
    public void onResume() {
        canHide = false;
        //当前窗口为隐藏状态
        if (windowState == WindowState.HIDE) {
            showShareWindow();
        }
    }


    public WindowState windowState = WindowState.NOT_CREATE;

    public void onDestroy() {
        if (windowState == WindowState.HIDE) windowState = WindowState.NOT_CREATE;
    }

    public enum WindowState {
        CREATED, HIDE, NOT_CREATE
    }

    public void onPause() {
        Log.d(TAG, "is share? " + isSharing);
        canHide = true;
        if (windowState == WindowState.CREATED) {
            if (!isSharing) {//若未处于分享状态则隐藏掉窗口.
                hideShareWindow();
                /**
                 *若窗口已经创建,则置为隐藏,在{@link #onResume()}后续恢复
                 */
                windowState = WindowState.HIDE;
            }
        }
    }


    public boolean isSharing() {
        return isSharing;
    }


}
