package com.example.osbg.pot.infrastructure;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.example.osbg.pot.MainActivity;
import com.example.osbg.pot.services.VolleyDataService;
import com.example.osbg.pot.services.IVolleyDataCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public class NodeConnector {
    private Context context;
    private String nodeData = "";
    private String uuid = "";
    private String nodePubKey = "";
    private String host = "";
    private String pub_id = "";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public NodeConnector(Context context, String nodeData) {
        this.context = context;
        this.nodeData = nodeData;
    }

    private void saveUUID() {
        sharedPreferences = context.getSharedPreferences(MainActivity.PREFERENCES_NAME, 0);
        if(!sharedPreferences.contains("uuid")) {
            uuid = nodeData.substring(4, 40);
            editor = sharedPreferences.edit();
            editor.putString(MainActivity.UUID, uuid);
            editor.apply();
            Log.d("getsavedthings", sharedPreferences.getString("uuid", ""));
        }
    }

    private String getUUID() {
        sharedPreferences = context.getSharedPreferences(MainActivity.PREFERENCES_NAME, 0);
        return sharedPreferences.getString("uuid", "");
    }

    private void saveNodePublicKey() {
            nodePubKey = nodeData.substring(40, 256);
            sharedPreferences = context.getSharedPreferences(MainActivity.PREFERENCES_NAME, 0);
            editor = sharedPreferences.edit();
            editor.putString(MainActivity.NODE_PUB_KEY, nodePubKey);
            editor.apply();
            Log.d("getsavedthings", sharedPreferences.getString("nodepubkey", ""));
    }

    private void saveHost() {
            host = nodeData.substring(256, nodeData.length());
            sharedPreferences = context.getSharedPreferences(MainActivity.PREFERENCES_NAME, 0);
            editor = sharedPreferences.edit();
//            if(!host.contains("http://")) {
//                editor.putString(MainActivity.HOSTST, "http://" + host);
//                editor.apply();
//            } else {
//                editor.putString(MainActivity.HOSTST, host);
//                editor.apply();
//            }
            editor.putString(MainActivity.HOSTST, host);
            editor.apply();
            Log.d("getsavedthings", sharedPreferences.getString("hosts", ""));
    }

    public void saveAllSettings() {
        saveUUID();
        saveNodePublicKey();
        saveHost();
        Toast.makeText(context, "Node settings applied!", Toast.LENGTH_SHORT).show();
    }

    public void connectToNode() {
        try {
            sharedPreferences = context.getSharedPreferences(MainActivity.PREFERENCES_NAME, 0);
            JSONObject myDataToEncrypt = new JSONObject();
            try {
                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null, null);
                if(keyStore.containsAlias(MainActivity.ALIAS)) {
                    Certificate cert = keyStore.getCertificate(MainActivity.ALIAS);
                    PublicKey publicKey = cert.getPublicKey();
                    byte[] publicKeyBytes = publicKey.getEncoded();
                    String pubKey = new String(Base64.encode(publicKeyBytes, 0));
                    myDataToEncrypt.put("pubkey", pubKey);
                    myDataToEncrypt.put("uuid", getUUID());
                }
            } catch (JSONException | NoSuchAlgorithmException | CertificateException | IOException | KeyStoreException e) {
                e.printStackTrace();
            }
                VolleyDataService volleyData = new VolleyDataService(context);
                volleyData.sendDataToNode("/device/new", Request.Method.POST, myDataToEncrypt.toString(), new IVolleyDataCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        if (response.toString().contains("OK")) {
                            Toast.makeText(context, "Connected to node!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
