package com.example.osbg.pot;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * LocationService class that keeps running in the background, until the phone's AIRPLANE MODE IS NOT TURNED ON,
 * AIRPLANE MODE is ON = service not working,
 * AIRPLANE MODE is OFF = service running,
 * On location change = send hashes with the new location to server...
 */

public class LocationService extends IntentService {
    private final Handler handler = new Handler();
    private WifiManager mWifiManager;
    private WifiReceiver mWifiScanReceiver;

    private JSONObject old_locationJSON = new JSONObject();
    private JSONObject new_locationJSON = new JSONObject();

    public LocationService() {
        super("LocationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //Required method...leave it just so :)
    }

    //CAN BE ONLY FORCE CLOSED :)
    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        doInback();
        return START_STICKY;
    }

    public void doInback() {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                mWifiScanReceiver = new WifiReceiver();
                registerReceiver(mWifiScanReceiver, new IntentFilter(
                        WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                mWifiManager.startScan();

                CellReceiver cellReceiver = new CellReceiver(getApplicationContext());
                cellReceiver.saveCellID();

                try {
                    new_locationJSON.put("cell", CellReceiver.myCellJSON);
                    new_locationJSON.put("wifi", WifiReceiver.myWIFIJSON);
                    new_locationJSON.put("device", (Build.MANUFACTURER + " " + Build.PRODUCT + " " + Build.VERSION.RELEASE));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("totalJSON", old_locationJSON.toString());
                Log.d("totalJSON", new_locationJSON.toString());

                if(!(old_locationJSON.equals(new_locationJSON))) {
                    old_locationJSON = new_locationJSON;
                    if(old_locationJSON != null) {
                        postJSONObject();
                    }
                }

                doInback();
            }
        }, 5000);

    }

    @Override
    public void onDestroy() {
        //stops the Location service
        handler.removeCallbacksAndMessages(null);
        Log.d("ondestroy", "stopping service");
        super.onDestroy();
    }

    private void postJSONObject() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final String url = "potserver.com";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, old_locationJSON, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("serverresponse", response.toString());
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