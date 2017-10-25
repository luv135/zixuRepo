package com.zigsun.luo.projection.fileexplore;


import android.app.Activity;
import android.app.DownloadManager;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.zigsun.mobile.R;
import com.zigsun.luo.projection.base.MainTabBaseFragment;
import com.zigsun.luo.projection.ProjectionActivity;
import com.zigsun.luo.projection.base.RadioGroupViewPagerMap;
import com.zigsun.luo.projection.base.ViewPagerFragmentAdapter;
import com.zigsun.luo.projection.fileexplore.event.FileExploreEvent;
import com.zigsun.luo.projection.fileexplore.ui.FileCategoryFragment;
import com.zigsun.luo.projection.fileexplore.ui.FileLocalFragment;
import com.zigsun.luo.projection.fileexplore.ui.FileUsbFragment;


/**
 * 文件tab
 */
public class FileExploreFragment extends MainTabBaseFragment implements ProjectionActivity.BackPressListener {

    private static final String TAG = FileExploreFragment.class.getSimpleName();
    private final SparseArray<Fragment> fragments;
    private RadioGroup radioGroup;

    private FileExploreEvent event;
    private RadioGroupViewPagerMap model;
    private ViewPager viewPager;

    public FileExploreFragment() {
        // Required empty public constructor
        fragments = new SparseArray<>();
        fragments.put(R.id.tab_explore_category, new FileCategoryFragment());
        fragments.put(R.id.tab_explore_local, new FileLocalFragment());
        fragments.put(R.id.tab_explore_usb, new FileUsbFragment());;
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView()");
        return inflater.inflate(R.layout.fragment_file_explore, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radioGroup = (RadioGroup) view.findViewById(R.id.file_radio);
        viewPager = (ViewPager) view.findViewById(R.id.fileExploreViewPager);

        model = new RadioGroupViewPagerMap(fragments);
        event = new FileExploreEvent(model,viewPager, radioGroup);

        final FragmentManager supportFragmentManager = ((FragmentActivity) getActivity()).getSupportFragmentManager();
        ViewPagerFragmentAdapter pagerAdapter = new ViewPagerFragmentAdapter(supportFragmentManager, model.getFragments());
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(pagerAdapter);

//        ((RadioButton) radioGroup.findViewById(R.id.tab_explore_category)).setChecked(true);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();


    }
    //    @Override
//    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
//        //        AnimatorSet
//
//        if(enter) {
//            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(radioGroup, "y", -100, 0);
//            objectAnimator.setDuration(500);
//            objectAnimator.start();
//        } else{
//            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(radioGroup, "y", -0, -100);
//            objectAnimator.setDuration(500);
//            objectAnimator.start();
//        }
//        int id = enter ? R.animator.slide_fragment_in : R.animator.slide_fragment_out;
//        final Animator anim = AnimatorInflater.loadAnimator(getActivity(), id);
//        return anim;
//    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public boolean onBackPressed() {
        return event.onBackPressed();

    }

    @Override
    public String getFlagTag() {
        return this.getClass().getSimpleName();
    }
}
