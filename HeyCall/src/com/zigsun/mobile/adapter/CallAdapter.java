package com.zigsun.mobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zigsun.mobile.R;
import com.zigsun.mobile.module.ContactItem;
import com.zigsun.mobile.utils.TextNumberMatcher;
import com.zigsun.ui.contact.SortModel;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Luo on 2015/6/30.
 */
public class CallAdapter extends BaseAdapter<SortModel> {

    private TextNumberMatcher colorHelper;

    public CallAdapter(Context context, List<SortModel> items) {
        super(context, items);
        colorHelper = new TextNumberMatcher(context.getResources().getColor(R.color.call_match));
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
        final ContactItem sortModel = (ContactItem) items.get(position);
        holder.uidTextView.setText(sortModel.getNickName()/*+"  "+items.get(position).getUlUserId()*/);
//        holder.telText.setText(sortModel.getName());
        holder.telText.setText(colorHelper.getColorText(sortModel.getName()));
        return convertView;
    }

    public void notifyDataSetChanged(String number) {
        colorHelper.setText(number);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        @InjectView(R.id.nameTextView)
        TextView uidTextView;
        @InjectView(R.id.telText)
        TextView telText;
        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
