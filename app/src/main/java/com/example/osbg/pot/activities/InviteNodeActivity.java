package com.example.osbg.pot.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.example.osbg.pot.R;
import com.example.osbg.pot.services.api.INodeRequestCallback;
import com.example.osbg.pot.services.api.NodeRequest;

import org.json.JSONObject;

public class InviteNodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_node);

        final ProgressBar progressBar = findViewById(R.id.qr_invite_progressbar);
        final ImageView QRInviteCode = findViewById(R.id.qr_invite);

        Window qrWindow = getWindow();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        qrWindow.setAttributes(layoutParams);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Invite to Process of Things");

        try {
            new NodeRequest(this).sendDataToNode("/device/invite", Request.Method.POST, "", new INodeRequestCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        byte[] decodedString = Base64.decode(response.getString("image").substring(22, response.getString("image").length()), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        QRInviteCode.setImageBitmap(decodedByte);
                        progressBar.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        finish();
        System.gc();
        super.onDestroy();
    }
}