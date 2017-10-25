package com.zigsun.luo.projection.share;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.List;

/**
 * Created by Luo on 2015/5/7.
 * 浮动小窗体
 * @deprecated
 */
public class FloatWindow {


    private static final String TAG = FloatWindow.class.getSimpleName();
    private final WindowManager manager;
    private final Context context;
    private WindowManager.LayoutParams layoutParams;
    private View root;
    private int oldOffsetX;
    private float firstRawX;
    private float firstRawY;
    private int oldOffsetY;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    firstRawX = event.getRawX();
                    firstRawY = event.getRawY();
                    oldOffsetX = layoutParams.x; // 偏移量
                    oldOffsetY = layoutParams.y; // 偏移量
                    Log.d(TAG, "ACTION_DOWN");
                    return true;
                case MotionEvent.ACTION_MOVE:
                    //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                    layoutParams.x = oldOffsetX + (int) (event.getRawX() - firstRawX);// - mFloatView.getMeasuredWidth() / 2;
                    //减25为状态栏的高度
                    layoutParams.y = oldOffsetY + (int) (event.getRawY() - firstRawY);// - mFloatView.getMeasuredHeight() / 2 - 25;
                    //刷新
                    manager.updateViewLayout(root, layoutParams);
                    Log.d(TAG, "ACTION_MOVE");
                    return true;
                case MotionEvent.ACTION_UP:
                    int newOffsetX = layoutParams.x;
                    int newOffsetY = layoutParams.y;

                    if (oldOffsetX == newOffsetX && oldOffsetY == newOffsetY) {
//                        Intent intent = new Intent(context, MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // You need this if starting
//                        //  the activity from a service
//                        intent.setAction(Intent.ACTION_MAIN);
//                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        if(onClickListener!=null)
                            onClickListener.onClick(root);
                        Log.d(TAG, "float window clicked!");
//
//                        try {
//                            pendingIntent.send();
//                        } catch (PendingIntent.CanceledException e) {
//                            e.printStackTrace();
//                        }

//                        getRunningActivityName();


//                        context.getApplicationContext().startActivity(intent);
//                        context.startActivity(intent);


//                        Intent intent = new Intent();
//                        intent.setAction("MainActivity");
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);

//                        Toast.makeText(context, "float window clicked!", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;
            }
            return false;
        }
    };
    private View.OnClickListener onClickListener;

    private void getRunningActivityName() {


        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);


        List<ActivityManager.RecentTaskInfo> recentTasks = activityManager.getRecentTasks(2, ActivityManager.RECENT_WITH_EXCLUDED);

        for (ActivityManager.RecentTaskInfo info : recentTasks) {
            Log.d(TAG, "recent id: " + info.id);
        }
//
//        ActivityManager.RecentTaskInfo recentTaskInfo = recentTasks.get(0);
//
//
//        Log.d(TAG, "recent id: " + recentTasks.get(1).id);
//        activityManager.moveTaskToFront(recentTasks.get(1).id, ActivityManager.MOVE_TASK_NO_USER_ACTION);
        Log.d(TAG, "activityManager.moveTaskToFront(recentTasks.get(1).id,ActivityManager.MOVE_TASK_WITH_HOME); ");
//        String runningActivity=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
//        return runningActivity;
    }


    private PendingIntent pendingIntent;

    public FloatWindow(Context context) {
        this.context = context;
        this.manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

    }

    public FloatWindow setContentView(View view) {
        layoutParams = new WindowManager.LayoutParams();
        //所有窗体之上,包括状态栏
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
//        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //设置图片格式，效果为背景透明
        layoutParams.format = -3;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        //调整悬浮窗显示的停靠位置为左侧置顶
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.alpha = 1f;
        //设置悬浮窗口长宽数据
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        return setContentView(view, layoutParams);

    }




    public FloatWindow setContentView(View view, WindowManager.LayoutParams layoutParams) {
        this.root = view;
        this.root.setOnTouchListener(onTouchListener);
        this.layoutParams = layoutParams;

        return this;
    }

    public FloatWindow setPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
        return this;
    }


    public void attachWindow() {
        Log.d(TAG, "attach window");
        manager.addView(root, layoutParams);
    }

    public void detachWindow() {
        manager.removeView(root);
    }

    public void setonClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    /**
     * 窗体位置
     */
    public FloatWindow setXY(int x, int y) {
        if(layoutParams==null){
            throw new NullPointerException("setContentView(View) first please!");
        }
        layoutParams.x = x;
        layoutParams.y = y;
        return this;
    }

//    public static class Builder {
//        private final WindowManager manager;
//
//        public Builder(WindowManager manager) {
//            this.manager = manager;
//        }
//
//        public Builder setContentView() {
//            return this;
//        }
//    }

}
