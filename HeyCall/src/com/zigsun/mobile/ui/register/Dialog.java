package com.zigsun.mobile.ui.register;

import android.content.Context;
import android.util.Log;

import com.zigsun.mobile.R;

/**
 * Created by Luo on 2015/7/4.
 */
public class Dialog extends com.zigsun.luo.projection.base.Dialog {

    private static final String TAG = "REGISTER_DIALGO";

    private Dialog(com.zigsun.luo.projection.base.Dialog.Parameter p) {
        super(p);
    }
    public static class Builder extends  com.zigsun.luo.projection.base.Dialog.Builder {

        public Builder(Context context) {
            super(context);
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
    @Override
    protected int getContentView() {
        Log.d(TAG,"REGISTER DIALOG CONTENT");
        return R.layout.abc_register_confirm_dialog;
    }
}
