package com.zigsun.mobile.ui.login;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zigsun.bean.UserInfo;
import com.zigsun.core.ClientSessMgr;
import com.zigsun.mobile.R;
import com.zigsun.mobile.interfaces.ILogin;
import com.zigsun.mobile.model.LoginModel;
import com.zigsun.mobile.ui.MainActivity;
import com.zigsun.mobile.ui.base.Dialog;
import com.zigsun.mobile.ui.register.RegisterTelNumberStep01FragmentActivity;
import com.zigsun.util.CONSTANTS;
import com.zigsun.util.UIUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends FragmentActivity implements ILogin.LoginListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @InjectView(R.id.registerButton)
    Button registerButton;
    @InjectView(R.id.loginButton)
    Button loginButton;
    @InjectView(R.id.userNameEditText)
    EditText userNameEditText;
    @InjectView(R.id.passwordEditText)
    EditText passwordEditText;
    @InjectView(R.id.userLayout)
    LinearLayout userLayout;
    @InjectView(R.id.passwordLayout)
    LinearLayout passwordLayout;

    private LoginEvent event;
    private ILogin module;
    private android.app.Dialog loadDialog;

    @Override
    public void loginSuccess() {
        Log.d(TAG, "loginSuccess()");
        hideLoginDailog();
//        startActivity(new Intent(this, testActivity.class));
        startActivity(new Intent(this, MainActivity.class));
//        finish();
    }

    @Override
    public void loginFailure(LoginModel.FailureCode code, String msg) {
        Log.d(TAG, "loginFailure()->" + code + " msg= " + msg);
        hideLoginDailog();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void serviceConnected() {
        Log.d(TAG, "serviceConnected()");
    }

    @Override
    public void serviceDisconnected() {
        hideLoginDailog();
        Toast.makeText(this, R.string.abc_connect_services_faile, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "serviceDisconnected()");
    }

    private void hideLoginDailog() {
        if (loadDialog != null && loadDialog.isShowing()) {
            loadDialog.dismiss();
//            loadDialog=null;
        }
    }


    private class LoginEvent implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.registerButton:
                    startActivity(new Intent(LoginActivity.this, RegisterTelNumberStep01FragmentActivity.class));




                    break;
                case R.id.loginButton:


                    attemptLogin();



//
//                    final EditText editText = new EditText(LoginActivity.this);
//                    new Dialog.Builder(LoginActivity.this).setCancelable(false).setTitle("dddddd")
//                            .setContentView(editText)
//                            .setPositiveButton(R.string.abc_ok, new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    final String string = editText.getText().toString();
//
//                                }
//                            }).setNegativeButton(R.string.abc_cancel, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                        }
//                    }).show();
//



                    break;
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_activity_login);
        ButterKnife.inject(this);
        event = new LoginEvent();
        module = new LoginModel(this);


//        loadDialog = new AlertDialog.Builder(this).setView(R.layout.abc_dialog_loading).create();
//        loadDialog = new android.app.Dialog(this,android.R.style.Theme_Translucent);
//        loadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        loadDialog.setContentView(R.layout.abc_dialog_loading);
        loadDialog = new com.zigsun.luo.projection.base.Dialog.Builder(this).setContentView(R.layout.abc_dialog_loading).create();

        setClickListener();

        module.initService(getString(R.string.abc_ip)
                , getString(R.string.abc_port)
        );
        if (!UIUtils.isNetworkActive(getApplicationContext())) {
            setNetWork();
        }
//        module.initService("192.168.1.136", "4888");    //账号sz01 111
//        module.initService("183.63.35.202", "3010");    //wai wagn
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSharedPreferences(CONSTANTS.USERINFO, MODE_PRIVATE);
        SharedPreferences sp = getSharedPreferences(CONSTANTS.USERINFO, MODE_PRIVATE);
        userNameEditText.setText(sp.getString("user", ""));
        passwordEditText.setText(sp.getString("password", ""));
    }

    private void setNetWork() {
        new Dialog.Builder(this).setTitle(getString(R.string.str_net_unavailable)).setMessage(R.string.str_is_set_net)
                .setPositiveButton(R.string.str_yes, new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(
                                Settings.ACTION_SETTINGS);
                        startActivity(intent);
                    }
                }).setNegativeButton(R.string.str_no, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        }).show();
    }

    public void attemptLogin() {
        final String user = userNameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString();
        Log.d(TAG, "attemptLogin() [user: " + user + " password: " + password + "]");
        if (checkString(user, password)) {
            module.login(user, password, getString(R.string.abc_domain));
            loadDialog.show();
        } else {
            if (TextUtils.isEmpty(user)) {
                userLayout.startAnimation(AnimationUtils.loadAnimation(this,R.anim.abc_shake));
                Toast.makeText(this, R.string.abc_user_empty, Toast.LENGTH_SHORT).show();
            } else
            if (TextUtils.isEmpty(password)) {
                passwordLayout.startAnimation(AnimationUtils.loadAnimation(this,R.anim.abc_shake));
                Toast.makeText(this, R.string.abc_password_empty, Toast.LENGTH_SHORT).show();
            }
//            Toast.makeText(this, R.string.abc_user_pass_error, Toast.LENGTH_SHORT).show();
        }
        SharedPreferences sp = getSharedPreferences(CONSTANTS.USERINFO, MODE_PRIVATE);
        final SharedPreferences.Editor edit = sp.edit();
        edit.putString("user", user);
        edit.putString("password", password);
        edit.commit();
    }

    private boolean checkString(String user, String password) {
        return !TextUtils.isEmpty(user)/*&& Utils.check(user)*/ && !TextUtils.isEmpty(password);
    }

    private void setClickListener() {
        registerButton.setOnClickListener(event);
        loginButton.setOnClickListener(event);
    }


}

