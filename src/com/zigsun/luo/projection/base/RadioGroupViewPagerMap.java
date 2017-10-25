package com.zigsun.luo.projection.base;

import android.support.v4.app.Fragment;
import android.util.SparseArray;

import com.zigsun.mobile.R;
import com.zigsun.luo.projection.fileexplore.ui.FileCategoryFragment;
import com.zigsun.luo.projection.fileexplore.ui.FileLocalFragment;
import com.zigsun.luo.projection.fileexplore.ui.FileUsbFragment;

/**
 * Created by Luo on 2015/6/11.
 * radioGroup 和 ViewPager 对应关系
 */
public  class RadioGroupViewPagerMap {
    private Fragment currentFragment;

    public SparseArray<Fragment> getFragments() {
        return fragments;
    }

    private SparseArray<Fragment> fragments;

    public RadioGroupViewPagerMap(SparseArray<Fragment> fragments) {
        this.fragments = fragments;
    }

    /**
     * @param checkedId radioGroup 中选中的RadioButton的Id
     * @return ViewPager的第几页
     */
    public int getViewPagerItemByRadioGroupId(int checkedId) {
        currentFragment = fragments.get(checkedId);
        return fragments.indexOfKey(checkedId);
    }

    /**
     * @param position ViewPager 的第几页
     * @return ViewPager第几页对应RadioGroup中RadioButton的Id
     */
    public int getRadioIdByViewPagerPosition(int position) {
        currentFragment = fragments.valueAt(position);
        return fragments.keyAt(position);
    }

    /**
     *
     * @return 当前显示的fragment
     */
    public Fragment getCurrentFragment() {
        return currentFragment;
    }
}
