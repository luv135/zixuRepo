package com.zigsun.mobile.ui.friend;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.zigsun.EMeetingApplication;
import com.zigsun.bean.UserInfo;
import com.zigsun.core.ClientSessMgr;
import com.zigsun.db.DbHelper;
import com.zigsun.mobile.R;
import com.zigsun.mobile.adapter.ResultAdapter;
import com.zigsun.mobile.module.ResultItem;
import com.zigsun.mobile.ui.base.Activity;
import com.zigsun.mobile.ui.personal.information.PerSonInfoMatinActivity;
import com.zigsun.util.CONSTANTS;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class FriendResultActivity extends Activity implements AdapterView.OnItemClickListener {

    public static final String USERNAME = "USERNAME";
    private static final String TAG = FriendResultActivity.class.getSimpleName();
    @InjectView(R.id.litView)
    ListView litView;
    @InjectView(R.id.empty_list_view)
    LinearLayout emptyListView;
    @InjectView(R.id.empty_list_not_find)
    LinearLayout emptyListNotFind;
    private UserInfo user;
    private ResultAdapter adapter;
    private int ADDuserFinish = 1;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_activity_friend_result);
        ButterKnife.inject(this);
        adapter = new ResultAdapter(this);
        name = getIntent().getStringExtra(USERNAME);

        litView.setAdapter(adapter);
//        addButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ClientSessMgr.CSMAddFriend(user.getUlUserID(), "addFriend" + user.getUlUserID());
//            }
//        });
        litView.setOnItemClickListener(this);
        litView.setEmptyView(emptyListView);
        EventBus.getDefault().registerSticky(this);
        ClientSessMgr.CSMSearchFriend(CONSTANTS.SEARCH_TYPE_LIKE,name);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
//        EventBus.getDefault().postSticky();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_result, menu);
//        return true;
//    }

    public void onEvent(UserInfo user) {
        Log.d(TAG, "USER NAME: "+user.getStrUserName()+ "  name= "+name+" eq:"+user.getStrUserName().equals(name));
        if (TextUtils.isEmpty(user.getStrUserName()) || !user.getStrUserName().equals(name)) {
            litView.setEmptyView(emptyListNotFind);
            emptyListView.setVisibility(View.INVISIBLE);
            return;
        }
        this.user = user;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ulUserID", user.getUlUserID());
        params.put("ulOwnerID", EMeetingApplication.getUserInfo().getUlUserID());
//        if (new DbHelper<UserInfo>().exists(user, params))
        ResultItem item = new ResultItem(new DbHelper<UserInfo>().exists(user, params));
        item.setUlUserID(user.getUlUserID());
        item.setStrUserName(user.getStrUserName());
        item.setUlOwnerID(EMeetingApplication.getUserInfo().getUlUserID());
        adapter.add(item);
        setResult(RESULT_OK);
        final Intent intent = new Intent(this, PerSonInfoMatinActivity.class);
        intent.putExtra(CONSTANTS.EXTRA_PERONS_INFO, (Parcelable) item);
        startActivityForResult(intent, ADDuserFinish);


    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Intent intent = new Intent(this, PerSonInfoMatinActivity.class);
        intent.putExtra(CONSTANTS.EXTRA_PERONS_INFO, (Parcelable) adapter.getItem(position));
        startActivityForResult(intent, ADDuserFinish);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADDuserFinish/* && resultCode == RESULT_OK*/)
            finish();
        else
            super.onActivityResult(requestCode, resultCode, data);
    }
}
