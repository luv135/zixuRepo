package com.zigsun.mobile.ui.meeting;

import android.animation.Animator;
import android.app.Activity;
import android.app.job.JobScheduler;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;

import com.zigsun.mobile.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class testActivity extends Activity {


    private static final String TAG = testActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        ButterKnife.inject(this);
        final WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        Log.d(TAG, "level: "+WifiManager.calculateSignalLevel(connectionInfo.getRssi(),9));

//        JobScheduler

//        new AnsModel(toAcceptButton, toRejectButton, startClickButton, toMessageButton);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
