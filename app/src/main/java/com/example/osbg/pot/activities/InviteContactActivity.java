package com.example.osbg.pot.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.osbg.pot.R;
import com.example.osbg.pot.services.api.INodeRequestCallback;
import com.example.osbg.pot.ui.messaging.invite_contact.InviteContactViewModel;

import org.json.JSONException;
import org.json.JSONObject;

public class InviteContactActivity extends AppCompatActivity {
    private InviteContactViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_contact);

        final ProgressBar progressBar = findViewById(R.id.qr_invite_contact_progressbar);
        final ImageView QRInviteCode = findViewById(R.id.qr_invite_contact);

        Window qrWindow = getWindow();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        qrWindow.setAttributes(layoutParams);

        Toolbar inviteContactToolbar = findViewById(R.id.invite_contact_toolbar);
        setSupportActionBar(inviteContactToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Invite a new contact");

        viewModel = ViewModelProviders.of(this).get(InviteContactViewModel.class);

        try {
            viewModel.genInviteQrCode(new INodeRequestCallback<JSONObject>(){
                public void onSuccess(JSONObject response) {

                    byte[] decodedString = new byte[0];
                    try {
                        decodedString = Base64.decode(response.getString("image").substring(22, response.getString("image").length()), Base64.DEFAULT);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    QRInviteCode.setImageBitmap(decodedByte);
                    progressBar.setVisibility(View.GONE);
                }
            } );
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