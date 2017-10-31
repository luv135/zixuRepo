package com.zigsun.mobile.model;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.zigsun.bean.UserInfo;
import com.zigsun.core.ClientSessMgr;
import com.zigsun.mobile.R;
import com.zigsun.mobile.interfaces.IUserInfoAlter;
import com.zigsun.mobile.module.AlterUsInfo;
import com.zigsun.mobile.module.LifeFragment;

import de.greenrobot.event.EventBus;

/**
 * Created by Luo on 2015/7/7.
 */
public class UserAlterInfoModel implements IUserInfoAlter, LifeFragment.Life {

    private static final String TAG = UserAlterInfoModel.class.getSimpleName();
    private final UserInfo userInfo;
    private CallBack callBack;
    private String now;
    private Who who;
    private Context context;

    public UserAlterInfoModel(FragmentActivity activity, UserInfo userInfo) {
        this.userInfo = userInfo;//new UserInfo();
        context = activity;
//
//        this.userInfo.setUlUserID(userInfo.getUlUserID());
//        this.userInfo.setSzDomain(userInfo.getSzDomain());
//        this.userInfo.setStrUserName(userInfo.getStrUserName());
//        this.userInfo.setStrPassword(userInfo.getStrPassword());

        this.callBack = (CallBack) activity;
        final LifeFragment lifeFragment = new LifeFragment(this);
//        lifeFragment.setLife(this);
        activity.getSupportFragmentManager().beginTransaction().add(lifeFragment, "UserAlterInfoModel.TAG_LIFE_FRAGMENT").commitAllowingStateLoss();
    }

    @Override
    public void alterEmail(String string) {
        who = Who.Email;
        now = string;
        userInfo.setStrEmail(string);
        ClientSessMgr.CSMModifyUserInfo(userInfo);
    }

    @Override
    public void alterTel(String string) {
        who = Who.Tel;
        now = string;
    }

    @Override
    public void alterCompany(String string) {

        who = Who.Company;
        now = string;
        Log.d(TAG, userInfo.toString());
        userInfo.setStrRegion(string);
        ClientSessMgr.CSMModifyUserInfo(userInfo);

//        user.setStrEmail();
//        user.setStrUserName("15566667777");
//        user.setSzDomain("www.sz.com");
//        user.setStrPassword("1111");
//
//

    }

    @Override
    public void alterDepart(String string) {
        who = Who.Depart;
        now = string;
        userInfo.setSzDepart(string);
        ClientSessMgr.CSMModifyUserInfo( userInfo);
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(AlterUsInfo i) {
        switch (i.getC()) {
            case MODIFY_SYSTEM_SUCCESS:
                callBack.alterResult(CallBackCode.Success, who, now);
                break;
            default:
                callBack.alterResult(CallBackCode.NetError, null, null);
                break;

        }
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }
}
