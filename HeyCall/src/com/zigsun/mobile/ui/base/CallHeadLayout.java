package com.zigsun.mobile.ui.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zigsun.mobile.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Luo on 2015/6/30.
 */
public class CallHeadLayout extends RelativeLayout {
    @InjectView(R.id.callNumberTextt)
    TextView callNumberTextt;
    @InjectView(R.id.deleteButton)
    ImageButton deleteButton;

    public CallHeadLayout(Context context) {
        super(context);
        prepareUI(context);
    }

    private void prepareUI(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.abc_call_header_layout, this);
        ButterKnife.inject(this);

    }

    public void setDeleteOnClickListener(OnClickListener l) {
        deleteButton.setOnClickListener(l);
    }

    public void setDeleteOnLongClickListener(OnLongClickListener l) {
        deleteButton.setOnLongClickListener(l);
    }


    public void setText(String number) {
        callNumberTextt.setText(number);
    }
}
