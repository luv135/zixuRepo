package com.zigsun.luo.projection.share;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.FormatException;
import com.zigsun.mobile.R;
import com.zigsun.luo.projection.ProjectionActivity;
import com.zigsun.luo.projection.RequestRootMessage;
import com.zigsun.luo.projection.interfaces.IShareInterface;
import com.zigsun.luo.projection.utils.Util;

import java.util.UnknownFormatConversionException;
import java.util.concurrent.CopyOnWriteArraySet;

import de.greenrobot.event.EventBus;

/**
 * Created by Luo on 2015/6/6.
 */
public class ShareButtonManager implements View.OnTouchListener, View.OnClickListener {
    private static final String TAG = ShareButtonManager.class.getSimpleName();

    private static final float MAX_TAP_DISTANCE = 5;    //点击响应最大移动范围
    //    private final int height;
//    private final int width;
    private final Button shareButton;
    private float firstRawX;
    private float firstRawY;
    private float oldOffsetX;
    private float oldOffsetY;
    private boolean isMoved;
    private Context context;

    //    private ShareScreenMessage.Action action = ShareScreenMessage.Action.UNKNOWN;
    private boolean isSharing; //是否在分享屏幕

//    public ShareButtonManager(Context context) {
//        this.context = context;
//        final Point screenResolution = Util.getScreenResolution(context);
//        height = screenResolution.y;
//        width = screenResolution.x;
//    }

    private IShareInterface shareInterface;

    public ShareButtonManager(IShareInterface iShareInterface, Button shareButton) {
        try {
            shareInterface =  iShareInterface;
        } catch (Exception e) {
            throw new RuntimeException("must implement IShareInterface");
        }
        this.shareButton = shareButton;
        shareButton.setOnTouchListener(this);
        shareButton.setOnClickListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isMoved = false;
                firstRawX = event.getRawX();
                firstRawY = event.getRawY();
                oldOffsetX = v.getTranslationX(); // 偏移量
                oldOffsetY = v.getTranslationY(); // 偏移量
                Log.d(TAG, "ACTION_DOWN");
                return false;
            case MotionEvent.ACTION_MOVE:
                if (!isMoved) {
                    float nowX = v.getTranslationX(); // 偏移量
                    float nowY = v.getTranslationY(); // 偏移量

                    if (Math.abs(oldOffsetX - nowX) > MAX_TAP_DISTANCE || Math.abs(oldOffsetY - nowY) > MAX_TAP_DISTANCE) {
                        isMoved = true;
                        MotionEvent cancelEvent = MotionEvent.obtain(event);
                        cancelEvent.setAction(MotionEvent.ACTION_CANCEL | (event.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                        v.onTouchEvent(cancelEvent);
                    }
                }

                //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                float x = oldOffsetX + (event.getRawX() - firstRawX);// - mFloatView.getMeasuredWidth() / 2;

                float y = oldOffsetY + (event.getRawY() - firstRawY);// - mFloatView.getMeasuredHeight() / 2 - 25;
//                if (x > 0 && x < width - v.getWidth())
                v.setTranslationX(x);
//                if (y > 0 && y < height - v.getHeight())
                v.setTranslationY(y);
                Log.d(TAG, "ACTION_MOVE x= " + x + " y= " + y);
                return true;
            case MotionEvent.ACTION_UP:
                v.animate().translationY(0).translationX(0).start();
                return isMoved;
//                int newOffsetX = layoutParams.x;
//                int newOffsetY = layoutParams.y;

//                if (oldOffsetX == newOffsetX && oldOffsetY == newOffsetY) {
//                        Intent intent = new Intent(context, MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // You need this if starting
//                        //  the activity from a service
//                        intent.setAction(Intent.ACTION_MAIN);
//                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                    if(onClickListener!=null)
//                        onClickListener.onClick(root);
//                Log.d(TAG, "float window clicked!");
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
//                return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {

        if (!isSharing && !Util.isRootSystem()) {
            EventBus.getDefault().post(new RequestRootMessage());
            return;
        }
        if(isSharing){
            shareInterface.closeScreenShare();
        } else{
            shareInterface.openScreenShare();
        }
//        EventBus.getDefault().post(new ShareScreenMessage(isSharing ? ShareScreenMessage.Action.END_SHARE : ShareScreenMessage.Action.START_SHARE));
    }

    public void updateUI(boolean isSharing) {
        this.isSharing = isSharing;
        shareButton.setText(isSharing ? R.string.sharing : R.string.share);
    }

}
