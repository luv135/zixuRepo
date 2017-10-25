package com.zigsun.luo.projection.base;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zigsun.mobile.R;


/**
 * Created by Luo on 2015/6/4.
 * custom dialog
 */
public class Dialog extends android.app.Dialog {


    private final Parameter p;
    Context context;
    protected RelativeLayout view;
    View backView;
    TextView messageTextView;
    TextView titleTextView;

    Button positiveButton;


    protected Dialog(Parameter p) {
        super(p.context, android.R.style.Theme_Translucent);
        this.p = p;
        context = p.context;
    }


    public static class Builder {

        protected Parameter Pt;

        public Builder(Context context) {
            Pt = new Parameter();
            Pt.context = context;
        }

        public Builder setTitle(int title) {
            Pt.title = Pt.context.getString(title);
            return this;
        }

        public Builder setMessage(int message) {
            Pt.message = Pt.context.getString(message);
            return this;
        }

        public Builder setNegativeButton(int negativeText, View.OnClickListener negativieListener) {
            Pt.negativeText = Pt.context.getString(negativeText);
            Pt.negativeListener = negativieListener;
            return this;
        }

        public Builder setPositiveButton(int positiveText, View.OnClickListener positiveListener) {
            Pt.positiveText = Pt.context.getString(positiveText);
            Pt.positiveListener = positiveListener;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            Pt.cancelAble = cancelable;
            return this;
        }



        public Builder setNegativeButton(int negativeText) {
            Pt.negativeText = Pt.context.getString(negativeText);
            return this;
        }

        public Builder setTitle(String title) {
            Pt.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            Pt.message = message;
            return this;
        }

        public Builder setContentView(int layoutResID) {
            Pt.layoutResID = layoutResID;
            return this;
        }
        public Dialog show() {
            final Dialog dialog = new Dialog(Pt);
            dialog.show();
            return dialog;
        }
        public Dialog create() {
            final Dialog dialog = new Dialog(Pt);
            return dialog;
        }
    }

    protected static class Parameter {
        Context context;
        String title;
        String message;
        boolean cancelAble;
        View.OnClickListener positiveListener;
        View.OnClickListener negativeListener;
        String positiveText;
        String negativeText;
        int layoutResID;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        prepareUI();
    }

    protected int getContentView() {
        return R.layout.dialog;
    }

    private void prepareUI() {

        view = (RelativeLayout) findViewById(R.id.rootLayout);
        backView = findViewById(R.id.dialog_rootView);
        backView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getX() < view.getLeft()
                        || event.getX() > view.getRight()
                        || event.getY() > view.getBottom()
                        || event.getY() < view.getTop()) {
                    if (p.cancelAble)
                        dismiss();
                }
                return false;
            }
        });
        if (p.layoutResID != 0) {
            view.removeAllViews();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            view.addView(LayoutInflater.from(context).inflate(p.layoutResID, null),params);
            return;
        }
        if (p.title != null) {
            titleTextView = (TextView) findViewById(R.id.title);
            titleTextView.setText(p.title);
        }
        if (p.message != null) {
            messageTextView = (TextView) findViewById(R.id.message);
            messageTextView.setText(p.message);
        }


        this.positiveButton = (Button) findViewById(R.id.buttonPositive);
        if (p.positiveText != null) {
            positiveButton.setText(p.positiveText);
        }
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (p.positiveListener != null)
                    p.positiveListener.onClick(v);
            }
        });
        Button negativeButton = (Button) findViewById(R.id.buttonNegative);
        if (p.negativeText != null) {
            negativeButton.setText(p.negativeText);
        }
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (p.negativeListener != null)
                    p.negativeListener.onClick(v);
            }
        });

    }

    @Override
    public void show() {
        super.show();
        // set dialog enter animations
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_main_show_amination));
        backView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_root_show_amin));
    }


    @Override
    public void dismiss() {
        super.dismiss();
//        Animation anim = AnimationUtils.loadAnimation(context, R.anim.dialog_main_hide_amination);
//        anim.setAnimationListener(new Animation.AnimationListener() {
//
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                view.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Dialog.super.dismiss();
//                    }
//                });
//
//            }
//        });
//        Animation backAnim = AnimationUtils.loadAnimation(context, R.anim.dialog_root_hide_amin);
//
//        view.startAnimation(anim);
//        backView.startAnimation(backAnim);
    }


}