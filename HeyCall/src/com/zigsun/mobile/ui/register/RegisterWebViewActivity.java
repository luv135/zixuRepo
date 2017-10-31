package com.zigsun.mobile.ui.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.zigsun.mobile.R;



import butterknife.ButterKnife;
import butterknife.InjectView;

public class RegisterWebViewActivity extends Activity {

    public static final String URL = "RegisterWebViewActivity.url";
    private static final String TAG = RegisterWebViewActivity.class.getSimpleName();
    @InjectView(R.id.webView)
    WebView webView;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_activity_register_web_view);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        final String url = intent.getStringExtra(URL);
        Log.d(TAG, "url: " + url);
        WebSettings settings = webView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        progressBar.setMax(100);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress > 80) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
                Log.d(TAG, "progress: " + newProgress);
                progressBar.setProgress(newProgress);
            }
        });
        webView.loadUrl(url);
    }

}
