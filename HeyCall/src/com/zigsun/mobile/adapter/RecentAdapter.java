package com.zigsun.mobile.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zigsun.mobile.R;
import com.zigsun.mobile.module.CallStatus;
import com.zigsun.mobile.module.RecenListItem;
import com.zigsun.mobile.module.RecentNickItem;
import com.zigsun.mobile.utils.RecentUtil;
import com.zigsun.mobile.utils.TimeUtil;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Luo on 2015/6/26.
 */
public class RecentAdapter extends BaseAdapter<RecenListItem> {


    public RecentAdapter(Context context, List<RecenListItem> items) {
        super(context, items);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.abc_recent_list_view_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        prepareDate(holder, items.get(position));

        return convertView;
    }

    private void prepareDate(ViewHolder holder, RecenListItem recenListItem) {
        RecentNickItem item = (RecentNickItem) recenListItem;
        holder.nameTextView.setText(item.getNickName());
//        holder.nameTextView.setText(item.getUlUserID());
        if (item.getPeople().contains(".")) {
            holder.avatarImageView.setImageResource(R.drawable.abc_mul_meeting);
        } else {
            holder.avatarImageView.setImageResource(R.drawable.test_ava);
        }
        holder.dateTextView.setText(TimeUtil.time(item.getBegin()));
        int res = R.drawable.abc_canle;
        final CallStatus callStatus = RecentUtil.switchStatus(item.getStatus());
        SpannableStringBuilder callStatusText;
        switch (callStatus) {
            case InAccept:
                res = R.drawable.abc_in;
//                result = TimeUtil.getMinu(getContext().getString(R.string.min), getContext().getString(R.string.sec), item.getEnd() - item.getBegin());
                callStatusText = new SpannableStringBuilder(TimeUtil.getMinu(getContext().getString(R.string.min), getContext().getString(R.string.sec), item.getEnd() - item.getBegin()));
                break;
            case InReject:
                callStatusText = generalRedText(R.string.abc_cancel);
                res = R.drawable.abc_in_not;
                break;
            case InComing:
            case InNoAnswer:
                callStatusText = generalRedText(R.string.abc_in_not);
                res = R.drawable.abc_in_not;
                break;
            case OutAccept:
                res = R.drawable.abc_out;
                callStatusText = new SpannableStringBuilder(TimeUtil.getMinu(getContext().getString(R.string.min), getContext().getString(R.string.sec), item.getEnd() - item.getBegin()));
                break;
            case OutNoAnswer:
            case OutReject:
                res = R.drawable.abc_out_not;
                callStatusText = generalRedText(R.string.abc_abc_out_not);
                break;
            case DialOut:
                res = R.drawable.abc_out;
                callStatusText = new SpannableStringBuilder(getContext().getString(R.string.abc_cancel));
                break;
            default:
                res = R.drawable.abc_canle;
                callStatusText = new SpannableStringBuilder(getContext().getString(R.string.abc_cancel));
                break;
        }
        holder.callStatusImageView.setImageResource(res);
        holder.callResultTextView.setText(callStatusText);
    }

    private SpannableStringBuilder generalRedText(int id) {
        final String string = getContext().getString(id);
        SpannableStringBuilder callStatusText = new SpannableStringBuilder(string);
        ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
        callStatusText.setSpan(span, 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return callStatusText;
    }

    static class ViewHolder {
        @InjectView(R.id.callStatusImageView)
        ImageView callStatusImageView;
        @InjectView(R.id.avatarImageView)
        ImageView avatarImageView;
        @InjectView(R.id.nameTextView)
        TextView nameTextView;
        @InjectView(R.id.callResultTextView)
        TextView callResultTextView;
        @InjectView(R.id.dateTextView)
        TextView dateTextView;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
