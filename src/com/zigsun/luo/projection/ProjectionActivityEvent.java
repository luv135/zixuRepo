package com.zigsun.luo.projection;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;
import android.widget.RadioGroup;

import com.zigsun.mobile.R;
import com.zigsun.luo.projection.interfaces.ITabVisibilityChangeListener;

/**
 * Created by Luo on 2015/5/27.
 * 界面事件处理
 */
public class ProjectionActivityEvent implements RadioGroup.OnCheckedChangeListener {
    private static final String TAG = ProjectionActivityEvent.class.getSimpleName();
    private ProjectionActivityModel transaction;
    private Activity activity;
    private ITabVisibilityChangeListener tabVisibilityChangeListener;


    public ProjectionActivityEvent(ProjectionActivity projectionActivity, ProjectionActivityModel transaction) {
        Log.d(TAG, "ProjectionActivityEvent()");
        this.activity = projectionActivity;
        this.tabVisibilityChangeListener = projectionActivity;
        this.transaction = transaction;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Log.d(TAG, "onCheckedChanged->checkedId: " + checkedId);

        transaction.tabSelectShowFragmentById(checkedId);
        transaction.setCurrentFragmentById(checkedId);
        if (true) return;


        Fragment fragment = transaction.getFragmentById(checkedId);
        Fragment currentFragment = transaction.getCurrentFragment();


//        if (currentFragment != null && fragment.equals(currentFragment)) {
//            Log.d(TAG, "onCheckedChanged-> same fragment return");
//            return;
//        }

        FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
//    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);

//        Log.d(TAG, "onCheckedChanged-> begin replace!");
        switch (checkedId) {
            case R.id.tab_laptop:
            case R.id.tab_projection:
            case R.id.tab_explore:
            case R.id.tab_switch:
            case R.id.tab_camera:
                fragmentTransaction.add(R.id.fl_main_container, fragment);
//                currentFragment = fragment;
                break;
//            case R.id.tab_camera:
//                fragmentTransaction.add(R.id.fl_camera_container,fragment);
//                currentFragment = fragment;
//                break;
        }
        if (currentFragment != null) {
//            if (currentFragment instanceof CameraFragment) {
//                fragmentTransaction.hide(currentFragment).detach(currentFragment);
//            } else {
            fragmentTransaction.remove(currentFragment);
//            }
        }
        transaction.setCurrentFragment(fragment);
        fragmentTransaction.commit();
    }

    public boolean onBackPressed() {
        return transaction.onBackPressed();
    }

    public void switchTabVisibility() {
        if (tabVisibilityChangeListener.isTabVisible()) {
            tabVisibilityChangeListener.hideTab();
        } else {
            tabVisibilityChangeListener.showTab();
        }

    }
}
