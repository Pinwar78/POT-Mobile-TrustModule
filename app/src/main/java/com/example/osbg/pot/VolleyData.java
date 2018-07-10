package com.example.osbg.pot;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class VolleyData {
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public VolleyData(Context context) {
        this.context = context;
    }

    public JSONObject encryptData(String dataToEncrypt) throws NoSuchAlgorithmException {
        JSONObject encryptedData = new JSONObject();
        String encryptedText = "";
        String AESKey = "";
        String IV = "";
        String MyPubKey = "";
        String EncryptedAESKey = "";

        //Encrypt Data with AES-Key
        try {
            AESEncryptor aes = new AESEncryptor("", 16);
            aes.genRandomBytes(16);
            encryptedText = aes.encrypt(dataToEncrypt);
            byte[] array = new byte[16];
            new Random().nextBytes(array);
            String generatedString = new String(array, Charset.forName("UTF-8"));
            Log.d("generatedString", generatedString);
            Log.d("EncryptedWithAES", encryptedText);
            String decdata = aes.decrypt(encryptedText);
            Log.d("DecryptedWithAES", decdata);
            AESKey = Base64.encodeToString(aes.getKeyValue(), Base64.DEFAULT);
            IV = Base64.encodeToString(aes.getIV(), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Encrypt AES-Key with NodePubKey
        try {
            RSAEncryptor rsaEncryptor = new RSAEncryptor(context, AESKey);
            EncryptedAESKey = rsaEncryptor.encryptData();
        } catch (NoSuchPaddingException | InvalidKeyException | BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        try {
            //GetMyPubKey
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null, null);
            if(keyStore.containsAlias(MainActivity.ALIAS)) {
                Certificate cert = keyStore.getCertificate(MainActivity.ALIAS);
                PublicKey getPublicKey = cert.getPublicKey();
                byte[] publicKeyBytes = getPublicKey.getEncoded();
                MyPubKey = new String(Base64.encode(publicKeyBytes, 0));
            }

            encryptedData.put("data", encryptedText);
            encryptedData.put("pubkey", MyPubKey);
            encryptedData.put("iv", IV);
            encryptedData.put("aeskey", EncryptedAESKey);


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("encryptedText: ", encryptedText);
        Log.d("encryptedData", String.valueOf(encryptedData));

        return encryptedData;
    }

    public void sendDataToNode(String dataToEncrypt) throws NoSuchAlgorithmException {
        JSONObject JSONtosend = encryptData(dataToEncrypt);
        RequestQueue queue = Volley.newRequestQueue(context);
        sharedPreferences = context.getSharedPreferences(MainActivity.PREFERENCES_NAME, 0);
        final String sharedPrefNodeIP = sharedPreferences.getString("nodeip", "");
        Log.d("sentjson", JSONtosend.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, sharedPrefNodeIP, JSONtosend, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("serverresponse", response.toString());
                /*if(response.has("uuid")) {
                    try {
                        String uuid = response.getString("uuid");
                        sharedPreferences = getApplicationContext().getSharedPreferences(MainActivity.PREFERENCES_NAME, 0);
                        editor = sharedPreferences.edit();
                        editor.putString(MainActivity.UUID, uuid);
                        editor.apply();
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("errorresponse", "Error: " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        jsonObjectRequest.setTag("myjsonobjrequest");
        queue.add(jsonObjectRequest);
    }
}