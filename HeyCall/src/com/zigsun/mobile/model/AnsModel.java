package com.zigsun.mobile.model;

import android.content.ClipData;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import com.zigsun.luo.projection.utils.Util;
import com.zigsun.util.CONSTANTS;

/**
 * Created by Luo on 2015/7/2.
 */
public class AnsModel {
    private View start, ans, reject, text;
    private Handler handler;
    private Context context;

    public AnsModel(Context context, View ans, View reject, View start, View text) {
        this.context = context;
        this.ans = ans;
        this.reject = reject;
        this.start = start;
        this.text = text;
        start.setOnTouchListener(new MyTouchListener());
//        ans.setOnDragListener(new MyDragListener(CONSTANTS.ACCEPT));
//        reject.setOnDragListener(new MyDragListener(CONSTANTS.REJCT));
//        text.setOnDragListener(new MyDragListener(-1));
    }

    private final class MyTouchListener implements View.OnTouchListener {  public static final float MAX_TAP_DISTANCE = 5;
        public boolean isMoved;
        public float oldOffsetY;
        public float oldOffsetX;
        public float firstRawY;
        public float firstRawX;
        public float height = Util.getScreenResolution(context).y / 2;
        public float width = Util.getScreenResolution(context).x;
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isMoved = false;
                    firstRawX = event.getRawX();
                    firstRawY = event.getRawY();
                    oldOffsetX = v.getX(); // 偏移量
                    oldOffsetY = v.getY(); // 偏移量
                    return false;
                case MotionEvent.ACTION_MOVE:
                    if (!isMoved) {
                        float nowX = v.getX(); // 偏移量
                        float nowY = v.getY(); // 偏移量
                        if (Math.abs(oldOffsetX - nowX) > MAX_TAP_DISTANCE || Math.abs(oldOffsetY - nowY) > MAX_TAP_DISTANCE) {
                            isMoved = true;
                            MotionEvent cancelEvent = MotionEvent.obtain(event);
                            cancelEvent.setAction(MotionEvent.ACTION_CANCEL | (event.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                            v.onTouchEvent(cancelEvent);
                        }
                    }
                    float x = oldOffsetX + (event.getRawX() - firstRawX);// - mFloatView.getMeasuredWidth() / 2;

                    float y = oldOffsetY + (event.getRawY() - firstRawY);// - mFloatView.getMeasuredHeight() / 2 - 25;
                    if (x > 0 && x < width - v.getWidth())
                        v.setX(x);
                    if (y > 0 && y < height - v.getHeight())
                        v.setY(y);
                    return true;
                case MotionEvent.ACTION_UP:
//                        v.animate().translationY(0).translationX(0).start();
                    return isMoved;

            }
            return false;
        }
    }

    class MyDragListener implements View.OnDragListener {
        private static final String TAG = "MyDragListener";
        private final int what;

        public MyDragListener(int what) {
            this.what = what;
        }


        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    View view2 = (View) event.getLocalState();
                    view2.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "ACTION_DRAG_STARTED");
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setScaleX(1.2f);
                    v.setScaleY(1.2f);
                    Log.d(TAG, "ACTION_DRAG_ENTERED");

                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setScaleX(1f);
                    v.setScaleY(1f);
                    Log.d(TAG, "ACTION_DRAG_EXITED");
                    break;
                case DragEvent.ACTION_DROP:
                    Log.d(TAG, "ACTION_DROP");
//                    handler.sendEmptyMessage(what);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setScaleX(1f);
                    v.setScaleY(1f);
                    View view1 = (View) event.getLocalState();
                    view1.setVisibility(View.VISIBLE);
                    Log.d(TAG, "ACTION_DRAG_ENDED");
                default:
                    break;
            }
            return true;
        }
    }
}
