package com.zigsun.mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zigsun.mobile.R;
import com.zigsun.mobile.module.MeListItem;
import com.zigsun.mobile.ui.base.CircleImageView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Luo on 2015/6/17.
 * 主界面 我
 */
public class MeAdapter extends BaseAdapter {
    private List<MeListItem> items;

    public MeAdapter(LayoutInflater inflater, List<MeListItem> items) {
        this.inflater = inflater;
        this.items = items;
    }

    private LayoutInflater inflater;


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).type.ordinal();
    }

    @Override
    public int getViewTypeCount() {
        return MeListItem.Type.values().length;
    }

    @Override
    public boolean isEnabled(int position) {
        return items.get(position).type != MeListItem.Type.Space;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MeListItem item = items.get(position);

        ViewHolder holder;
        switch (item.type) {
            case Space:
                if (convertView == null)
                    convertView = inflater.inflate(R.layout.abc_me_fragment_list_item_space, null);
                break;
            case Info:
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.abc_me_fragment_list_item_info, null);
                    holder = new ViewHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                break;
            case Header:
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.abc_me_fragment_list_item_header, null);
                    holder = new HeadViewHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (HeadViewHolder) convertView.getTag();
                }
                break;
        }
        return convertView;
    }


    static class ViewHolder {
        @InjectView(R.id.headerImageView)
        CircleImageView imageView;
        @InjectView(R.id.userNameText)
        TextView titleTextView;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    static class HeadViewHolder extends ViewHolder {
        @InjectView(R.id.qrImageView)
        ImageView qrImageView;

        HeadViewHolder(View view) {
            super(view);
        }
    }

}
