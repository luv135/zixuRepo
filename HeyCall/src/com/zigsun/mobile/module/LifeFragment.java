package com.zigsun.mobile.module;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * 关联Activity 生命周期
 */
public class LifeFragment extends Fragment {

    public static final String TAG = LifeFragment.class.getSimpleName();

    public LifeFragment() {
        life = new Life() {
            @Override
            public void onCreate() {
//                throw new IllegalStateException("Life interface is null");

            }

            @Override
            public void onDestroy() {
//                throw new IllegalStateException("Life interface is null");
            }

            @Override
            public void onResume() {
//                throw new IllegalStateException("Life interface is null");
            }

            @Override
            public void onPause() {
//                throw new IllegalStateException("Life interface is null");
            }
        };
    }

    @SuppressLint("ValidFragment")
    public LifeFragment(Life life) {
        this.life = life;
    }


    public void setLife(Life life) {
        this.life = life;
    }

    private Life life;

    public interface Life {
        void onCreate();

        void onDestroy();

        void onResume();

        void onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        life.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        life.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        life.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        life.onPause();
    }
}