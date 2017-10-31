package com.zigsun.mobile.adapter;

import android.app.Notification;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zigsun.bean.UserInfo;

import java.util.List;

/**
 * Created by Luo on 2015/6/29.
 */
public class InComingAdapter extends BaseAdapter<UserInfo> {
    public InComingAdapter(Context context, List<UserInfo> items) {
        super(context, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }


}
