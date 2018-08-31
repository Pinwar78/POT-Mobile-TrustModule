package com.example.osbg.pot.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import com.example.osbg.pot.R;
import java.io.File;

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
        webSettings.setAllowFileAccessFromFileURLs(true); //Maybe you don't need this rule
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView", consoleMessage.message());
                return true;
            }


        });

        File indexFile = (File) getIntent().getExtras().getSerializable("indexFile");

        mWebView.loadUrl(indexFile.toURI().toString());
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
