package com.zigsun.mobile.observers;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luo on 2015/6/26.
 *
 */
public class ContactObserver {
    private static final String TAG = ContactObserver.class.getSimpleName();
    private static ContactObserver instance = new ContactObserver();
    private List<ContactDataObserver> observers = new ArrayList<>();

    private ContactObserver() {
    }

    public interface ContactDataObserver {
        void recentDateChange();
    }

    public void registerObserver(ContactDataObserver observer) {
        Log.d(TAG, "registerObserver ");
        if (observers.contains(observer)){
            Log.d(TAG, "registerObserver contain");
            return;
        }
        observers.add(observer);
    }

    public void unRegisterObserver(ContactDataObserver observer) {
        observers.remove(observer);
    }

    public static ContactObserver getInstance() {
        return instance;
    }

    public void notifyDataChange() {
        Log.d(TAG, "notifyDataChange BEGIN");
        for (ContactDataObserver l : observers) {
            Log.d(TAG, "notifyDataChange");
            l.recentDateChange();
        }
    }
}
