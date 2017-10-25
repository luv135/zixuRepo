package com.zigsun.luo.projection.base;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.zigsun.luo.projection.ProjectionActivity;

/**
 * Created by Luo on 2015/6/11.
 */
public class RadioGroupViewPagerListenerHub extends ViewPager.SimpleOnPageChangeListener implements RadioGroup.OnCheckedChangeListener {


    private ViewPager viewPager;

    public RadioGroupViewPagerMap getModel() {
        return model;
    }

    private RadioGroupViewPagerMap model;
    private RadioGroup radioGroup;

    public RadioGroupViewPagerListenerHub(RadioGroupViewPagerMap model,ViewPager viewPager, RadioGroup radioGroup) {
        this.model = model;
        this.viewPager = viewPager;
        this.radioGroup = radioGroup;
        viewPager.setOnPageChangeListener(this);
        radioGroup.setOnCheckedChangeListener(this);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        viewPager.setCurrentItem(model.getViewPagerItemByRadioGroupId(checkedId));
    }

    @Override
    public void onPageSelected(int position) {
        ((RadioButton)radioGroup.findViewById(model.getRadioIdByViewPagerPosition(position))).setChecked(true);
    }




}
