package com.zigsun.luo.projection.base;

import android.app.Fragment;
import android.app.FragmentTransaction;

import com.zigsun.luo.projection.ProjectionActivityModel;

/**
 * Created by Luo on 2015/6/9.
 * tab 内容Fragment,其生命周期同步主activity{@link com.zigsun.luo.projection.ProjectionActivity}
 * 不再replace, 只是进行隐藏和显示.
 * 关联具体见: {@link ProjectionActivityModel#attachToActivity()}
 */
public abstract class MainTabBaseFragment extends Fragment {

    /**
     * 显示fragment,未commit()
     */
    public  void showSelf(FragmentTransaction transaction){
        transaction.show(this);
    }

    /**
     *  隐藏fragment,未commit()
     */
    public void hideSelf(FragmentTransaction transaction) {
        transaction.hide(this);
    }
    public abstract String getFlagTag();
}
