package com.zigsun.mobile.ui.base;

import android.content.Context;
import android.text.*;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.zigsun.mobile.R;
import com.zigsun.mobile.ui.register.RegisterInputIdCodeStep02FragmentActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by Luo on 2015/7/13.
 */
public class CodeEditText extends LinearLayout implements View.OnKeyListener {
    private static final String TAG = CodeEditText.class.getSimpleName();
    @InjectView(R.id.editCode01)
    EditText editCode01;
    @InjectView(R.id.editCode02)
    EditText editCode02;
    @InjectView(R.id.editCode03)
    EditText editCode03;
    @InjectView(R.id.editCode04)
    EditText editCode04;
    private TextWatcher watcher;

    private void prepareUI(Context context) {
        LayoutInflater.from(context).inflate(R.layout.abc_edit_code_layout, this);
        ButterKnife.inject(this);

    }

    public CodeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        prepareUI(context);
        editCode01.addTextChangedListener(new TextWatcherImpl() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1)
                    editCode02.requestFocus();
                if (watcher != null) watcher.onTextChanged(getEditString(), 0, 0, 0);
            }

        });
        editCode02.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1)
                    editCode03.requestFocus();
                if (watcher != null) watcher.onTextChanged(getEditString(), 0, 0, 0);
            }

        });
        editCode03.addTextChangedListener(new TextWatcherImpl() {


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1)
                    editCode04.requestFocus();
                if (watcher != null) watcher.onTextChanged(getEditString(), 0, 0, 0);
            }

        });
        editCode04.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (watcher != null) watcher.onTextChanged(getEditString(), 0, 0, 0);
            }
        });
        editCode02.setOnKeyListener(this);
        editCode03.setOnKeyListener(this);
        editCode04.setOnKeyListener(this);
    }

    public String getEditString() {
        return getString(editCode01) + getString(editCode02) + getString(editCode03) + getString(editCode04);
    }


    public void addTextChangedListener(TextWatcher watcher) {
        this.watcher = watcher;
    }


    private String getString(EditText edit) {
        final String s = edit.getText().toString();
        return TextUtils.isEmpty(s) ? "" : s;
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (KeyEvent.ACTION_UP == event.getAction() && keyCode == KeyEvent.KEYCODE_DEL) {
            EditText editText = null;
            EditText pre = null;
            switch (v.getId()) {
                case R.id.editCode02:
                    editText = editCode02;
                    pre = editCode01;
                    break;
                case R.id.editCode03:
                    editText = editCode03;
                    pre = editCode02;
                    break;
                case R.id.editCode04:
                    editText = editCode04;
                    pre = editCode03;
                    break;
            }

            if (editText != null) {
                final Object tag = editText.getTag();
                if (tag != null && ((Boolean) tag)) {
                    editText.setTag(false);
                    pre.requestFocus();
                    return true;
                }
                if (editText.getText().length() == 0) {
                    editText.setTag(true);
                }
            }
            return true;
        }

        return false;
    }

    public void setError(String string) {
        editCode04.setError(string);
    }
}
