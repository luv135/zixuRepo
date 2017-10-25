package com.zigsun.luo.projection.share;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.zigsun.mobile.R;
import com.zigsun.luo.projection.RequestRootMessage;
import com.zigsun.luo.projection.utils.Util;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class ShareWindowService extends Service {
    private static final String TAG = ShareWindowService.class.getSimpleName();
    private WindowManager manager;

    private FloatWindow floatWindow;
    private TextView textView;

    private List<View.OnClickListener> onClickListenerList = new ArrayList<>();


    public void addOnClickListener(View.OnClickListener onClickListener) {
        onClickListenerList.add(onClickListener);
    }

    public void removeOnClickListener(View.OnClickListener onClickListener) {
        onClickListenerList.remove(onClickListener);
    }


    public ShareWindowService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
//        return null;
        return binder;
    }

    private ShareBinder binder = new ShareBinder();

    /**
     * 更新浮动窗口状态;
     * 设置文本: 分享/分享中..
     *
     * @param isSharing
     */
    public void update(boolean isSharing) {
        textView.setText(isSharing ? R.string.sharing : R.string.share);
    }

    public class ShareBinder extends Binder {
        public ShareWindowService getShareService() {
            return ShareWindowService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "flaot window create and attach");


//        this.manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//        //所有窗体之上,包括状态栏
//        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
////        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
//        //设置图片格式，效果为背景透明
//        layoutParams.format = -3;
//        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
//        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_FULLSCREEN
//                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
//        //调整悬浮窗显示的停靠位置为左侧置顶
//        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
//        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
//        layoutParams.x = 0;
//        layoutParams.y = 0;
//        layoutParams.alpha = 1f;
//        //设置悬浮窗口长宽数据
//        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        View root  = LayoutInflater.from(this).inflate(R.layout.share_layout, null);
//        manager.addView(root, layoutParams);
//
        floatWindow = new FloatWindow(this);
        View view = LayoutInflater.from(this).inflate(R.layout.share_layout, null);
        textView = (TextView) view.findViewById(R.id.tv_float_share);
        int x = Util.getScreenResolution(this).x;
        floatWindow.setContentView(view).setXY(x, 0);//.attachWindow();
        floatWindow.setonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                handler.sendEmptyMessage(0);
//                for (View.OnClickListener listener : onClickListenerList) {
//                    listener.onClick(v);
//                }

                boolean sharing = ShareWindowManager.getDefaultInstance().isSharing();
                if (!sharing && !Util.isRootSystem()) {
                    EventBus.getDefault().post(new RequestRootMessage());
                    return;
                }
                ShareWindowManager.getDefaultInstance().setIsSharing(!sharing);


                //test
//                if (sharing) {
//                    Intent intent = new Intent(getApplication(), ProjectionActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                }
//                EventBus.getDefault().post(new ShareEvent(ShareEvent.SHARE_START));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        floatWindow.detachWindow();
    }

    //    private String getRunningActivityName(){
//        ActivityManager activityManager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RecentTaskInfo> recentTasks = activityManager.getRecentTasks(10, ActivityManager.RECENT_WITH_EXCLUDED);
//        ActivityManager.RecentTaskInfo recentTaskInfo = recentTasks.get(0);
//
//
//
//
//        activityManager.moveTaskToFront(recentTaskInfo.id,0);
//
//        String runningActivity=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
//        return runningActivity;
//    }
}
