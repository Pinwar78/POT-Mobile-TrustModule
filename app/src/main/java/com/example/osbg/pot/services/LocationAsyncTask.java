package com.example.osbg.pot.services;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.osbg.pot.infrastructure.CellReceiver;
import com.example.osbg.pot.MainActivity;
import com.example.osbg.pot.infrastructure.NotificationHandler;
import com.example.osbg.pot.infrastructure.WifiReceiver;
import com.example.osbg.pot.domain_models.Contact;
import com.example.osbg.pot.domain_models.ReceivedMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LocationAsyncTask extends AsyncTask<Void, Void, Void> {
    private final Handler handler = new Handler();
    private WifiManager mWifiManager;
    private WifiReceiver mWifiScanReceiver;
    private Context context;

    private JSONObject cellJSON = new JSONObject();
    private JSONObject new_locationJSON = new JSONObject();

    private SharedPreferences sharedPreferences;

    public static ArrayList<ReceivedMessage> notificationsList = new ArrayList<>();


    public LocationAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        handler.post(new Runnable() {

            @Override
            public void run() {
                mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                mWifiScanReceiver = new WifiReceiver();
                context.registerReceiver(mWifiScanReceiver, new IntentFilter(
                        WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                mWifiManager.startScan();

                CellReceiver cellReceiver = new CellReceiver(context);
                cellReceiver.saveCellID();
                cellJSON = cellReceiver.myCellJSON;

                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                sharedPreferences = context.getSharedPreferences(MainActivity.PREFERENCES_NAME, 0);

                if(CellReceiver.isCellChanged && WifiReceiver.isWifiChanged) {
                    new_locationJSON = new JSONObject();
                    try {
                        new_locationJSON.put("cell", cellJSON);
                        new_locationJSON.put("wifi", WifiReceiver.myWIFIJSON);
                        new_locationJSON.put("device", (Build.MANUFACTURER + " " + Build.PRODUCT + " " + Build.VERSION.RELEASE + " " + telephonyManager.getNetworkOperatorName()));
                        if(sharedPreferences.contains("uuid")){
                            new_locationJSON.put("uuid", sharedPreferences.getString(MainActivity.UUID, null));
                        }
                        postJSONObject();
                        CellReceiver.isCellChanged = false;
                        WifiReceiver.isWifiChanged = false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                else if(CellReceiver.isCellChanged) {
                    new_locationJSON = new JSONObject();
                    try {
                        new_locationJSON.put("cell", cellJSON);
                        new_locationJSON.put("wifi", WifiReceiver.myWIFIJSON);
                        new_locationJSON.put("device", (Build.MANUFACTURER + " " + Build.PRODUCT + " " + Build.VERSION.RELEASE + " " + telephonyManager.getNetworkOperatorName()));
                        if(sharedPreferences.contains("uuid")){
                            new_locationJSON.put("uuid", sharedPreferences.getString(MainActivity.UUID, null));
                        }
                        postJSONObject();
                        CellReceiver.isCellChanged = false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else if(WifiReceiver.isWifiChanged) {
                    new_locationJSON = new JSONObject();
                    try {
                        new_locationJSON.put("cell", cellJSON);
                        new_locationJSON.put("wifi", WifiReceiver.myWIFIJSON);
                        new_locationJSON.put("device", (Build.MANUFACTURER + " " + Build.PRODUCT + " " + Build.VERSION.RELEASE + " " + telephonyManager.getNetworkOperatorName()));
                        if(sharedPreferences.contains("uuid")){
                            new_locationJSON.put("uuid", sharedPreferences.getString(MainActivity.UUID, null));
                        }
                        postJSONObject();
                        WifiReceiver.isWifiChanged = false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return null;
    }

    private void postJSONObject() {
        RequestQueue queue = Volley.newRequestQueue(context);
        final String url = "http://trusttest.processofthings.io:9070/node/trust";
        Log.d("sentjson", new_locationJSON.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new_locationJSON, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("serverresponse", response.toString());
                NotificationHandler notification = new NotificationHandler(context);
                if (response.has("message")) {
                    if (response.has("notification")) {
                        try {
                            String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
                            String subject = response.getJSONObject("notification").getString("subject");
                            String message = response.getJSONObject("notification").getString("message");

                            ReceivedMessage newReceivedMessage = new ReceivedMessage(new Contact("test", "test", "test", "test"), message, currentTime);
                            notificationsList.add(newReceivedMessage);

                            notification.sendNotification(subject, message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
        jsonObjectRequest.setTag("myjsonobjrequest");
        queue.add(jsonObjectRequest);
    }
}