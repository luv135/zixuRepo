package com.zigsun.mobile.ui.personal.information;

import android.database.ContentObservable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zigsun.bean.UserInfo;
import com.zigsun.core.ClientSessMgr;
import com.zigsun.mobile.R;
import com.zigsun.mobile.module.ResultItem;
import com.zigsun.mobile.observers.RecentObserver;
import com.zigsun.mobile.ui.base.Activity;
import com.zigsun.ui.contact.ContactMgr;
import com.zigsun.util.CONSTANTS;
import com.zigsun.util.log.BaseLog;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PerSonInfoMatinActivity extends Activity implements View.OnClickListener {

    @InjectView(R.id.userNameText)
    TextView userNameText;
    @InjectView(R.id.addButton)
    Button addButton;
    private ResultItem user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_activity_per_son_info_matin);
        ButterKnife.inject(this);
        user = (ResultItem) getIntent().getParcelableExtra(CONSTANTS.EXTRA_PERONS_INFO);
        userNameText.setText(user.getStrUserName());

        if(user.isExist())addButton.setVisibility(View.GONE);
        addButton.setOnClickListener(this);


    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addButton:
                Toast.makeText(getApplicationContext(), R.string.abc_add_friend_success, Toast.LENGTH_SHORT).show();
                ClientSessMgr.CSMAddFriend(user.getUlUserID(), "addFriend" + user.getUlUserID());

                setResult(RESULT_OK);
                finish();
                break;
        }
    }



    /**
     * 获取联系人并初始化
     */
    private void initContact() {
        BaseLog.print("LoginActivty.CONSTANTS.LOGIN_SUCCESS initContact() start");
        // 获取联系人
        ClientSessMgr.CSMFetchAllFriend();
        // 获取语音会议分组
        ClientSessMgr.CSMFetchAllGroup(CONSTANTS.GROUP_TYPE_AUDIO);
        // 获取视频会议分组
        ClientSessMgr.CSMFetchAllGroup(CONSTANTS.GROUP_TYPE_VIDEO);
        // 初始化语音会议分组
        if (ContactMgr.getGroups(CONSTANTS.GROUP_TYPE_AUDIO).size() != 5) {
            for (int j = 1; j <= 5; j++) {
                ClientSessMgr.CSMAddGroup("AUDIO" + j + "组",
                        CONSTANTS.GROUP_TYPE_AUDIO);
            }
        }
        // 初始化语音会议分组
        if (ContactMgr.getGroups(CONSTANTS.GROUP_TYPE_VIDEO).size() != 5) {
            for (int j = 1; j <= 5; j++) {
                ClientSessMgr.CSMAddGroup("VEDIIO" + j + "组",
                        CONSTANTS.GROUP_TYPE_VIDEO);
            }
        }
        BaseLog.print("LoginActivty.CONSTANTS.LOGIN_SUCCESS initContact() end");
        // 匹配手机联系人
        List<UserInfo> phoneContacts = ContactMgr.getPhoneContacts();
        BaseLog.print("phoneContacts.size()=" + phoneContacts.size());
        for (UserInfo bean : phoneContacts) {
            String userName = bean.getStrUserName();
            if (userName.startsWith("+86")) {
                userName = userName
                        .substring("+86".length(), userName.length());
            }
            BaseLog.print("getStrUserName()=" + userName);
            ClientSessMgr.CSMMobileNOCheck(userName.replaceAll("\\s*", ""));
        }
    }
}
