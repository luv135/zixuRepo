package com.zigsun.mobile.ui.meeting;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zigsun.EMeetingApplication;
import com.zigsun.bean.UserInfo;
import com.zigsun.mobile.R;
import com.zigsun.mobile.adapter.ContactsAdapter;
import com.zigsun.mobile.adapter.InvitedAdapter;
import com.zigsun.mobile.model.ContactsModel;
import com.zigsun.mobile.ui.base.Activity;
import com.zigsun.mobile.ui.base.CircleImageView;
import com.zigsun.mobile.ui.base.SelectEditText;
import com.zigsun.mobile.ui.base.TextWatcher;
import com.zigsun.mobile.ui.friend.SearchFriendActivity;
import com.zigsun.mobile.utils.Utils;
import com.zigsun.ui.contact.SortModel;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class InvitedJoinMeetingActivity extends Activity implements SelectEditText.ChangeListener {


    private static final String TAG = InvitedJoinMeetingActivity.class.getSimpleName();

    @InjectView(R.id.contactsListView)
    ListView contactsListView;
    @InjectView(R.id.testButton)
    Button testButton;
    @InjectView(R.id.searchSelectEditText)
    SelectEditText searchSelectEditText;
    private ContactsModel module;
    private InvitedJoinMeetingActivityEvent event;
    private InvitedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_activity_invited_join_meeting);
        ButterKnife.inject(this);
        module = new ContactsModel();
        event = new InvitedJoinMeetingActivityEvent();
        final List<UserInfo> userInfos = EMeetingApplication.getUserInfos();
        final List<SortModel> contactsList = module.getContactsList(null);
        adapter = new InvitedAdapter(contactsListView, event,contactsList, this);
        contactsListView.setAdapter(adapter);
        adapter.setSelected(userInfos);
//        contactsListView.setOnItemClickListener(event);
        searchSelectEditText.setListener(this);
        searchSelectEditText.getEditText().addTextChangedListener(new TextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                module.getContactsList(s.toString());
                adapter.notifyDataSetChanged();
            }
        });
//        testButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setNeedInvitedMember();
//                setResult(RESULT_OK);
//                finish();
//            }
//        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.abc_invited_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.abc_action_ok) {
            searchSelectEditText.getEditText().setText("");
            setNeedInvitedMember();
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void itemRemoved(View view) {
        int userId = (int) view.getTag();
        adapter.setItemCheck(userId,false);

    }

    private SparseArray<View> slected = new SparseArray<>();



    private class InvitedJoinMeetingActivityEvent implements AdapterView.OnItemClickListener, ContactsAdapter.CallListener {

        @Override
        public void onCheckedChanged(int userId, boolean checked, boolean canCall) {
            if (checked) {
//                final TextView textView = new TextView(InvitedJoinMeetingActivity.this);
//                final SortModel item = (SortModel) adapter.getItem(position);
//                textView.setText(item.getUlUserId() + "   ");
//                textView.setTag(position);
                CircleImageView image = new CircleImageView(InvitedJoinMeetingActivity.this);
                image.setImageResource(R.drawable.test_ava);
                image.setTag(userId);
                slected.put(userId, image);
                searchSelectEditText.addItem(image);
            } else {
                final View view1 = slected.get(userId);
                if (view1 != null)
                    searchSelectEditText.removeItem(view1);
            }
        }

        @Override
        public void hideCallLayout() {

        }

        @Override
        public void showCallLayout() {

        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            adapter.notifyDataSetChanged();
//            if (adapter.isItemChecked(position)) {
////                final TextView textView = new TextView(InvitedJoinMeetingActivity.this);
//                final SortModel item = (SortModel) adapter.getItem(position);
////                textView.setText(item.getUlUserId() + "   ");
////                textView.setTag(position);
//                ImageView image = new CircleImageView(InvitedJoinMeetingActivity.this);
//                image.setImageResource(R.drawable.test_ava);
//                image.setTag(position);
//                slected.put(position, image);
//                searchSelectEditText.addItem(image);
//            } else {
//                final View view1 = slected.get(position);
//                if (view1 != null)
//                    searchSelectEditText.removeItem(view1);
//            }
        }
    }

    private void setNeedInvitedMember() {
        final List<SortModel> checkedItems = adapter.getCheckedItems();
        EMeetingApplication.getInViteduserInfos().clear();
        for (int i = 0; i < checkedItems.size(); i++) {
            final SortModel sortModel = checkedItems.get(i);
            EMeetingApplication.addInViteduserInfo(Utils.wrap(sortModel));
        }
    }

    private void cancelInvited() {

    }
}
