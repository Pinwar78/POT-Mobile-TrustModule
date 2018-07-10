package com.example.osbg.pot;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import java.util.HashMap;
import java.util.Map;

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
                        case "uuid":
                            saveUUID();
                            break;
                        case "genkeys":
                            if(jsonResult.get("genkeys").equals("true")) {
                                generateKeys();
                            }
                            break;
                        case "nodepubkey":
                            saveNodePublicKey();
                            break;
                        case "hosts":
                            saveHosts();
                            break;
                        default:
                            defaultCase();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }connectToNode();
        }
    }

    private void saveUUID() {
        try {
            String uuid = jsonResult.getString("uuid");
            sharedPreferences = context.getSharedPreferences(MainActivity.PREFERENCES_NAME, 0);
            editor = sharedPreferences.edit();
            editor.putString(MainActivity.UUID, uuid);
            editor.apply();
            Log.d("getsavedthings", sharedPreferences.getString("uuid", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void generateKeys() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null, null);
            if(!keyStore.containsAlias(MainActivity.ALIAS)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(MainActivity.ALIAS)
                        .setSubject(new X500Principal("CN=POT, O=POT Authority"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                generator.initialize(spec);

                KeyPair keyPair = generator.generateKeyPair();
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException | CertificateException | IOException e) {
            e.printStackTrace();
        }
    }

    private void saveNodePublicKey() {
        try {
            String nodepubkey = jsonResult.getString("nodepubkey");
            sharedPreferences = context.getSharedPreferences(MainActivity.PREFERENCES_NAME, 0);
            editor = sharedPreferences.edit();
            editor.putString(MainActivity.NODE_PUB_KEY, nodepubkey);
            editor.apply();
            Log.d("getsavedthings", sharedPreferences.getString("nodepubkey", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveHosts() {
        try {
            JSONArray hosts = jsonResult.getJSONArray("hosts");
            Log.d("gethosts", jsonResult.toString());
            sharedPreferences = context.getSharedPreferences(MainActivity.PREFERENCES_NAME, 0);
            editor = sharedPreferences.edit();
            editor.putString(MainActivity.HOSTST, hosts.toString());
            editor.apply();
            Log.d("getsavedthings", sharedPreferences.getString("hosts", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void connectToNode() {
        try {
            RequestQueue queue = Volley.newRequestQueue(context);
            sharedPreferences = context.getSharedPreferences(MainActivity.PREFERENCES_NAME, 0);
            String sharedPrefHosts = sharedPreferences.getString("hosts", "");
            JSONArray jsonArray = new JSONArray(sharedPrefHosts);
            Gson gson = new Gson();
            String[] connectToArray = gson.fromJson(String.valueOf(jsonArray), String[].class);
            for (int i = 0; i < connectToArray.length; i++) {
                final String connectTo = connectToArray[i];
                Log.d("connectTo", connectToArray.toString());
                final JSONObject jsonObject = new JSONObject().put("ReadyToConnect", "YES");
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        connectTo, jsonObject,
                        new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("thefuckinresponse", response.toString());
                        if (response.toString().contains("OK")) {
                            MainActivity.IS_CONNECTED = 1;
                            sharedPreferences = context.getSharedPreferences(MainActivity.PREFERENCES_NAME, 0);
                            editor = sharedPreferences.edit();
                            editor.putString(MainActivity.NODE_IP, connectTo);
                            editor.apply();
                            Toast.makeText(context, "Connected to the node!",
                                    Toast.LENGTH_LONG).show();
                            JSONObject myDataToEncrypt = new JSONObject();
                            try {
                                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                                keyStore.load(null, null);
                                if(keyStore.containsAlias(MainActivity.ALIAS)) {
                                    Key key = keyStore.getKey(MainActivity.ALIAS, null);
                                    Certificate cert = keyStore.getCertificate(MainActivity.ALIAS);
                                    PublicKey publicKey = cert.getPublicKey();
                                    byte[] publicKeyBytes = publicKey.getEncoded();
                                    String pubKey = new String(Base64.encode(publicKeyBytes, 0));
                                    myDataToEncrypt.put("pubkey", pubKey);
                                    myDataToEncrypt.put("uuid", jsonResult.get("uuid"));
                                    VolleyData volleyData = new VolleyData(context);
                                    volleyData.sendDataToNode(myDataToEncrypt.toString());
                                }
                            } catch (JSONException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
                                e.printStackTrace();
                            } catch (KeyStoreException e) {
                                e.printStackTrace();
                            }
                        }
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
                jsonObjectRequest.setTag("trytoconnectrequest");
                queue.add(jsonObjectRequest);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void defaultCase() {
        Toast.makeText(context, "QR Code not from the POT Subsystem!",
                Toast.LENGTH_LONG).show();
    }
}