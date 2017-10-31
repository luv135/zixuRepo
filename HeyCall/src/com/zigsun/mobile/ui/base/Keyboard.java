package com.zigsun.mobile.ui.base;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.zigsun.mobile.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Luo on 2015/6/16.
 */
public class Keyboard extends LinearLayout {


    /**
     * # 按键
     */
    public static final int CODE_X = 010001;
    /**
     * * 按键
     */
    public static final int CODE_XX = 010002;
    public static final int CODE_ADD = 010003;
    public static final int CODE_CALL = 010004;
    public static final int CODE_DELETE = 010005;
    public static final int CODE_CALL_VIDEO = 010006;
    @InjectView(R.id.number01)
    ImageButton number01;
    @InjectView(R.id.number02)
    ImageButton number02;
    @InjectView(R.id.number03)
    ImageButton number03;
    @InjectView(R.id.number04)
    ImageButton number04;
    @InjectView(R.id.number05)
    ImageButton number05;
    @InjectView(R.id.number06)
    ImageButton number06;
    @InjectView(R.id.number07)
    ImageButton number07;
    @InjectView(R.id.number08)
    ImageButton number08;
    @InjectView(R.id.number09)
    ImageButton number09;
    @InjectView(R.id.numberX)
    ImageButton numberX;
    @InjectView(R.id.number00)
    ImageButton number00;
    @InjectView(R.id.numberXX)
    ImageButton numberXX;
    @InjectView(R.id.add)
    ImageButton add;
    @InjectView(R.id.call)
    ImageButton call;
    @InjectView(R.id.delete)
    ImageButton delete;
    @InjectView(R.id.callVideo)
    ImageButton callVideo;

    private void initLayout(Context context) {

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.abc_call_keyboard_layout, this);
        ButterKnife.inject(this);
    }
    private class KeyboardEvent implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.number01:
                    keyDown(1, "1");
                    break;
                case R.id.number02:
                    keyDown(2, "2");

                    break;
                case R.id.number03:
                    keyDown(3, "3");

                    break;
                case R.id.number04:
                    keyDown(4, "4");

                    break;
                case R.id.number05:
                    keyDown(5, "5");

                    break;
                case R.id.number06:

                    keyDown(6, "6");

                    break;
                case R.id.number07:
                    keyDown(7, "7");

                    break;
                case R.id.number08:
                    keyDown(8, "8");

                    break;
                case R.id.number09:
                    keyDown(9, "9");

                    break;
                case R.id.numberX:
                    keyDown(CODE_X, "#");

                    break;
                case R.id.number00:
                    keyDown(0, "0");

                    break;
                case R.id.numberXX:
                    keyDown(CODE_XX, "*");

                    break;
                case R.id.add:
                    keyDown(CODE_ADD, "");

                    break;
                case R.id.call:
                    keyDown(CODE_CALL, "");

                    break;
                case R.id.delete:
                    keyDown(CODE_DELETE, "");

                    break;
                case R.id.callVideo:
                    keyDown(CODE_CALL_VIDEO, "");

                    break;
            }
        }
    }



    private KeyboardEvent event;

    public Keyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setBackgroundColor(Color.WHITE);
        initLayout(context);
        event = new KeyboardEvent();
        setClickListener();
    }

    public void setListener(KeyboardListener listener) {
        this.listener = listener;
    }

    private KeyboardListener listener;
    public interface KeyboardListener {
        void keyDown(int code, String value);

    }

    private void keyDown(int code, String value) {
        if (listener != null) listener.keyDown(code, value);
    }

    private void setClickListener() {


        number01.setOnClickListener(event);

        number02.setOnClickListener(event);

        number03.setOnClickListener(event);

        number04.setOnClickListener(event);

        number05.setOnClickListener(event);

        number06.setOnClickListener(event);

        number07.setOnClickListener(event);

        number08.setOnClickListener(event);

        number09.setOnClickListener(event);

        numberX.setOnClickListener(event);

        number00.setOnClickListener(event);

        numberXX.setOnClickListener(event);

        add.setOnClickListener(event);

        call.setOnClickListener(event);

        delete.setOnClickListener(event);

        callVideo.setOnClickListener(event);

    }
}
