package com.zigsun.mobile.ui.register;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zigsun.mobile.R;
import com.zigsun.mobile.interfaces.IRegister;
import com.zigsun.mobile.model.RegisterModel;
import com.zigsun.mobile.ui.base.FragmentActivity;
import com.zigsun.util.CONSTANTS;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RegisterPasswordStep03FragmentActivity extends FragmentActivity implements IRegister.RegisterListener {

    @InjectView(R.id.telephoneNumberText)
    TextView telephoneNumberText;
    @InjectView(R.id.passwordEdittext)
    EditText passwordEdittext;
    @InjectView(R.id.passwordConfirmEditText)
    EditText passwordConfirmEditText;
    @InjectView(R.id.confirmButton)
    Button confirmButton;
    private RegisterPasswordEvent event;

    @Override
    public void registerSuccess(IRegister.RegisterCode code) {
        Toast.makeText(this, R.string.abc_register_success, Toast.LENGTH_SHORT).show();
        model.saveToLocal();
        finish();
    }

    @Override
    public void registerFailure(IRegister.RegisterCode code) {
        Toast.makeText(this, "失败: " + code, Toast.LENGTH_SHORT).show();
        confirmButton.setText(R.string.abc_register_failure);
    }

    @Override
    public void serviceConnected() {

    }

    @Override
    public void serviceDisconnected() {

    }

    private class RegisterPasswordEvent implements View.OnClickListener, TextWatcher {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.confirmButton:
                    confirmButton.setText(R.string.register_waiting);
                    model.confirmPassword(passwordEdittext.getText().toString(),
                            passwordConfirmEditText.getText().toString().trim());
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            passwordConfirmEditText.setError(null);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private Regist03Model model;

    private class Regist03Model {
        private RegisterModel model;// = new RegisterModel(this);
        public String number;
        private String password;
        public Regist03Model(android.support.v4.app.FragmentActivity activity) {
            this.model = new RegisterModel(activity);
        }

        public void confirmPassword(final String p1, String p2) {
            if (!TextUtils.isEmpty(p1) && !TextUtils.isEmpty(p2) && p1.equals(p2)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        model.register(number, p1);
                    }
                }).start();

            } else {
                passwordConfirmEditText.setError(getString(R.string.abc_error_password));
            }
        }
        public void saveToLocal(){
            SharedPreferences sp = getSharedPreferences(CONSTANTS.USERINFO, MODE_PRIVATE);
            final SharedPreferences.Editor edit = sp.edit();
            edit.putString("user", number);
            edit.putString("password", ""/*password*/);
            edit.commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_activity_register_password_step03);
        ButterKnife.inject(this);
        event = new RegisterPasswordEvent();
        model = new Regist03Model(this);
        model.number = getIntent().getStringExtra(CONSTANTS.EXTRA_TEL_NUMBER);
        telephoneNumberText.setText(model.number);
        confirmButton.setOnClickListener(event);
        passwordConfirmEditText.addTextChangedListener(event);
        passwordEdittext.addTextChangedListener(event);
    }


}
