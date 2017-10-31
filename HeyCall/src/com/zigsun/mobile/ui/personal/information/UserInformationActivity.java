package com.zigsun.mobile.ui.personal.information;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zigsun.EMeetingApplication;
import com.zigsun.luo.projection.utils.Util;
import com.zigsun.mobile.R;
import com.zigsun.mobile.interfaces.IUserInfoAlter;
import com.zigsun.mobile.model.UserAlterInfoModel;
import com.zigsun.mobile.ui.base.Dialog;
import com.zigsun.mobile.ui.base.FragmentActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserInformationActivity extends FragmentActivity implements IUserInfoAlter.CallBack {

    private static final String TAG = UserInformationActivity.class.getSimpleName();
    @InjectView(R.id.exitCurrentButton)
    Button exitCurrentButton;
    @InjectView(R.id.qrLayout)
    RelativeLayout qrLayout;
    @InjectView(R.id.emailLayout)
    RelativeLayout emailLayout;
//    @InjectView(R.id.telLayout)
//    RelativeLayout telLayout;
    @InjectView(R.id.companyLayout)
    RelativeLayout companyLayout;
    @InjectView(R.id.departLayout)
    RelativeLayout departLayout;
    @InjectView(R.id.emailText)
    TextView emailText;
    @InjectView(R.id.teleText)
    TextView teleText;
    @InjectView(R.id.companyText)
    TextView companyText;
    @InjectView(R.id.departText)
    TextView departText;
    private UserInfoEvent event;

    private UserAlterInfoModel model;


    @Override
    public void alterResult(IUserInfoAlter.CallBackCode code, IUserInfoAlter.Who who, String now) {
        Toast.makeText(getApplicationContext(), "person information alter success"+code, Toast.LENGTH_SHORT).show();
        if (code == IUserInfoAlter.CallBackCode.Success) {
        }
    }

    private class UserInfoEvent implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.qrLayout:
                    final Intent intent = new Intent(UserInformationActivity.this, QRDisplayActivity.class);
                    intent.putExtra(QRDisplayActivity.QRSTRING, EMeetingApplication.getUserInfo().getStrUserName());
                    startActivity(intent);
                    break;
                case R.id.emailLayout:
                case R.id.telLayout:
                case R.id.companyLayout:
                case R.id.departLayout:
                    showAlterDialog(v.getId());
                    break;
                case R.id.exitCurrentButton:
//                    model.alterCompany("222");
                    setResult(RESULT_OK);
                    finish();
                    break;

            }
        }

        private void showAlterDialog(final int id) {
            int title = 0;
            switch (id) {
                case R.id.emailLayout:
                    title = R.string.abc_alter_email;
                    break;
                case R.id.telLayout:
                    title = R.string.abc_alter_tel;
                    break;
                case R.id.companyLayout:
                    title = R.string.abc_alter_company;
                    break;
                case R.id.departLayout:
                    title = R.string.abc_alter_depart;
                    break;

            }
            final EditText editText = new EditText(UserInformationActivity.this);
            new Dialog.Builder(UserInformationActivity.this).setCancelable(false).setTitle(title)
                    .setContentView(editText)
                    .setPositiveButton(R.string.abc_ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String string = editText.getText().toString();
                            switch (id) {
                                case R.id.emailLayout:
                                    model.alterEmail(string);
                                    break;
                                case R.id.telLayout:
                                    model.alterTel(string);
                                    break;
                                case R.id.companyLayout:
                                    model.alterCompany(string);
                                    break;
                                case R.id.departLayout:
                                    model.alterDepart(string);
                                    break;
                            }
                        }
                    }).setNegativeButton(R.string.abc_cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            }).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_activity_user_information);
        ButterKnife.inject(this);
        event = new UserInfoEvent();
        model = new UserAlterInfoModel(this, EMeetingApplication.getUserInfo());

        Log.d(TAG, EMeetingApplication.getUserInfo().toString());
        Util.setOnClickListener(event, qrLayout, exitCurrentButton, emailLayout /*telLayout*/, companyLayout, departLayout);

        prepareUIDate();

    }

    private void prepareUIDate() {
        emailText.setText(EMeetingApplication.getUserInfo().getStrEmail());
        teleText.setText(EMeetingApplication.getUserInfo().getStrTelephone());
        departText.setText(EMeetingApplication.getUserInfo().getSzDepart());
        companyText.setText(EMeetingApplication.getUserInfo().getStrRegion());
    }


//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_user_information, menu);
//        return true;
//    }
//
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
}
