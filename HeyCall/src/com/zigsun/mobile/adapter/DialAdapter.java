package com.zigsun.mobile.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zigsun.bean.UserInfo;
import com.zigsun.mobile.R;
import com.zigsun.mobile.model.ContactsModel;
import com.zigsun.mobile.module.DialingUserInfo;
import com.zigsun.mobile.module.UserInfoStatus;
import com.zigsun.util.CONSTANTS;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Luo on 2015/6/24.
 */
public class DialAdapter extends BaseAdapter<UserInfo> {
    private ContactsModel nickNameHelper;

    public DialAdapter(Context context, List<UserInfo> items) {
        super(context, items);
        nickNameHelper = new ContactsModel();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.dial_grid_item_layout, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DialingUserInfo info = (DialingUserInfo) getItem(position);
        final byte ucStatus = info.getUcStatus();
        UserInfoStatus status = info.getStatus();
        /*
        int text = ucStatus == CONSTANTS.EXTRA_UC_STATUS_VALUE_OFFLINE ? R.string.off_line
                : ucStatus == CONSTANTS.EXTRA_UC_STATUS_VALUE_IN_MEETING ? R.string.in_meeting
                : ucStatus == CONSTANTS.EXTRA_UC_STATUS_VALUE_LEAV_MEETING ? R.string.abc_leav : R.string.on_line;
*/
        int text = 0;
        switch (status) {
            case Online:
                text = R.string.on_line;
                break;
            case OffLine:
                text = R.string.off_line;
                break;
            case InMeeting:
                text = R.string.in_meeting;
                break;
            case LeaveMeeting:
                text = R.string.abc_leav;
                break;
            case Dialing:
                text = R.string.abc_dialing;
                break;
            case Rejected:
                text = R.string.abc_reject;
                break;
            case Accepted:
                text = R.string.abc_accepted;
                break;
        }
        String string = "";
        if (text != 0)
            string = getContext().getResources().getString(text);
        String strNickName = info.getStrNickName();
        if (TextUtils.isEmpty(strNickName)) {
            strNickName = nickNameHelper.qureyName(info.getStrUserName());
            info.setStrNickName(strNickName);
        }
        holder.nameTextView.setText(strNickName +/*+" - "+info.getUlUserID()+*/ "\n" + string);
        int avatar = status == UserInfoStatus.OffLine ? R.drawable.off_line : R.drawable.on_line;
        holder.avatarImageView.setImageResource(avatar);
        return convertView;
    }

    public int checkPeopInMeeting() {
        int sizie = items.size();
        for (UserInfo p : items) {
            if (p.getUcStatus() == CONSTANTS.EXTRA_UC_STATUS_VALUE_LEAV_MEETING) {
                sizie--;
            }
        }
        return sizie;
    }

    static class ViewHolder {
        @InjectView(R.id.avatarImageView)
        ImageView avatarImageView;
        @InjectView(R.id.nameTextView)
        TextView nameTextView;
        @InjectView(R.id.statusImageView)
        ImageView statusImageView;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
