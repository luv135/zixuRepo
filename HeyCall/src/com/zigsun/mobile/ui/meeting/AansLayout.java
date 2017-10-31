package com.zigsun.mobile.ui.meeting;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.zigsun.mobile.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Luo on 2015/7/2.
 */
public class AansLayout extends RelativeLayout {
    private static final String TAG = AansLayout.class.getSimpleName();
    @InjectView(R.id.toMessageButton)
    ImageButton toMessageButton;
    @InjectView(R.id.toAcceptButton)
    ImageButton toAcceptButton;
    @InjectView(R.id.startClickButton)
    ImageButton startClickButton;
    @InjectView(R.id.toRejectButton)
    ImageButton toRejectButton;

    private Rect start, acc, rej, msg;

    public AansLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        prepareUI(context);
      startClickButton.setOnTouchListener(new Ot());
    }

    private class Ot implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "event"+event.getX());

                        return true;
                    case MotionEvent.ACTION_MOVE:

                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;

                }
                return true;

        }
    }

    private void prepareUI(Context context) {
        inflate(context, R.layout.abc_bubble, this);
        ButterKnife.inject(this);
    }

}
