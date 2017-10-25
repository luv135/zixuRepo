package com.zigsun.luo.projection.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import java.util.List;

/**
 * Created by Luo on 2015/6/11.
 */
public class ViewPagerFragmentAdapter extends FragmentPagerAdapter {
    private final SparseArray<Fragment> fragments;

    public ViewPagerFragmentAdapter(FragmentManager fm, SparseArray<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }


    @Override
    public int getCount() {
        return fragments.size();
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        return fragments.valueAt(position);
    }
}
