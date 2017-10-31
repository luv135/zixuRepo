package com.zigsun.mobile.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zigsun.mobile.ui.base.AbsBaseViewPagerFragment;

import java.util.List;

/**
 * Created by Luo on 2015/6/11.
 * 主界面
 */
public class ViewPagerFragmentAdapter extends FragmentPagerAdapter {
    private final List<AbsBaseViewPagerFragment> fragments;
    private final Context context;

    public ViewPagerFragmentAdapter(Context context, FragmentManager fm, List<AbsBaseViewPagerFragment> fragments) {
        super(fm);
        this.context = context;
        this.fragments = fragments;
    }


    @Override
    public int getCount() {
        return fragments.size();
    }


    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(fragments.get(position).getPageTitle());
    }
}
