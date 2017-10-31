package com.zigsun.mobile.ui;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zigsun.luo.projection.utils.Util;
import com.zigsun.mobile.R;
import com.zigsun.mobile.adapter.ViewPagerFragmentAdapter;
import com.zigsun.mobile.interfaces.LayoutChange;
import com.zigsun.mobile.interfaces.SoftInputVisible;
import com.zigsun.mobile.model.ContactsModel;
import com.zigsun.mobile.ui.base.AbsBaseViewPagerFragment;
import com.zigsun.mobile.ui.base.AnimationListener;
import com.zigsun.mobile.ui.base.Dialog;
import com.zigsun.mobile.ui.base.FrameLayout;
import com.zigsun.mobile.ui.base.InterViewPager;
import com.zigsun.mobile.ui.base.SlidingTabLayout;
import com.zigsun.mobile.ui.child.CallFragment;
import com.zigsun.mobile.ui.child.ContactsFragment;
import com.zigsun.mobile.ui.child.MeFragment;
import com.zigsun.mobile.ui.child.RecentFragment;
import com.zigsun.mobile.ui.friend.SearchFriendActivity;
import com.zigsun.mobile.utils.Utils;
import com.zigsun.util.CONSTANTS;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends FragmentActivity implements LayoutChange {

    public static final int EXIST = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    @InjectView(R.id.sliding_tabs)
    SlidingTabLayout slidingTabs;
    @InjectView(R.id.viewPager)
    InterViewPager viewPager;
    @InjectView(R.id.headContainerLayout)
    FrameLayout headContainerLayout;
    @InjectView(R.id.addTitleButton)
    ImageButton addTitleButton;
    @InjectView(R.id.navigationLayout)
    LinearLayout navigationLayout;

    private MainActivityEvent event;
    private ViewPagerFragmentAdapter fragmentAdapter;
    //    private MenuItem item;
    private BroadCastReceiver receiver;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        moveTaskToBack(true);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    public void addView(View view) {
        viewPager.setInterceptTouch(true);
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
//            actionBar.hide();
        }
        Utils.hideView(this, navigationLayout,R.anim.hide_toup);
        Utils.showView(this, headContainerLayout, R.anim.keyboard_showin_frombottom_up);
//        headContainerLayout.setVisibility(View.VISIBLE);
        headContainerLayout.addView(view);
//        item.setVisible(false);
    }

    @Override
    public void removeView(View view) {
        final ActionBar actionBar = getActionBar();
//        if (actionBar != null) actionBar.show();
        viewPager.setInterceptTouch(false);
        headContainerLayout.removeView(view);
        Utils.showView(this, navigationLayout,R.anim.showin_fromup);
        Utils.hideView(this, headContainerLayout, R.anim.keyboard_hide_tobottom);
//        headContainerLayout.setVisibility(View.INVISIBLE);
//        item.setVisible(true);

    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult" + requestCode + " resultCode: " + resultCode);
        if (requestCode == EXIST && resultCode == RESULT_OK) {
            finish();
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    private class MainActivityEvent extends ViewPager.SimpleOnPageChangeListener implements View.OnClickListener {
        private Fragment lastFragment;

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                tryHideSoftInput();
            }
        }

        @Override
        public void onPageSelected(int position) {
            tryHideSoftInput();
            lastFragment = fragmentAdapter.getItem(position);
        }

        private void tryHideSoftInput() {
            if (lastFragment instanceof SoftInputVisible) {
                ((SoftInputVisible) lastFragment).hideSoftInput(null);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.addTitleButton:
                    startActivity(new Intent(MainActivity.this, SearchFriendActivity.class));

                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_title_background));
        }
        setContentView(R.layout.abc_activity_main);
        ButterKnife.inject(this);
        initData();
        event = new MainActivityEvent();

        viewPager.addOnPageChangeListener(event);
        List<AbsBaseViewPagerFragment> fragments = getFragments();
        fragmentAdapter = new ViewPagerFragmentAdapter(this, getSupportFragmentManager(), fragments);

        viewPager.setAdapter(fragmentAdapter);
        slidingTabs.setCustomTabView(R.layout.abc_tab_item, R.id.text);
        slidingTabs.setViewPager(viewPager);
        receiver = new BroadCastReceiver();
        final IntentFilter filter = new IntentFilter(
                CONSTANTS.EMEETING_ACTION_LOGIN);
        filter.addAction(CONSTANTS.ACTION_UPDATE_USER_STATUS);
        registerReceiver(receiver, filter);
        addTitleButton.setOnClickListener(event);
    }

    private void initData() {

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    /**
     * 底层业务交互广播接收
     */
    public class BroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            switch (action) {
                case CONSTANTS.ACTION_UPDATE_USER_STATUS:
                    long id = intent.getLongExtra("ulUserID", 0);
                    byte st = intent.getByteExtra("ucStatus", (byte) 0);
                    Log.d(TAG, "receive->　userID: " + id + "  staus:" + st);
                    new ContactsModel().updateUserStatus(id, st);
                    break;
                case CONSTANTS.EMEETING_ACTION_LOGIN:

                    int flag = intent.getExtras().getInt("flag");
                    switch (flag) {
                        case CONSTANTS.DISCONNECT_SERVER:
                            Log.w(TAG, "服务器断开");
//                            Toast.makeText(getApplicationContext(), R.string.abc_service_disconnected, Toast.LENGTH_SHORT).show();
                            new Dialog.Builder(context).setTitle(R.string.abc_hint).setMessage(R.string.abc_log_out)
                                    .setPositiveButton(R.string.abc_ok, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            finish();
                                        }
                                    }).show();
//                    finish();
                            break;


                    }

                    break;
            }

        }
    }

    private List<AbsBaseViewPagerFragment> getFragments() {
        List<AbsBaseViewPagerFragment> fragments = new ArrayList<>();
        fragments.add(new RecentFragment());
        fragments.add(new ContactsFragment());
        fragments.add(new CallFragment());
        fragments.add(new MeFragment());
        return fragments;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.abc_menu_main, menu);
////        item = menu.findItem(R.id.abc_action_add_contact);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.abc_action_add_contact) {
//            startActivity(new Intent(this, SearchFriendActivity.class));
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
