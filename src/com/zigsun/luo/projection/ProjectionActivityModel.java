package com.zigsun.luo.projection;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.zigsun.mobile.R;
import com.zigsun.luo.projection.base.MainTabBaseFragment;
import com.zigsun.luo.projection.battery.IBatteryListener;
import com.zigsun.luo.projection.fileexplore.FileExploreFragment;
import com.zigsun.luo.projection.ui.CameraFragment;
import com.zigsun.luo.projection.ui.LaptopFragment;
import com.zigsun.luo.projection.ui.MobileProjectionFragment;
import com.zigsun.luo.projection.ui.SwitchFragment;

/**
 * Created by Luo on 2015/5/27.
 * 逻辑处理
 */
public class ProjectionActivityModel implements IBatteryListener {


    private static final String TAG = ProjectionActivityModel.class.getSimpleName();
    private Fragment currentFragment;
    private static SparseArray<MainTabBaseFragment> fragments = new SparseArray<>();

    static {
        fragments.put(R.id.tab_laptop, new LaptopFragment());
        fragments.put(R.id.tab_camera, new CameraFragment());
//        fragments.put(R.id.tab_camera, activity.getFragmentManager().findFragmentById(R.id.cameraFragment));
        fragments.put(R.id.tab_explore, new FileExploreFragment());
        fragments.put(R.id.tab_projection, new MobileProjectionFragment());
        fragments.put(R.id.tab_switch, new SwitchFragment());
    }

    private Activity activity;

    public ProjectionActivityModel(Activity activity) {
        Log.d(TAG, "ProjectionActivityModel(), fragments is null? " + (fragments == null));
        this.activity = activity;

        attachToActivity();
        // 此处, 平板左下角弹通知窗,点击进入360, 会重新构造,导致子fragment有两个实例
        //但改为静态,却不会重新构造.(⊙﹏⊙)b
//        fragments = new SparseArray<>();
//        fragments.put(R.id.tab_laptop, new LaptopFragment());
//        fragments.put(R.id.tab_camera, new CameraFragment());
////        fragments.put(R.id.tab_camera, activity.getFragmentManager().findFragmentById(R.id.cameraFragment));
//        fragments.put(R.id.tab_explore, new FileExploreFragment());
//        fragments.put(R.id.tab_projection, new MobileProjectionFragment());
//        fragments.put(R.id.tab_switch, new SwitchFragment());


    }

    /**
     * 添加到activity
     */
    private void attachToActivity() {
        final FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
        for (int i = 0; i < fragments.size(); i++) {
            final Fragment fragment = fragments.valueAt(i);
            fragmentTransaction.add(R.id.fl_main_container, fragment).hide(fragment);
        }
        fragmentTransaction.commit();
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(Fragment currentFragment) {
        this.currentFragment = currentFragment;

    }

    public void tabSelectShowFragmentById(int id) {
        final FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
        for (int i = 0; i < fragments.size(); i++) {
            final int key = fragments.keyAt(i);
            final MainTabBaseFragment fragment = fragments.get(key);
            if (key == id) {
                fragment.showSelf(fragmentTransaction);
//                fragmentTransaction.show(fragment);
//                if (fragment instanceof ICameraControl) {
//                    ((ICameraControl) fragment).openCamera();
//                }
                continue;
            }
            fragment.hideSelf(fragmentTransaction);
//            if (fragment instanceof ICameraControl) {
//                ((ICameraControl) fragment).closeCamera();
//            }
//            fragmentTransaction.hide(fragment);
        }
        fragmentTransaction.commit();
    }


    public boolean onBackPressed() {
        return currentFragment instanceof ProjectionActivity.BackPressListener
                && ((ProjectionActivity.BackPressListener) currentFragment).onBackPressed();
    }

    public Fragment getFragmentById(int checkedId) {
        return fragments.get(checkedId);

    }

    @Override
    public void powerDisconnected() {
        //当前不是switchFragment, 则Toast 提示
        if (!(currentFragment instanceof IBatteryListener)) {
            Toast.makeText(activity, R.string.connect_power_please, Toast.LENGTH_SHORT).show();
        }
        //更新switchFragment 状态
        Fragment fragment = fragments.get(R.id.tab_switch);
        ((IBatteryListener) fragment).powerDisconnected();
    }

    @Override
    public void powerConnected() {
        //当前不是switchFragment, 则Toast 提示
        if (!(currentFragment instanceof IBatteryListener)) {
            Toast.makeText(activity, R.string.power_connected, Toast.LENGTH_SHORT).show();
        }
        //更新switchFragment 状态
        Fragment fragment = fragments.get(R.id.tab_switch);
        ((IBatteryListener) fragment).powerConnected();
    }

    public void setCurrentFragmentById(int checkedId) {
        currentFragment = fragments.get(checkedId);
    }
}
