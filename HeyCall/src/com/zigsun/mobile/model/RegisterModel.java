package com.zigsun.mobile.model;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.zigsun.bean.UserInfo;
import com.zigsun.core.ClientSessMgr;
import com.zigsun.mobile.R;
import com.zigsun.mobile.interfaces.IRegister;
import com.zigsun.mobile.module.LifeFragment;
import com.zigsun.mobile.module.RegisterMessage;

import de.greenrobot.event.EventBus;

/**
 * Created by Luo on 2015/6/17.
 */
public class RegisterModel implements IRegister, LifeFragment.Life {

    private static final String TAG_LIFE_FRAGMENT = "TAG_LIFE_FRAGMENT_FOR_RegisterModule";
    private static final String TAG = RegisterModel.class.getSimpleName();
    private static String ip;
    private static String port;
    private final Context context;
    private RegisterListener listener;

    public RegisterModel(FragmentActivity activity) {
        try {
            listener = (RegisterListener) activity;
        } catch (Exception e) {
            throw new ClassCastException("activity must implement RegisterListener");
        }
        context = activity;
        final LifeFragment lifeFragment = new LifeFragment(this);
//        lifeFragment.setLife(this);
        activity.getSupportFragmentManager().beginTransaction().add(lifeFragment, TAG_LIFE_FRAGMENT).commitAllowingStateLoss();
    }

    @Override
    public boolean userNameValid() {
        return true;
    }

    @Override
    public boolean passwordValid() {
        return true;
    }

    /**
     * @param userName
     * @param password 不能为空,不能为""
     */
    @Override
    public void register(String userName, String password) {
        UserInfo user = new UserInfo();
        user.setStrUserName(userName);
        user.setSzDomain(context.getString(R.string.abc_domain));
        user.setStrPassword(password);
        Log.d(TAG, "register()  "+userName+" - "+password);
//        user.setStrTelephone(userName);
        if(TextUtils.isEmpty(ip)||TextUtils.isEmpty(port)) throw new NullPointerException("initService() first - RegisterModel");
        ClientSessMgr.CSMRegisiterUser(ip, Short.parseShort(port), user);
    }

    @Override
    public void sendTelephoneIdentifyingCode(String number) {

    }

    @Override
    public void confirmTelephoneIdentifyingCode(String idCode) {
        listener.registerSuccess(RegisterCode.CONFIRM_OK);
    }

    @Override
    public void initService(String ip, String port) {
        RegisterModel.ip = ip;
        RegisterModel.port = port;
//        ClientSessMgr.CSMConnect(EMeetingApplication.gethMgr(), ip,
//                Short.parseShort(port));
    }

    public void onEvent(RegisterMessage msg) {
        switch (msg.getCode()) {
            case REGISTER_SYSTEM_ALREADY_EXIST:
                listener.registerFailure(RegisterCode.UserNameExist);
                break;
            case REGISTER_SYSTEM_ERROR:
                listener.registerFailure(RegisterCode.UnknownError);
                break;
            case REGISTER_SYSTEM_SUCCESS:
                listener.registerSuccess(RegisterCode.REGISTER_FINISH);
                break;
        }
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }
}
