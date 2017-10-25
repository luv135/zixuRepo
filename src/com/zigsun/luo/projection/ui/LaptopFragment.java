package com.zigsun.luo.projection.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zigsun.mobile.R;
import com.zigsun.luo.projection.base.MainTabBaseFragment;


public class LaptopFragment extends MainTabBaseFragment {


    public LaptopFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_laptop, container, false);
    }

    @Override
    public String getFlagTag() {
        return this.getClass().getSimpleName();
    }
}
