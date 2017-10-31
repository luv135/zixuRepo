package com.zigsun.mobile.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.zigsun.EMeetingApplication;
import com.zigsun.bean.UserInfo;
import com.zigsun.core.ClientSessMgr;
import com.zigsun.mobile.interfaces.ILogin;
import com.zigsun.mobile.module.LifeFragment;
import com.zigsun.ui.contact.ContactMgr;
import com.zigsun.util.CONSTANTS;
import com.zigsun.util.log.BaseLog;

import java.util.List;

/**
 * Created by LuoWei on 2015/6/17.
 * <pre>
 * 登陆逻辑模块
 * 1. 连接服务器CSMConnect(...)
 *          a. 成功:flag=CONNECT_SERVER_SUC-> 2.
 *          b. 失败:flag=DISCONNECT_SERVER -> END
 * 2. 开始登陆CSMLogin(...)
 *          a. 成功:flag=LOGIN_SUCCESS
 *          b. 失败: 服务器断开flag=DISCONNECT_SERVER
 *                  失败原因flag=SHOW_MSG
 *
 *
 * 还有个邀请:MEETING_INVITE
 *
 *
 * 1. 初始化服务器{@linkplain #initService(String, String)}
 * 2. 登陆{@linkplain #login(String, String, String)}
 *
 * </pre>
 */
public class LoginModel implements ILogin, LifeFragment.Life {
    private static final String TAG_LIFE_FRAGMENT = "TAG_LifeFragment";
    private static final String TAG = LoginModel.class.getSimpleName();
    private LoginBroadCastReceiver receiver;
    private Context context;
    private LoginListener listener;
    private String ip, port, domain;
    private String userName;
    private String password;
    private boolean serviceConnected;
    private boolean waitLogin;


    /**
     * @param activity 需要实现LoginListener接口
     */
    public LoginModel(FragmentActivity activity) {
        try {
            listener = (LoginListener) activity;
        } catch (Exception e) {
            throw new ClassCastException("activity must implement LoginListener");
        }
        context = activity;
        final LifeFragment lifeFragment = new LifeFragment(this);
//        lifeFragment.setLife(this);
        activity.getSupportFragmentManager().beginTransaction().add(lifeFragment, TAG_LIFE_FRAGMENT).commitAllowingStateLoss();
    }


    @Override
    public void initService(String ip, String port) {
        if (ip == null || port == null /*|| domain == null*/)
            throw new NullPointerException("initService(String, String) parameter maybe null");
        this.ip = ip;
        this.port = port;
//        this.domain = domain;
        initService();
//        ClientSessMgr.CSMConnect(EMeetingApplication.gethMgr(), ip,
//                Short.parseShort(port));
    }

    private void initService() {
        Log.d(TAG, "initService() - ip= " + ip + " port= " + port );
        ClientSessMgr.CSMConnect(EMeetingApplication.gethMgr(), ip,
                Short.parseShort(port));
    }

    @Override
    public void login(String userName, String password, String domain) {
        if (userName == null || password == null || domain == null)
            throw new NullPointerException("login(String, String, String) parameter maybe null");
        Log.d(TAG, "login() - user= " + userName + " password= " + password + " service is connected? " + serviceConnected);
        waitLogin = false;
        this.userName = userName;
        this.password = password;
        this.domain = domain;
        if (serviceConnected) {
            login();
        } else {
            waitLogin = true;
            initService();
        }
    }

    private void login() {
        ClientSessMgr.CSMLogin(EMeetingApplication.gethMgr(),
                userName, password, domain,
                CONSTANTS.DEFAULT_VERSION);
    }


    @Override
    public void onCreate() {
        Log.d(TAG, "register login broadcast");
        receiver = new LoginBroadCastReceiver();
        context.registerReceiver(receiver, new IntentFilter(
                CONSTANTS.EMEETING_ACTION_LOGIN));
    }

    @Override
    public void onDestroy() {
        if (receiver != null) {
            Log.d(TAG, "unregisterReceiver login broadcast");

            context.unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    private void loginSuccess() {
        initContact();
        listener.loginSuccess();
    }

    private void serviceDisconnected() {
        serviceConnected = false;
        listener.serviceDisconnected();
    }

    private void serviceConnected() {
        Log.d(TAG, "serviceConnected() - waitLogin= " + waitLogin);
        serviceConnected = true;
        listener.serviceConnected();
        if (waitLogin) {
            login();
            waitLogin = false;
        }

    }

    /**
     * 获取联系人并初始化
     */
    private void initContact() {
        BaseLog.print("LoginActivty.CONSTANTS.LOGIN_SUCCESS initContact() start");
        // 获取联系人
        ClientSessMgr.CSMFetchAllFriend();
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

    /**
     * 底层业务交互广播接收
     */
    public class LoginBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent i) {
            int flag = i.getExtras().getInt("flag");
            BaseLog.print("LoginActivty.LoginBroadCastReceiver.onReceive()  flag="
                    + flag);
            switch (flag) {
                case CONSTANTS.LOGIN_SUCCESS:
                    BaseLog.print("LoginActivty.CONSTANTS.LOGIN_SUCCESS");
                    loginSuccess();
                    break;
                case CONSTANTS.CONNECT_SERVER_SUC:
                    BaseLog.print("LoginActivty.CONSTANTS.CONNECT_SERVER_SUC");
                    serviceConnected();
                    break;
                case CONSTANTS.DISCONNECT_SERVER:
                    BaseLog.print("LoginActivty.CONSTANTS.DISCONNECT_SERVER");
                    serviceDisconnected();
                    break;
                case CONSTANTS.SHOW_MSG:
                    BaseLog.print("LoginActivty.CONSTANTS.SHOW_MSG:");
                    String msg = i.getExtras().getString("msg");
                    listener.loginFailure(FailureCode.UserNameOrPasswordError, msg);

//                    hideDialog();
//                    showAppMsg(i.getExtras().getString("msg"));
//                    UIUtils.ToastShort(context, i.getExtras().getString("msg"));
                    break;
                case CONSTANTS.MEETING_INVITE:
                    BaseLog.print("LoginActivty.CONSTANTS.MEETING_INVITE:");
//                    intent = new Intent(getApplicationContext(),
//                            RecieveInvitedActivity.class);
//                    intent.putExtras(i.getExtras());
//                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    }


}
