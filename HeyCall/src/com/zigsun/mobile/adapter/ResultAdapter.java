package com.zigsun.mobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zigsun.mobile.R;
import com.zigsun.mobile.module.ResultItem;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Luo on 2015/6/30.
 */
public class ResultAdapter extends BaseAdapter<ResultItem> {


    public ResultAdapter(Context context) {
        super(context);
    }

    @Override
    public void add(ResultItem item) {
        super.add(item);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.abc_result_item_layout, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final int exist = items.get(position).isExist()? R.string.abc_firend: R.string.abc_stranger;
        String s = getContext().getString(exist);
        holder.uidTextView.setText(items.get(position).getStrUserName()+"  "+ s);
        return convertView;
    }


    static class ViewHolder {
        @InjectView(R.id.nameTextView)
        TextView uidTextView;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
