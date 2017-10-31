package com.zigsun.mobile.ui.base;

import android.app.ActionBar;
import android.os.Bundle;

import android.util.Log;

import com.zigsun.mobile.R;

/**
 * Created by Luo on 2015/7/4.
 */
public class FragmentActivity extends android.support.v4.app.FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_title_background));
        } else{
//            Log.d("BASEFRAGMENT","NO ACTIONBAR ");
        }
    }
}
