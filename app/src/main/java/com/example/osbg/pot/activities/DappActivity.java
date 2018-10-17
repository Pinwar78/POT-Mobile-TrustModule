package com.example.osbg.pot.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.osbg.pot.R;
import com.example.osbg.pot.services.NodeSettingsService;
import com.example.osbg.pot.services.dapps.DappWebviewInterface;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class DappActivity extends AppCompatActivity {
    public WebView mWebView;
    public RelativeLayout mContainer;
    public NodeSettingsService nodeSettingsService;
    public FloatingActionButton qrFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dapp);

        mWebView = findViewById(R.id.webview);
        mContainer = findViewById(R.id.webview_frame);
        nodeSettingsService = new NodeSettingsService(this);
        qrFab = findViewById(R.id.qr_fab);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true); //Maybe you don't need this rule
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mWebView.loadUrl("javascript: \n" +
                        "document.querySelector('body').addEventListener('click', function(event) {\n" +
                        "  if (event.target.tagName.toLowerCase() === 'input' && event.target.getAttribute('type') === 'text') {\n" +
                        "    Android.showQrFab();\n" +
                        "\n" +
                        "    event.target.addEventListener(\"focus\", function( event ) {\n" +
                        "      Android.showQrFab();\n" +
                        "    }, true);\n" +
                        "    event.target.addEventListener(\"blur\", function( event ) {\n" +
                        "      Android.hideQrFab();\n" +
                        "    }, true);\n" +
                        "  }\n" +
                        "});");
            }
        });

//        File indexFile = (File) getIntent().getExtras().getSerializable("indexFile");
//        mWebView.loadUrl(indexFile.toURI().toString());

        mWebView.addJavascriptInterface(new DappWebviewInterface(){
            @Override
            @JavascriptInterface
            public void showQrFab(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        qrFab.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            @JavascriptInterface
            public void hideQrFab(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        qrFab.setVisibility(View.GONE);
                    }
                });
            }
        }, "Android");

        mWebView.loadUrl("http://"+nodeSettingsService.get("host")+":9070");

        qrFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(DappActivity.this).setCaptureActivity(ScannerActivity.class).initiateScan();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //We will get scan results here
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        //check for null
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show();
            } else {
                final String resultString = result.getContents();
                char[] szRes = resultString.toCharArray(); // Convert String to Char array

                KeyCharacterMap CharMap;

                CharMap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD);

                KeyEvent[] events = CharMap.getEvents(szRes);

                for (KeyEvent event : events){
                    mWebView.dispatchKeyEvent(event);
                }
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
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
