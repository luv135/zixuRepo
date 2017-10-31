package com.zigsun.mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lu on 15-6-22.
 */
public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {
    public Context getContext() {
        return context;
    }

    private final Context context;
    protected List<T> items;
    protected LayoutInflater inflater;

    public BaseAdapter(Context context, List<T> items) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.items = items;
    }

    public BaseAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        items = new ArrayList<>();
        this.context = context;
    }

    public void add(T item) {
        items.add(item);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
