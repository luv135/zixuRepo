package com.zigsun.mobile.ui.base.bubbles;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class BubbleLayout extends ImageView {
    private static final String TAG = BubbleLayout.class.getSimpleName();
    private float initialTouchX;
    private float initialTouchY;
    private int initialX;
    private int initialY;
    private OnBubbleRemoveListener onBubbleRemoveListener;
    private ObjectAnimator animator;

    public void setOnBubbleRemoveListener(OnBubbleRemoveListener listener) {
        onBubbleRemoveListener = listener;
    }

    void setLayoutCoordinator(BubblesLayoutCoordinator layoutCoordinator) {
        this.layoutCoordinator = layoutCoordinator;
    }

    BubblesLayoutCoordinator getLayoutCoordinator() {
        return layoutCoordinator;
    }

    public BubbleLayout(Context context) {
        super(context);
        initializeView();
    }

    public BubbleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView();
    }

    public BubbleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeView();
    }

    void notifyBubbleRemoved() {
        if (onBubbleRemoveListener != null) {
            onBubbleRemoveListener.onBubbleRemoved(this);
        }
    }

    private void initializeView() {
        setClickable(true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        playAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        animator.cancel();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = getLeft();
                    initialX = (int) getTranslationX();
                    initialY = getTop();
                    initialY = (int) getTranslationY();

                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    if (layoutCoordinator != null) {
                        layoutCoordinator.notifyBubblePositionChanged(this, 0, 0);
                    }
                    animator.cancel();
//                    setVisibility(INVISIBLE);
                    break;
                case MotionEvent.ACTION_MOVE:
                    int x = initialX + (int) (event.getRawX() - initialTouchX);
                    int y = initialY + (int) (event.getRawY() - initialTouchY);
                    setTranslationX(x);
                    setTranslationY(y);
//                    Log.d(TAG, "left:"+getLeft()+"   transx: "+getTranslationX()+" x "+getX());
//                    setLeft(x);
//                    setTop(y);
//                    getViewParams().x = x;
//                    getViewParams().y = y;
//                    getWindowManager().updateViewLayout(this, getViewParams());

                    if (layoutCoordinator != null) {
                        layoutCoordinator.notifyBubblePositionChanged(this, x, y);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (layoutCoordinator != null) {
                        if (layoutCoordinator.notifyBubbleRelease(this)) return true;
                    }
                    setTranslationX(initialX);
                    setTranslationY(initialY);
                    animator.start();

//                    setLeft(initialX);
//                    setTop(initialY);
//                    setVisibility(VISIBLE);
                    break;
            }
        }
        return true;//super.onTouchEvent(event);
    }

    private BubblesLayoutCoordinator layoutCoordinator;

    private void playAnimation() {
        if (!isInEditMode()) {

            animator = Tada.tada(this, 5);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    Log.d(TAG, "onAnimationRepeat");
                }
            });
            animator.start();


//            AnimatorSet animator = (AnimatorSet) AnimatorInflater
//                    .loadAnimator(getContext(), R.animator.bubble_shown_animator);
//            animator.setTarget(this);
//            animator.start();
        }
    }

    public interface OnBubbleRemoveListener {
        void onBubbleRemoved(BubbleLayout bubble);
    }
}
