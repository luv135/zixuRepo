package com.zigsun.mobile.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zigsun.mobile.R;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class Dialog extends android.app.Dialog {


    private final Params P;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.message)
    TextView message;
    @InjectView(R.id.contentLayout)
    LinearLayout contentLayout;
    @InjectView(R.id.message_scrollView)
    ScrollView messageScrollView;
    @InjectView(R.id.buttonNegative)
    Button buttonNegative;
    @InjectView(R.id.buttonPositive)
    Button buttonPositive;
    @InjectView(R.id.rootLayout)
    RelativeLayout rootLayout;
    @InjectView(R.id.dialog_rootView)
    RelativeLayout dialogRootView;

    public static class Builder {
        private Params P;
        private int mTheme;


        public Builder(Context context) {
            P = new Params();
            P.mContext = context;
        }


        public Context getContext() {
            return P.mContext;
        }


        public Builder setTitle(int titleId) {
            P.mTitle = P.mContext.getText(titleId);
            return this;
        }


        public Builder setTitle(CharSequence title) {
            P.mTitle = title;
            return this;
        }


        public Builder setMessage(int messageId) {
            P.mMessage = P.mContext.getText(messageId);
            return this;
        }

        public Builder setMessage(CharSequence message) {
            P.mMessage = message;
            return this;
        }


        public Builder setPositiveButton(int textId, final View.OnClickListener listener) {
            P.mPositiveButtonText = P.mContext.getText(textId);
            P.mPositiveButtonListener = listener;
            return this;
        }

        public Builder setPositiveButton(CharSequence text, final View.OnClickListener listener) {
            P.mPositiveButtonText = text;
            P.mPositiveButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(int textId, final View.OnClickListener listener) {
            P.mNegativeButtonText = P.mContext.getText(textId);
            P.mNegativeButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(CharSequence text, final View.OnClickListener listener) {
            P.mNegativeButtonText = text;
            P.mNegativeButtonListener = listener;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            P.mCancelable = cancelable;
            return this;
        }

        public Builder setContentView(View view) {
            P.mContentView = view;
            return this;
        }


        public Dialog create() {
            final Dialog dialog = new Dialog(P);
            return dialog;
        }

        public Dialog show() {
            Dialog dialog = create();
            dialog.show();
            return dialog;
        }
    }

    public Dialog(Params P) {
        super(P.mContext, android.R.style.Theme_Translucent);
        this.P = P;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.abc_base_dialog);
        ButterKnife.inject(this);
        prepareUI();
    }

    private void prepareUI() {
        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        dialogRootView = (RelativeLayout) findViewById(R.id.dialog_rootView);
        dialogRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getX() < rootLayout.getLeft()
                        || event.getX() > rootLayout.getRight()
                        || event.getY() > rootLayout.getBottom()
                        || event.getY() < rootLayout.getTop()) {
                    if (P.mCancelable)
                        dismiss();
                }
                return false;
            }
        });
        title.setText(P.mTitle);
        if (P.mContentView == null)
            message.setText(P.mMessage);
        else {
            contentLayout.removeAllViews();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            contentLayout.addView(P.mContentView, params);
        }

        if (P.mPositiveButtonListener != null) {
            buttonPositive.setText(P.mPositiveButtonText);
            buttonPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    P.mPositiveButtonListener.onClick(v);
                    dismiss();
                }
            });
        } else {
            buttonPositive.setVisibility(View.GONE);
        }
        if (P.mNegativeButtonListener != null) {
            buttonNegative.setText(P.mNegativeButtonText);

            buttonNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    P.mNegativeButtonListener.onClick(v);
                    dismiss();
                }
            });
        } else {
            buttonNegative.setVisibility(View.GONE);
        }

    }


    @Override
    public void setContentView(int layoutResID) {

    }

    @Override
    public void setContentView(View view) {

    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {

    }

    private static class Params {
        public Context mContext;
        public CharSequence mTitle;
        public CharSequence mMessage;
        public CharSequence mPositiveButtonText;
        public View.OnClickListener mPositiveButtonListener;
        public CharSequence mNegativeButtonText;
        public View.OnClickListener mNegativeButtonListener;
        public boolean mCancelable;
        public View mContentView;
    }
}
