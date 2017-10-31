package com.zigsun.mobile.model;

import android.util.Log;

import com.zigsun.db.RecentDBHelper;
import com.zigsun.mobile.module.RecenListItem;
import com.zigsun.mobile.module.RecentNickItem;
import com.zigsun.mobile.utils.RecentUtil;
import com.zigsun.mobile.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Luo on 2015/6/26.
 */
public class RecentModel {
    private static final String TAG = RecentModel.class.getSimpleName();
    private List<RecenListItem> recenListItems = new ArrayList<>();
    private RecentDBHelper helper;

//    private RecentDateChangeListener listener;

    public RecentModel(/*RecentDateChangeListener listener*/) {
        /*if (listener == null)
            throw new NullPointerException("must implement RecentDateChangeListener");
        this.listener = listener;*/
        helper = new RecentDBHelper();

    }


    public boolean deleteRecord(int postion) {
        final RecenListItem po = recenListItems.get(postion);
        final boolean b = helper.remove(po.getId()) != -1;
        Log.d(TAG, po.toString()+" :"+b);
//        listener.recentDateChange();
        return b;
    }

    public boolean addCallRecord(RecenListItem item) {
        final boolean b = helper.create(item) != -1;
//        listener.recentDateChange();
        return b;
    }

    public List<RecenListItem> getHistroyCall() {
        recenListItems.clear();
        final List<RecenListItem> collection = helper.queryForAll();
        final ContactsModel contactsModel = new ContactsModel();
        Log.d(TAG, "getHistroyCall");
        for (RecenListItem c : collection) {
            Log.d(TAG, "history call:"+c.toString());
            final RecentNickItem wrap = Utils.wrap(c);
            final String[] tel = RecentUtil.switchPeople(wrap.getPeople());
            StringBuilder buffer = new StringBuilder();
            for (String s : tel) {
                buffer.append(contactsModel.qureyName(s)).append(".");
            }
            wrap.setNickName(buffer.toString());
            recenListItems.add(wrap);
        }
//        recenListItems.addAll(collection);
        Collections.reverse(recenListItems);
        return recenListItems;
    }

}
