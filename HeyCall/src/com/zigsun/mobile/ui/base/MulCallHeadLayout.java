package com.zigsun.mobile.ui.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.zigsun.mobile.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Luo on 2015/6/25.
 */
public class MulCallHeadLayout extends RelativeLayout implements View.OnClickListener {
    @InjectView(R.id.callContainerTy)
    LinearLayout callContainerTy;
    @InjectView(R.id.closeButton)
    ImageButton closeButton;
    @InjectView(R.id.hsViewt)
    HorizontalScrollView hsViewt;

    public MulCallHeadLayout(Context context, ChangeListener listener) {
        super(context);
        this.listener = listener;
        prepareLayout(context);
    }

    private void prepareLayout(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.abc_mul_call_head_item_layout, this);
        ButterKnife.inject(this);
        closeButton.setOnClickListener(this);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
//        return super.onTouchEvent(event);
    }

    public void addItemView(View view) {
        callContainerTy.addView(view);

    }

    public void removeItem(View view) {
        callContainerTy.removeView(view);
    }

    private ChangeListener listener;

    @Override
    public void onClick(View v) {
        listener.itemRemoeAll();
    }

    public interface ChangeListener {
        void itemRemoeAll();
    }

}
