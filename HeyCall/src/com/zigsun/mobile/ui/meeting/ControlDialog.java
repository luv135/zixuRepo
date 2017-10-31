package com.zigsun.mobile.ui.meeting;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import com.zigsun.mobile.R;

/**
 * Created by Luo on 2015/7/20.
 */
public class ControlDialog extends Dialog {
    public ControlDialog(Context context) {
        super(context, android.R.style.Theme_Translucent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_control_dialog);
    }
}
