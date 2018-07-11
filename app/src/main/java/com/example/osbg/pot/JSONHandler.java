package com.example.osbg.pot;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;

public class JSONHandler {
    private JSONObject jsonResult;
    private JSONArray jsonNames;
    private Context context;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public JSONHandler(JSONObject jsonResult, Context context) {
        this.jsonResult = jsonResult;
        this.context = context;
    }

    public void processJSON() {
        if (jsonResult != null) {
            jsonNames = jsonResult.names();
            Log.d("jsonNames", jsonNames.toString());
            for (int i = 0; i < jsonNames.length(); i++) {
                try {
                    String key = jsonNames.get(i).toString();
                    switch (key) {
                        case "genkeys":
                            break;
                        default:
                            defaultCase();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void defaultCase() {
        Toast.makeText(context, "QR Code not from the POT Subsystem!",
                Toast.LENGTH_LONG).show();
    }
}