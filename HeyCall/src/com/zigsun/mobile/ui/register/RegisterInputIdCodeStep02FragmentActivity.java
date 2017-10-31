package com.zigsun.mobile.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zigsun.luo.projection.utils.Util;
import com.zigsun.mobile.R;
import com.zigsun.mobile.interfaces.IRegister;
import com.zigsun.mobile.model.RegisterModel;
import com.zigsun.mobile.ui.base.CodeEditText;
import com.zigsun.mobile.ui.base.FragmentActivity;
import com.zigsun.util.CONSTANTS;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RegisterInputIdCodeStep02FragmentActivity extends FragmentActivity implements IRegister.RegisterListener {

    @InjectView(R.id.registerNextButton)
    Button registerNextButton;
    @InjectView(R.id.telephoneNumberText)
    TextView telephoneNumberText;
    @InjectView(R.id.idCodeEditText)
    CodeEditText idCodeEditText;
//    @InjectView(R.id.countTextView)
//    TextView countTextView;
    @InjectView(R.id.reSendButton)
    Button reSendButton;
    private RegisterStep02Event event;
    private String countString;
    private MyHandler handler = new MyHandler(this);
    private Reminder countTask;
    private Register02Model model;

    @Override
    public void registerSuccess(IRegister.RegisterCode code) {
        if (code == IRegister.RegisterCode.CONFIRM_OK) {
            final Intent intent = new Intent(RegisterInputIdCodeStep02FragmentActivity.this, RegisterPasswordStep03FragmentActivity.class);
            intent.putExtra(CONSTANTS.EXTRA_TEL_NUMBER, model.number);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void registerFailure(IRegister.RegisterCode code) {
        if (code == IRegister.RegisterCode.CONFIRM_NOT) {
            idCodeEditText.setError(getString(R.string.abc_id_code_confirm_failure));
        }
    }

    @Override
    public void serviceConnected() {

    }

    @Override
    public void serviceDisconnected() {

    }

    private static class MyHandler extends Handler {
        private static final int UPDATE_COUNT = 1;
        private WeakReference<RegisterInputIdCodeStep02FragmentActivity> ref;


        public MyHandler(RegisterInputIdCodeStep02FragmentActivity activity) {
            ref = new WeakReference<RegisterInputIdCodeStep02FragmentActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            RegisterInputIdCodeStep02FragmentActivity activity = ref.get();
            if (activity != null) {
                switch (msg.what) {
                    case UPDATE_COUNT:
                        activity.updateCount(msg.arg1);
                        break;
                    default:
                        super.handleMessage(msg);
                        break;
                }
            }

        }
    }

    private void updateCount(int count) {
        if (count == 0) {
            countTask.timer.cancel();
            countTask = null;
            reSendButton.setEnabled(true);
            reSendButton.setText(R.string.abc_send_code);
//            countTextView.setVisibility(View.INVISIBLE);
        } else {
            reSendButton.setEnabled(false);
//            countTextView.setVisibility(View.VISIBLE);
            reSendButton.setText(String.format(countString, count));
        }
    }

    private class RegisterStep02Event implements View.OnClickListener, TextWatcher {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.reSendButton:
                    model.sendTelephoneIdentifyingCode();
                    countTask = new Reminder(1);
                    break;
                case R.id.registerNextButton:
                    model.confirmTelephoneIdentifyingCode(idCodeEditText.getEditString());
//                    startActivity(new Intent(RegisterInputIdCodeStep02Activity.this, RegisterPasswordStep03Activity.class));
                    break;
            }
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 4) {
                registerNextButton.setEnabled(true);
            } else {
                registerNextButton.setEnabled(false);
            }
//            idCodeEditText.setError(null);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }


    private class Register02Model {
        private RegisterModel registerModule;
        public String number;

        public Register02Model(android.support.v4.app.FragmentActivity activity) {
            registerModule = new RegisterModel(activity);
        }

        public void confirmTelephoneIdentifyingCode(String idCode) {

            registerModule.confirmTelephoneIdentifyingCode(idCode);
        }

        public void sendTelephoneIdentifyingCode() {
            registerModule.sendTelephoneIdentifyingCode(number);
        }

        public String getFormatNumber() {
            StringBuilder str = new StringBuilder();
            str.append(number.substring(0,3)).append("-").append(number.substring(3,7)).append("-")
                    .append(number.substring(7,11));
            return str.toString();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_activity_register_input_id_code02);

        ButterKnife.inject(this);
        event = new RegisterStep02Event();
        model = new Register02Model(this);

        model.number = getIntent().getStringExtra(CONSTANTS.EXTRA_TEL_NUMBER);
        telephoneNumberText.setText(model.getFormatNumber());
        countString = getString(R.string.limit_sixty_sec);
        reSendButton.setText(String.format(countString, Reminder.RemindTask.WAIT_SEC_TIM));
        countTask = new Reminder(1);
        registerNextButton.setEnabled(false);
        Util.setOnClickListener(event, reSendButton,registerNextButton);
        idCodeEditText.addTextChangedListener(event);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(countTask!=null)
        countTask.timer.cancel();
    }

    public class Reminder {
        Timer timer;
        public Reminder(int seconds) {
            timer = new Timer();
            // 在1000秒后,每seconds执行一次
            timer.schedule(new RemindTask(), 1000, seconds * 1000);
        }

        class RemindTask extends TimerTask {
            private static final String TAG = "Tsk";
            public static final int WAIT_SEC_TIM = 60;
            private int count = WAIT_SEC_TIM;
            @Override
            public void run() {
                Log.d(TAG, "task run: "+count);
                Message msg = Message.obtain();
                msg.what = MyHandler.UPDATE_COUNT;
                msg.arg1 = --count;
                handler.sendMessage(msg);
                if(count<0){
                    cancel();
                }
            }
        }
    }

}
