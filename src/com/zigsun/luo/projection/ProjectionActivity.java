package com.zigsun.luo.projection;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.zigsun.EMeetingApplication;
import com.zigsun.mobile.R;
import com.zigsun.luo.projection.base.Dialog;
import com.zigsun.luo.projection.base.ViewPagerFragmentAdapter;
import com.zigsun.luo.projection.battery.BatteryChangeReceiver;
import com.zigsun.luo.projection.battery.IBatteryListener;
import com.zigsun.luo.projection.fileexplore.FileExploreFragment;
import com.zigsun.luo.projection.interfaces.IShareInterface;
import com.zigsun.luo.projection.interfaces.ITabVisibilityChangeListener;
import com.zigsun.luo.projection.share.ShareButtonManager;
import com.zigsun.luo.projection.ui.CameraFragment;
import com.zigsun.luo.projection.ui.LaptopFragment;
import com.zigsun.luo.projection.ui.MobileProjectionFragment;
import com.zigsun.luo.projection.ui.SwitchFragment;
import com.zigsun.luo.projection.utils.Util;
import com.zigsun.no_screen.voice_conference.AddContactsActivity;
import com.zigsun.no_screen.voice_conference.VoiceConferenceActivity;

import de.greenrobot.event.EventBus;

/**
 *
 */
public class ProjectionActivity extends FragmentActivity implements
        ITabVisibilityChangeListener, IBatteryListener, SwitchFragment.InitListener, IShareInterface {


    // @TargetApi(19)
    // private void setTranslucentStatus(boolean on) {
    // Window win = getWindow();
    // WindowManager.LayoutParams winParams = win.getAttributes();
    // final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
    // if (on) {
    // winParams.flags |= bits;
    // } else {
    // winParams.flags &= ~bits;
    // }
    // win.setAttributes(winParams);

    private ProjectionActivityEvent event;
    private ProjectionActivityModel transaction;
    private static final String TAG = ProjectionActivity.class.getSimpleName();
    private RadioGroup radioGroup;
    private boolean tabVisible;
    private ShareButtonManager shareButtonManager;

    private EMeetingApplication application;
    private Button shareButton;
    private SparseArray<Fragment> fragments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projection);
        transaction = new ProjectionActivityModel(this);
        event = new ProjectionActivityEvent(this, transaction);
        prepareUI();
        tabVisible = true;

        Log.d(TAG, "onCreate");

        // //---状态栏颜色
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        // setTranslucentStatus(true);
        // }
        // SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // tintManager.setStatusBarTintEnabled(true);
        // tintManager.setStatusBarTintResource(R.color.statusbar_bg);
        // ---


        EventBus.getDefault().register(this);
        BatteryChangeReceiver.Helper.registerReceiver(this);
//        radioGroup.check(R.id.tab_switch);

    }


    private void prepareUI() {

        shareButton = (Button) findViewById(R.id.shareButton);
        radioGroup = (RadioGroup) findViewById(R.id.main_radio);


        radioGroup = (RadioGroup) findViewById(R.id.main_radio);
        radioGroup.setOnCheckedChangeListener(event);
        shareButton = (Button) findViewById(R.id.shareButton);

        radioGroup.setOnCheckedChangeListener(event);

        radioGroup.setVisibility(View.VISIBLE);
        shareButtonManager = new ShareButtonManager(this, shareButton);
        ((RadioButton) findViewById(R.id.tab_switch)).setChecked(true);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //浮动窗口
//        ShareWindowManager.getDefaultInstance().onResume();
//        initFinish();
        Log.d(TAG, "onResume");

    }

    /**
     * 请求root对话框
     */
    public void onEvent(RequestRootMessage message) {

        new Dialog.Builder(this).setTitle(R.string.root_title)
                .setMessage(R.string.root_message)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("http://www.wandoujia.com/search?key=root");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).setNegativeButton(R.string.cancel).show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        EventBus.getDefault().unregister(this);

        BatteryChangeReceiver.Helper.unregisterReceiver(this);

    }


    @Override
    public void onBackPressed() {
        if (!event.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void showTab() {
        tabVisible = true;
        radioGroup.animate().translationYBy(-radioGroup.getHeight()).start();
    }

    @Override
    public boolean isTabVisible() {
        return tabVisible;
    }

    @Override
    public void hideTab() {
        tabVisible = false;
        radioGroup.animate().translationYBy(radioGroup.getHeight())/*.setListener(new AnimatorListenerImpl() {
            @Override
			public void onAnimationEnd(Animator animation) {
				radioGroup.setVisibility(View.GONE);
			}
		})*/.start();
    }

    @Override
    public void switchTabVisibility() {
        event.switchTabVisibility();
    }

    @Override
    public void powerDisconnected() {
        transaction.powerDisconnected();
    }

    @Override
    public void powerConnected() {
        transaction.powerConnected();
    }

    @Override
    public void initFinish() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.bottom_tab_init_finish);
        View laptop = findViewById(R.id.tab_laptop);
        View projection = findViewById(R.id.tab_projection);
        View tabSwitch = findViewById(R.id.tab_switch);
        View explore = findViewById(R.id.tab_explore);
        View camera = findViewById(R.id.tab_camera);

        radioGroup.setVisibility(View.VISIBLE);
        Util.startAnimation(animation, laptop, projection, tabSwitch, explore, camera);
//        ShareWindowManager.getDefaultInstance().showShareWindow();
    }

    @Override
    public void openScreenShare() {
        startActivity(new Intent(getApplicationContext(),
                VoiceConferenceActivity.class));
        shareButtonManager.updateUI(true);
    }

    @Override
    public void closeScreenShare() {
        shareButtonManager.updateUI(false);
    }

    @Override
    public boolean screenIsShare() {
        return false;
    }


    public interface BackPressListener {
        boolean onBackPressed();
    }

}
