package com.zigsun.mobile.ui.personal.information;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zigsun.bean.UserInfo;
import com.zigsun.core.ClientSessMgr;
import com.zigsun.luo.projection.utils.Util;
import com.zigsun.mobile.R;
import com.zigsun.mobile.model.ContactsModel;
import com.zigsun.mobile.observers.RecentObserver;
import com.zigsun.mobile.ui.base.Activity;
import com.zigsun.mobile.ui.base.CircleImageView;
import com.zigsun.util.CONSTANTS;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ContactsPersonalInformationActivity extends Activity {

    private static final String TAG = ContactsPersonalInformationActivity.class.getSimpleName();
    @InjectView(R.id.deleteButton)
    Button deleteButton;
    @InjectView(R.id.userNameCText)
    TextView userNameCText;
    @InjectView(R.id.qrLayout)
    RelativeLayout qrLayout;
    @InjectView(R.id.emailTextP)
    TextView emailTextP;
    @InjectView(R.id.teleTextP)
    TextView teleTextP;
    @InjectView(R.id.departTextP)
    TextView departTextP;
    @InjectView(R.id.head_arrow)
    ImageView headArrow;
    @InjectView(R.id.avaterCImageView)
    CircleImageView avaterCImageView;
    @InjectView(R.id.head_arrow2)
    ImageView headArrow2;
    @InjectView(R.id.head_arrow3)
    ImageView headArrow3;
    @InjectView(R.id.head_arrow4)
    ImageView headArrow4;
    @InjectView(R.id.head_arrow6)
    ImageView headArrow6;
    @InjectView(R.id.companyTextP)
    TextView companyTextP;
    @InjectView(R.id.head_arrow5)
    ImageView headArrow5;
    private ContactPersonEvent event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_activity_contacts_personal_infomation);
        ButterKnife.inject(this);
        event = new ContactPersonEvent();
        model = new ContatPersonModel();
        model.user = (UserInfo) getIntent().getSerializableExtra(CONSTANTS.USERINFO);

        Log.d(TAG, model.user.toString());
        Util.setOnClickListener(event, qrLayout, deleteButton);
        prepareUIDate();

    }

    private void prepareUIDate() {
        userNameCText.setText(model.user.getStrUserName());
        emailTextP.setText(model.user.getStrEmail());
        teleTextP.setText(model.user.getStrTelephone());
        departTextP.setText(model.user.getSzDepart());
        companyTextP.setText("测试:ulUserID"+model.user.getUlUserID()+" ucStatus: "+model.user.getUcStatus());
        companyTextP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ContactsModel().updateUserStatus(model.user.getUlUserID(), (byte) 1);

            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_contacts_personal_infomation, menu);
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
//        if (id == R.id.abc_action_add_contact) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private ContatPersonModel model;

    private class ContatPersonModel {
        private UserInfo user;

        public void delete() {
            new ContactsModel().delete(user.getUlUserID());
            ClientSessMgr.CSMDeleteFriend(user.getUlUserID());
            finish();
        }
    }

    private class ContactPersonEvent implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.deleteButton:
                    model.delete();
                    break;
                case R.id.qrLayout:
                    final Intent intent = new Intent(getApplication(), QRDisplayActivity.class);
                    intent.putExtra(QRDisplayActivity.QRSTRING, model.user.getStrUserName());
                    startActivity(intent);
                    break;
            }
        }
    }
}
