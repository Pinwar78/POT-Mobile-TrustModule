package com.example.osbg.pot.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.example.osbg.pot.R;

public class DappActivity extends AppCompatActivity {
    public WebView mWebView;
    public RelativeLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dapp_activity);

        mWebView = findViewById(R.id.webview);
        mContainer = findViewById(R.id.webview_frame);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        String goTo = getIntent().getExtras().getString("qrcodeinfo");

        mWebView.loadUrl(goTo);
    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
