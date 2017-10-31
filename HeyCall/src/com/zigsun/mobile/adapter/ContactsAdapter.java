package com.zigsun.mobile.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zigsun.mobile.R;
import com.zigsun.mobile.module.ContactItem;
import com.zigsun.mobile.module.ContactsLetterItem;
import com.zigsun.ui.contact.SortModel;
import com.zigsun.util.CONSTANTS;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Luo on 2015/6/19.
 */
public class ContactsAdapter extends BaseAdapter {
    private static final String TAG = ContactsAdapter.class.getSimpleName();
    private static final int MAX_CHECK = 5;
    private ContactsListScrollListener listScrollListener;
    private Context context;

    public List<SortModel> getContacts() {
        return contacts;
    }

    protected List<SortModel> contacts;
    private List<SortModel> contactItems;
    protected LayoutInflater inflater;

    private ListView listView;

    public ContactsAdapter(/*ListView listView,*/ List<SortModel> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
        this.inflater= LayoutInflater.from(context);
        contactItems = new ArrayList<>();
        listScrollListener = new ContactsListScrollListener();
        prepareLetterData();

    }

    public ContactsAdapter(ListView listView, CallListener listener, List<SortModel> contacts, Context context) {
        this(contacts, context);
        this.listener = listener;
        this.listView = listView;
        listView.setOnItemClickListener(onItemClickListener);
        listView.setOnScrollListener(listScrollListener);

    }

    @Override
    public boolean isEnabled(int position) {
        return !(contactItems.get(position) instanceof ContactsLetterItem);
    }

