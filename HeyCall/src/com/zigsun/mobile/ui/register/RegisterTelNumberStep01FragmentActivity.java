package com.zigsun.mobile.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zigsun.mobile.R;
import com.zigsun.mobile.interfaces.IRegister;
import com.zigsun.mobile.model.RegisterModel;
import com.zigsun.mobile.ui.base.FragmentActivity;
import com.zigsun.mobile.utils.Utils;
import com.zigsun.util.CONSTANTS;

import butterknife.ButterKnife;
import butterknife.InjectView;
public class RegisterTelNumberStep01FragmentActivity extends FragmentActivity implements IRegister.RegisterListener {


    private static final String TAG = RegisterTelNumberStep01FragmentActivity.class.getSimpleName();
    @InjectView(R.id.telephoneNumberEditText)
    EditText telephoneNumberEditText;
    @InjectView(R.id.registerNextButton)
    Button registerNextButton;
    @InjectView(R.id.user_agreement)
    TextView userAgreement;

    private RegisterTelNumberStep01Event event;
    private IRegister module;
    private Animation shakeAnimation;

    @Override
    public void registerSuccess(IRegister.RegisterCode code) {
        Log.d(TAG, "registerSuccess()");
//        Toast.makeText(this, "registerSuccess()", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void registerFailure(IRegister.RegisterCode code) {
        Log.d(TAG, "registerFailure()" + code);
    }

    @Override
    public void serviceConnected() {

    }

    @Override
    public void serviceDisconnected() {

    }

    private class RegisterTelNumberStep01Event implements View.OnClickListener, android.text.TextWatcher {

        @Override
        public void onClick(View v) {
            final String telephoneNumber = telephoneNumberEditText.getText().toString().trim();
            Log.d(TAG, "telephoneNumber=" + telephoneNumber);
            if (Utils.check(telephoneNumber)) {
                showConfirmDialog(telephoneNumber);
            } else {
                telephoneNumberEditText.startAnimation(shakeAnimation);
                telephoneNumberEditText.setError(getString(R.string.abc_error_tel_number));
            }
//            module.register(telephoneNumber, "123");
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            telephoneNumberEditText.setError(null);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_activity_register_telnumber_step01);
        ButterKnife.inject(this);
        event = new RegisterTelNumberStep01Event();
        module = new RegisterModel(this);
        registerNextButton.setOnClickListener(event);
        module.initService(getString(R.string.abc_ip)
                , getString(R.string.abc_port)
        );
        initUserAgreementAction();
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.abc_shake);
        telephoneNumberEditText.addTextChangedListener(event);
    }

    private void initUserAgreementAction() {
        userAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence text = userAgreement.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) userAgreement.getText();
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            style.clearSpans(); // should clear old spans
            for (URLSpan url : urls) {
                MyURLSpan myURLSpan = new MyURLSpan(url.getURL());
                style.setSpan(myURLSpan, sp.getSpanStart(url),
                        sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            userAgreement.setText(style);
        }
    }

    private class MyURLSpan extends ClickableSpan {
        private String mUrl;

        MyURLSpan(String url) {
            mUrl = url;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            //下划线
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            final Intent intent = new Intent(RegisterTelNumberStep01FragmentActivity.this, RegisterWebViewActivity.class);
            intent.putExtra(RegisterWebViewActivity.URL, mUrl);
            startActivity(intent);
        }
    }

    private void showConfirmDialog(final String telephoneNumber) {
        String msg = getString(R.string.confirm_telephone_number_message) + telephoneNumber;
        new Dialog.Builder(this).setTitle(R.string.confirm_telephone_number_title).setMessage(msg)
                .setPositiveButton(R.string.confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        module.sendTelephoneIdentifyingCode(telephoneNumber);
                        final Intent intent = new Intent(RegisterTelNumberStep01FragmentActivity.this, RegisterInputIdCodeStep02FragmentActivity.class);
                        intent.putExtra(CONSTANTS.EXTRA_TEL_NUMBER, telephoneNumber);
                        startActivity(intent);
                        finish();

                    }
                }).setNegativeButton(R.string.cancel).show();
    }


}
