package com.example.osbg.pot.services;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.osbg.pot.MainActivity;
import com.example.osbg.pot.services.api.INodeRequestCallback;
import com.example.osbg.pot.services.api.INodeRequestError;
import com.example.osbg.pot.utilities.encryption.AESDecryptor;
import com.example.osbg.pot.utilities.encryption.AESEncryptor;
import com.example.osbg.pot.utilities.encryption.RSADecryptor;
import com.example.osbg.pot.utilities.encryption.RSAEncryptor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

public class NodeRequestService {
    private Context context;
    private NodeSettingsService nodeSettings;
    private RequestQueue queue;
    private String host;
    private String nodePubKey;

    public NodeRequestService(Context context){
        this.context = context;
        this.nodeSettings = new NodeSettingsService(context);
        this.host = nodeSettings.get("host");
        this.nodePubKey = nodeSettings.get("pubkey");
    }

    public NodeRequestService(Context context, String host, String nodePubKey) {
        this.context = context;
        this.nodeSettings = new NodeSettingsService(context);
        this.host = host;
        this.nodePubKey = nodePubKey;
    }

    private JSONObject encryptData(String dataToEncrypt) throws NoSuchAlgorithmException {
        JSONObject encryptedData = new JSONObject();
        String encryptedText = "";
        String AESKey = "";
        String IV = "";
        String myPubKey = "";
        String encryptedAESKey = "";

        //Encrypt Data with AES-Key
        try {
            AESEncryptor aes = new AESEncryptor();
            encryptedText = aes.encrypt(dataToEncrypt);
            Log.d("EncryptedWithAES", encryptedText);
            AESKey = Base64.encodeToString(aes.getKeyValue(), Base64.DEFAULT);
            IV = Base64.encodeToString(aes.getIV(), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Encrypt AES-Key with NodePubKey
        try {
            RSAEncryptor rsaEncryptor = new RSAEncryptor(AESKey, nodePubKey);
            encryptedAESKey = rsaEncryptor.encryptData();
        } catch (NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
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
                myPubKey = new String(Base64.encode(publicKeyBytes, 0));
            }

            encryptedData.put("iv", IV);
            encryptedData.put("aeskey", encryptedAESKey);
            encryptedData.put("data", encryptedText);
            encryptedData.put("pubkey", myPubKey);


        } catch (NoSuchAlgorithmException | JSONException | CertificateException | KeyStoreException | IOException e) {
            e.printStackTrace();
        }
        Log.d("encryptedData", encryptedData.toString());

        return encryptedData;
    }

    public void sendDataToNode(String host, final String endpoint, final int requestMethod, String dataToEncrypt, final INodeRequestCallback callback, final INodeRequestError errorCallback) throws Exception {
        if (host.equals("")){
            Toast.makeText(context, "Please connect to a PoT node.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject encryptedDataJson = encryptData(dataToEncrypt);

        if(queue == null) {
            queue = Volley.newRequestQueue(context);
        }

        String url = "http://"+host+":9090"+endpoint;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(requestMethod, url, encryptedDataJson, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("volleydataresponse", response.toString());
                try {
                    RSADecryptor rsaDecryptor = new RSADecryptor();
                    AESDecryptor aesDecryptor = new AESDecryptor(response.getString("iv"), rsaDecryptor.decryptData(response.getString("aeskey")));
                    String decrDataString = aesDecryptor.decryptAESData(response.getString("data"));
                    JSONObject decrData = new JSONObject(decrDataString);
                    callback.onSuccess(decrData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    errorCallback.onError(error);
                    error.printStackTrace();
                    Toast.makeText(context, "Server timeout!", Toast.LENGTH_LONG).show();
                    nodeSettings.clear();
                    Toast.makeText(context, "Please connect to a new PoT node.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    JSONObject response = new JSONObject(new String(error.networkResponse.data));
                    Toast.makeText(context, "Error: " + response.get("error"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders(){
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        jsonObjectRequest.setTag("myjsonobjrequest");
        queue.add(jsonObjectRequest);
        System.gc();
    }

    public void sendDataToNode(String host, final String endpoint, final int requestMethod, String dataToEncrypt, final INodeRequestCallback callback) throws Exception{
        this.sendDataToNode(host, endpoint, requestMethod, dataToEncrypt, callback, new INodeRequestError() {
            @Override
            public void onError(Exception response) {

            }
        });
    }

    public void sendDataToNode(final String endpoint, final int requestMethod, String dataToEncrypt, final INodeRequestCallback callback, final INodeRequestError errorCallback) throws Exception{
        this.sendDataToNode(host, endpoint, requestMethod, dataToEncrypt, callback, errorCallback);
    }

    public void sendDataToNode(final String endpoint, final int requestMethod, String dataToEncrypt, final INodeRequestCallback callback) throws Exception{
        this.sendDataToNode(host, endpoint, requestMethod, dataToEncrypt, callback, new INodeRequestError() {
            @Override
            public void onError(Exception response) {

            }
        });
    }
}