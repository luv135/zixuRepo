package com.zigsun.mobile.ui.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by Luo on 2015/6/26.
 */
public class InterRelativeLayout extends RelativeLayout {
    private static final String TAG = InterRelativeLayout.class.getSimpleName();

    public InterRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setInterception(boolean interception) {
        this.interception = interception;
    }


    private boolean interception;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (interception) return true;
        return super.onTouchEvent(event);
    }
}