    @Override
    public int getItemViewType(int position) {
        return contactItems.get(position) instanceof ContactsLetterItem ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private void prepareLetterData() {
        final int size = contacts.size();
        contactItems.clear();
        if (size <= 0) return;
        String letter = contacts.get(size - 1).getSortLetters();
        for (int i = size - 1; i >= 0; i--) {
            final String sortLetters = contacts.get(i).getSortLetters();
            if (!letter.equals(sortLetters)) {
                contactItems.add(0, new ContactsLetterItem(letter));
                letter = sortLetters;
            }
            contactItems.add(0, contacts.get(i));
        }
        contactItems.add(0, new ContactsLetterItem(letter));
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            final SortModel sortModel = contactItems.get(i);

            String sortStr = sortModel.getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }


    @Override
    public int getCount() {
        return contactItems.size();
    }

    @Override
    public Object getItem(int position) {
        return contactItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final ContactItem sortModel = (ContactItem) contactItems.get(position);
        if (sortModel instanceof ContactsLetterItem) {
            convertView = prepareLetterUI(convertView, (ContactsLetterItem) sortModel);
        } else {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.abc_contacts_list_view_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            initItem(viewHolder, sortModel);
        }
        return convertView;
    }

    private View prepareLetterUI(View convertView, ContactsLetterItem item) {
        LetterViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.abc_contacts_letter, null);
            viewHolder = new LetterViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (LetterViewHolder) convertView.getTag();
        }
        viewHolder.letterText.setText(item.getLetter());
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        if (listScrollListener.isCanUpdateUI()) {
            prepareLetterData();
            super.notifyDataSetChanged();
        } else {
            listScrollListener.requestUpdateUI();
        }
    }

    private void initItem(ViewHolder viewHolder, ContactItem sortModel) {


        final byte ucStatus = sortModel.getStatus();
        int av = ucStatus == CONSTANTS.EXTRA_UC_STATUS_VALUE_OFFLINE ? R.drawable.off_line
                : ucStatus == CONSTANTS.EXTRA_UC_STATUS_VALUE_IN_MEETING ? R.drawable.in_meeting
                : ucStatus == CONSTANTS.EXTRA_UC_STATUS_VALUE_LEAV_MEETING ? R.drawable.abc_leav : R.drawable.on_line;


        viewHolder.avatarImageView.setImageResource(av);
//        viewHolder.nameTextView.setText("测试: "+contacts.get(position).getName() + "- "+contacts.get(position).getUlUserId() + " - "+viewHolder.nameTextView.getContext().getResources().getString(text));
        viewHolder.nameTextView.setText(sortModel.getNickName() /*+ " - " + sortModel.getSortLetters()*/);

        switch (sortModel.getInfoStatus()) {
            case Online:
                viewHolder.setStatusText(R.string.on_line);
                break;
            case InMeeting:
                viewHolder.setStatusText(R.string.in_meeting);
                break;
            default:
                viewHolder.setStatusText("");
        }
//        viewHolder.setStatusText(sortModel.getInfoStatus()+" - ucStatus: "+ucStatus);

        final long ulUserId = sortModel.getUlUserId();
        Boolean checked = this.checked.get((int) ulUserId, false);
//        if(checkCount<MAX_CHECK||checked) {
        viewHolder.checkBox.setOnCheckedChangeListener(new CheckBoxCheckedChange((int) ulUserId));
//        } else{
//            viewHolder.checkBox.setOnCheckedChangeListener(null);
//        }
//        final CompoundButton compoundButton = checked.get(position);
//        if (compoundButton != null) {
//            viewHolder.checkBox.setChecked(compoundButton.isChecked());
//        } else{


        viewHolder.checkBox.setChecked(checked);

//        }


//        viewHolder.checkBox.setChecked(listView.isItemChecked(position));
    }

    protected SparseArray<Boolean> checked = new SparseArray<>();

    public boolean isItemChecked(int position) {
        return checked.get((int) contactItems.get(position).getUlUserId(), false);
    }

    public void setItemCheck(int userId, boolean check) {
        if (checked.get(userId) != check) {
            checked.put(userId, check);
            checkCount = check ? checkCount + 1 : checkCount - 1;
            notifyDataSetChanged();
        }

//        checked.get((int) contacts.get(position).getUlUserId(), false);
    }


    private class CheckBoxCheckedChange implements CompoundButton.OnCheckedChangeListener {
        private int userId;

        public CheckBoxCheckedChange(int userId) {
            this.userId = userId;
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (checkCount >= MAX_CHECK && isChecked) {
                if (!checked.get(userId, false)) {
                    buttonView.setChecked(false);
                    Toast.makeText(context,R.string.abc_out_of_member,Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "out out ...");
                return;
            }
            boolean change = false;
            if ((!isChecked && checked.get(userId, false)) ||
                    (isChecked && !checked.get(userId, false))) {
                checkCount = isChecked ? checkCount + 1 : checkCount - 1;
                Log.d(TAG, "index: " + userId + " checked: " + isChecked + " checkCount: " + checkCount);

                checked.put(userId, isChecked);
                if (listener != null) {
                    listener.onCheckedChanged(userId, isChecked, checkCount > 0);

                }

            }
//            if (!isChecked && checked.get(userId, false)) {
//                checkCount--;
//
//            } else if (isChecked && !checked.get(userId, false)) {
//                checkCount++;
//                change = true;
//            }
//            if (!change) {
//                return;
//            }
//            Log.d(TAG, "userId: " + userId + " checked: " + isChecked + " checkCount: " + checkCount);
//            checked.put(userId, isChecked);
//            if (listener != null) {
//                listener.onCheckedChanged(userId, isChecked, checkCount > 0);
//
//            }
        }
    }

    protected int checkCount = 0;

    public List<SortModel> getCanCallItem() {
        List<SortModel> callItem = new ArrayList<>();

        for (int i = 0; i < checked.size(); i++) {
//            final CompoundButton compoundButton = checked.valueAt(i);
            if (checked.valueAt(i)) {
                callItem.add(getItemByUserId(checked.keyAt(i)));
            }
        }
        return callItem;
    }

    private SortModel getItemByUserId(long userId) {
        for (SortModel m : contactItems) {
            if (m.getUlUserId() == userId) return m;
        }
        return null;
    }

    public void resetChecked() {
        if (checkCount > 0) {
            for (int i = 0; i < checked.size(); i++) {
                if (checked.valueAt(i)) {
                    checked.setValueAt(i, false);
                    listener.onCheckedChanged(checked.keyAt(i), false, --checkCount > 0);
                }
//            final CompoundButton compoundButton = checked.valueAt(i);
//            compoundButton.setChecked(false);
            }
            notifyDataSetChanged();
        }
        Log.d(TAG, "resetChecked() checkCount: " + checkCount);
    }

    private CallListener listener;

    public interface CallListener {
        void onCheckedChanged(int userId, boolean checked, boolean canCall);


        /**
         * 呼叫图层显示或隐藏
         */
        void hideCallLayout();
        void showCallLayout();
//        void canCall(int userId);
//
//        void canNotCall(int userId);

        void onItemClick(AdapterView<?> parent, View view, int position, long id);
    }

    private final AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            if (checkCount == 0) {
//                if (listener != null) listener.onItemClick(parent, view, position, id);
//            } else {
//                final ViewHolder holder = (ViewHolder) view.getTag();
//                holder.checkBox.setChecked(!holder.checkBox.isChecked());
//            }

            final ViewHolder holder = (ViewHolder) view.getTag();
            holder.checkBox.setChecked(!holder.checkBox.isChecked());
        }
    };


    public static class ViewHolder {
        @InjectView(R.id.avatarImageView)
        ImageView avatarImageView;
        @InjectView(R.id.nameTextView)
        TextView nameTextView;
        @InjectView(R.id.checkBox)
        CheckBox checkBox;
        @InjectView(R.id.statusText)
        TextView statusText;
        @InjectView(R.id.rightStatus)
        TextView rightStatus;
        @InjectView(R.id.leftStatus)
        TextView leftStatus;

        public void setStatusText(int text) {
            setStatusText(statusText.getContext().getString(text));
        }

        public void setStatusText(String text) {
            if (TextUtils.isEmpty(text)) {
                rightStatus.setVisibility(View.INVISIBLE);
                leftStatus.setVisibility(View.INVISIBLE);
                statusText.setVisibility(View.INVISIBLE);
                return;
            }
            rightStatus.setVisibility(View.VISIBLE);
            leftStatus.setVisibility(View.VISIBLE);
            statusText.setVisibility(View.VISIBLE);
            statusText.setText(text);
        }

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    static class LetterViewHolder {
        TextView letterText;

        LetterViewHolder(View view) {
            letterText = (TextView) view.findViewById(R.id.letterText);

        }
    }

private CallLayoutListener callLayoutListener;
    public  interface CallLayoutListener{
        void showCallLayout();
        void hideCallLayout();
    }

    private class ContactsListScrollListener implements AbsListView.OnScrollListener {


        public ContactsListScrollListener() {
            canUpdateUI = true;
        }

        /**
         * weather can update ui
         *
         * @return false maybe you need {@linkplain #requestUpdateUI()} to update when scroll state is idle.
         */
        public boolean isCanUpdateUI() {
            return canUpdateUI;
        }

        boolean canUpdateUI;
        boolean needUpdate;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            canUpdateUI = scrollState == SCROLL_STATE_IDLE;
            if (canUpdateUI && needUpdate) {
                needUpdate = false;
                ContactsAdapter.this.notifyDataSetChanged();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.d(TAG, "firstVisibleItem: "+firstVisibleItem+" visibleItemCount: "+visibleItemCount+" totalItemCount: "+totalItemCount);
            if(firstVisibleItem+visibleItemCount>=totalItemCount){
                listener.hideCallLayout();
            } else{
                listener.showCallLayout();
            }
        }

        /**
         * 请求更新数据
         */
        public void requestUpdateUI() {
            needUpdate = true;
        }
    }
}
