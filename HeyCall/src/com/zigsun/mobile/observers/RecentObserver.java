package com.zigsun.mobile.observers;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luo on 2015/6/26.
 *
 */
public class RecentObserver {
    private static final String TAG = RecentObserver.class.getSimpleName();
    private static RecentObserver instance = new RecentObserver();
    private List<RecentDateChangeListener> observers = new ArrayList<>();

    private RecentObserver() {
    }

    public interface RecentDateChangeListener {
        void recentDateChange();
    }

    public void registerObserver(RecentDateChangeListener observer) {
        Log.d(TAG, "registerObserver ");
        if (observers.contains(observer)){
            Log.d(TAG, "registerObserver contain");
            return;
        }
        observers.add(observer);
    }

    public void unRegisterObserver(RecentDateChangeListener observer) {
        observers.remove(observer);
    }

    public static RecentObserver  getInstance() {
        return instance;
    }

    public void notifyDataChange() {
        Log.d(TAG, "RecentObserver notifyDataChange BEGIN");
        for (RecentDateChangeListener l : observers) {
            Log.d(TAG, "notifyDataChange");
            l.recentDateChange();
        }
    }
}
