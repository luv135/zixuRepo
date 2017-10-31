package com.zigsun.mobile.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.zigsun.EMeetingApplication;
import com.zigsun.bean.UserInfo;
import com.zigsun.ui.contact.SortModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luo on 2015/6/24.
 */
public class InvitedAdapter extends ContactsAdapter {

    private static final String TAG = InvitedAdapter.class.getSimpleName();

    private List<UserInfo> selected;

    public InvitedAdapter(ListView listView, List<SortModel> contacts, Context context) {
        super(contacts, context);

    }

    public InvitedAdapter(ListView listView, CallListener listener, List<SortModel> contacts, Context context) {
        super(listView, listener, contacts, context);

    }

    @Override
    public boolean isEnabled(int position) {
        return findPositionUser((SortModel) getItem(position)) == -1;
//        return super.isEnabled(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        final Object tag = view.getTag();
        if(tag instanceof ViewHolder) {
            final ViewHolder holder = (ViewHolder) tag;
                holder.checkBox.setClickable(false);
                if (!isEnabled(position)) {
                    holder.checkBox.setEnabled(false);
                } else {
//            holder.checkBox.setClickable(true);
                    holder.checkBox.setEnabled(true);
                }
        }
//        holder.checkBox.setChecked(listView.isItemChecked(position));
//        Log.d(TAG, "POS:"+position+" is checked: " +holder.checkBox.isChecked());
        return view;
    }

    public void setSelected(List<UserInfo> selected) {
        this.selected = selected;
        for (UserInfo i : selected) {
            checked.put((int) i.getUlUserID(), true);
        }
        checkCount = selected.size();
        notifyDataSetChanged();
    }

    public List<SortModel> getCheckedItems() {
//        List<SortModel> sorts = new ArrayList<>();
//        for (int j = 0; j < contacts.size(); j++) {
//            if (listView.isItemChecked(j)) {
//                sorts.add(contacts.get(j));
//            }
//        }
        return getCanCallItem();
    }

    private int findUserPosition(UserInfo selected) {
        for (int j = 0; j < contacts.size(); j++) {
            if (selected.getUlUserID() == contacts.get(j).getUlUserId()) {
                return j;
            }
        }
        return -1;
    }

    private int findPositionUser(SortModel sortModel) {
        for (int j = 0; j < selected.size(); j++) {
            if (sortModel.getUlUserId() == selected.get(j).getUlUserID()) {
                return j;
            }
        }
        return -1;

    }
}
