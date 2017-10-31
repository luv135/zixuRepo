package com.zigsun.mobile.ui.meeting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.zigsun.mobile.ui.base.AnimationListener;

/**
 * Created by Luo on 2015/7/21.
 */
public class StatusText extends TextView {
    private static final long DELAYHIDE = 2000;
    private AlphaAnimation animation ;

    public StatusText(Context context, AttributeSet attrs) {
        super(context, attrs);
        animation  = new AlphaAnimation(1, 0);
        animation.setDuration(DELAYHIDE);
    }

    public void setStatus(int resid) {
        setVisibility(VISIBLE);
        setText(resid);
        postDelayed(hideRunnable, DELAYHIDE);
    }

    public final void setStatus(CharSequence text) {
        setVisibility(VISIBLE);
        setText(text);
        postDelayed(hideRunnable, DELAYHIDE);
    }


    public Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            if (getVisibility() == INVISIBLE || animation.hasStarted()) return;
            animation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    setVisibility(INVISIBLE);
                }
            });
            startAnimation(animation);
        }
    };
}
