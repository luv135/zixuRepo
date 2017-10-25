package com.zigsun.luo.projection.fileexplore.event;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.RadioGroup;

import com.zigsun.luo.projection.ProjectionActivity;
import com.zigsun.luo.projection.base.RadioGroupViewPagerListenerHub;
import com.zigsun.luo.projection.base.RadioGroupViewPagerMap;

/**
 * Created by Luo on 2015/6/11.
 *
 */
public class FileExploreEvent extends RadioGroupViewPagerListenerHub {


    public FileExploreEvent(RadioGroupViewPagerMap model, ViewPager viewPager, RadioGroup radioGroup) {
        super(model, viewPager, radioGroup);
    }

    public boolean onBackPressed() {
        Fragment currentFragment = getModel().getCurrentFragment();
        return currentFragment instanceof ProjectionActivity.BackPressListener
                && (((ProjectionActivity.BackPressListener) currentFragment).onBackPressed());
    }


}
