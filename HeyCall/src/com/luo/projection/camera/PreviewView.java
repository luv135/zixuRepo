package com.zigsun.luo.projection.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

import com.google.zxing.aztec.encoder.HighLevelEncoder;

/**
 * Created by Luo on 2015/5/15.
 * 相机预览View
 */
public class PreviewView extends SurfaceView {
    private static final String TAG = PreviewView.class.getSimpleName();
    private int mRatioWidth = 0;
    private int mRatioHeight = 0;

    public PreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
     * calculated from the parameters. Note that the actual sizes of parameters don't matter, that
     * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
     *
     * @param width  Relative horizontal size
     * @param height Relative vertical size
     */
    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        Log.d(TAG, "setAspectRatio: width="+width+" height="+ height);
        requestLayout();
    }




    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
            Log.d(TAG, "onMeasure() width= "+width+" height= "+height);
        } else {
            //根据相机尺寸等比扩大覆盖全屏
//            float wr = (float) width / mRatioWidth;
//            float hr = (float) height / mRatioHeight;
//
//            if (wr < hr) {
//                setMeasuredDimension(width, (int) (wr * mRatioHeight));
////                Log.d(TAG, "预览窗口大小: width=" + width + "  height" + width / mRatioWidth * mRatioHeight);
//            } else {
//                setMeasuredDimension((int) (mRatioWidth * hr), height);
////                Log.d(TAG, "预览窗口大小: width="+mRatioWidth*height/mRatioHeight+"  height"+height);
//            }

            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
                Log.d(TAG, "onMeasure() width= " + width + " height= " + width * mRatioHeight / mRatioWidth);
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
                Log.d(TAG, "onMeasure() width= " + height * mRatioWidth / mRatioHeight + " height= " + height);
            }
        }
    }
}
