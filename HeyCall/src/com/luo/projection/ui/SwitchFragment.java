package com.zigsun.luo.projection.ui;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zigsun.mobile.R;
import com.zigsun.luo.projection.base.MainTabBaseFragment;
import com.zigsun.luo.projection.battery.IBatteryListener;

/**
 * 开关
 * @author Luo wei
 */
public class SwitchFragment extends MainTabBaseFragment implements IBatteryListener {


    private static final String TAG = SwitchFragment.class.getSimpleName();
    private TextView textView;

    private boolean initIsFinish;

    public SwitchFragment() {
        // Required empty public constructor
        Log.d(TAG, "SwitchFragment");
        initIsFinish = false;
    }

    private InitListener listener;

    @Override
    public String getFlagTag() {
        return this.getClass().getSimpleName();
    }

    public interface InitListener {
        void initFinish();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (InitListener) activity;
        } catch (Exception e) {
            throw new ClassCastException("activity must implement InitListener interface");
        }
        Log.d(TAG, "onAttach");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        Log.d(TAG, "onDetach");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView");


        return inflater.inflate(R.layout.fragment_switch, container, false);



    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        textView = (TextView) view.findViewById(R.id.tv_init);
//        if (initIsFinish) {
//            textView.setText(R.string.projection_init_finish);
//        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    public void powerDisconnected() {
        Log.d(TAG, "powerDisconnected");
        textView.setText(R.string.power_disconnected);
    }

    @Override
    public void powerConnected() {
        Log.d(TAG, "powerConnected");
        if (!initIsFinish)
            simulate();
        else {
            textView.setText(R.string.projection_init_finish);
        }
//        textView.setText(R.string.power_connected);
//        listener.initFinish();
    }

    public void simulate() {
        textView.setText(R.string.init_projection);
        textView.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setText(R.string.projection_init_finish);
//                if (!initIsFinish &&listener != null) listener.initFinish();
                listener = null; //初始化成功便不再需要回调
                initIsFinish = true;
            }
        }, 2000);
    }

    /**
     * 之前已经初始化成功,此处恢复成功的状态.
     */
    private void initFinish() {
        textView.setText(R.string.projection_init_finish);

    }
}
