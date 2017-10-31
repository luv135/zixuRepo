package com.zigsun.mobile.ui.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Luo on 2015/6/30.
 */
public class FrameLayout extends android.widget.FrameLayout {
    public FrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
//        return super.onTouchEvent(event);
    }
}
