package com.zigsun.mobile.ui.base;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Luo on 2015/6/26.
 */
public class InterViewPager extends ViewPager {
    public InterViewPager(Context context) {
        super(context);
    }

    public InterViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    public void setInterceptTouch(boolean interceptTouch) {
        this.interceptTouch = interceptTouch;
    }

    private boolean interceptTouch;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (interceptTouch) return false;
        return super.onInterceptTouchEvent(ev);
    }

}
