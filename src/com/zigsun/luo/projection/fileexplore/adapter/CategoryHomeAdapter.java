package com.zigsun.luo.projection.fileexplore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zigsun.mobile.R;

import com.zigsun.luo.projection.fileexplore.helper.FileInfo;
import com.zigsun.luo.projection.utils.Util;

import java.util.List;

/**
 * Created by Luo on 2015/5/19.
 */
public class CategoryHomeAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<FileInfo> fileList;

    public CategoryHomeAdapter(Context context,List<FileInfo> fileList) {
        inflater = LayoutInflater.from(context);
        this.fileList = fileList;
    }

    @Override
    public int getCount() {
        return fileList.size();
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
        View view;
        Holder holder;
        if (convertView == null) {
            view = inflater.inflate(R.layout.framgent_categroy_home, null);
            holder = new Holder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (Holder) view.getTag();
        }
        FileInfo fileInfo = fileList.get(position);

        Util.setText(holder.name, fileInfo.fileName);
        Util.setText(holder.description, "(" + fileInfo.Count + ")");
        holder.icon.setImageResource(fileInfo.image);
        return view;
    }

    private class Holder {
        public ImageView icon;
        public TextView name;
        public TextView description;

        public Holder(View root) {
            icon = (ImageView) root.findViewById(R.id.iv_category);
            name = (TextView) root.findViewById(R.id.tv_category_text);
            description = (TextView) root.findViewById(R.id.tv_category_count);
        }
    }
}
