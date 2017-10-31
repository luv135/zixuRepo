package com.zigsun.mobile;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.zigsun.mobile.ui.base.Activity;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class AboutActivity extends Activity {

    @InjectView(R.id.versionTextView)
    TextView versionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_activity_about);
        ButterKnife.inject(this);
        try {
            versionTextView.setText("当前版本为: "+getPackageManager().getPackageInfo("com.zigsun.mobile", 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }
}
