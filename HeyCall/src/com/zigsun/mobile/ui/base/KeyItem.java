package com.zigsun.mobile.ui.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zigsun.mobile.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Luo on 2015/7/1.
 */
public class KeyItem extends LinearLayout {
    @InjectView(R.id.numberTexView)
    TextView numberTexView;
    @InjectView(R.id.letterTextVew)
    TextView letterTextVew;

    public KeyItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.KeyItem);
        numberTexView.setText(a.getString(R.styleable.KeyItem_number));
        final String string = a.getString(R.styleable.KeyItem_letter);
        if(string==null)letterTextVew.setVisibility(GONE);
        else
        letterTextVew.setText(string);
        a.recycle();
    }

    private void initLayout(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.abc_key_item, this);
        ButterKnife.inject(this);

    }
}
