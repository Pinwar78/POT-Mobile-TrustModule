package com.example.osbg.pot.infrastructure;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.example.osbg.pot.MainActivity;
import com.example.osbg.pot.utilities.HashCalculator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * WifiReceiver class that receives WIFI networks and sorts them into categories according to their
 * strength.
 */

public class WifiReceiver extends BroadcastReceiver {
    private WifiManager mWifiManager;
    private List<ScanResult> new_wifiNetworksList;
    private int dbm;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ArrayList<String> excellent_wifis = new ArrayList<String>();
    private ArrayList<String> good_wifis = new ArrayList<String>();
    private ArrayList<String> fair_wifis = new ArrayList<String>();
    private ArrayList<String> poor_wifis = new ArrayList<String>();

    private JSONObject excellentJSONWifi = new JSONObject();
    private JSONObject goodJSONWifi = new JSONObject();
    public static JSONObject myWIFIJSON = new JSONObject();

    private String excelWifisHash = "";
    private String goodWifisHash = "";

    public static boolean isWifiChanged = false;

    @Override
    public void onReceive(Context c, Intent intent) {

        try {
            mWifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
            new_wifiNetworksList = mWifiManager.getScanResults();
        } catch (Exception e) {
            e.printStackTrace();
        }

            for (int i = 0; i < new_wifiNetworksList.size(); i++) {
                dbm = new_wifiNetworksList.get(i).level;

                if(dbm >= -50) {
                    excellent_wifis.add(new_wifiNetworksList.get(i).BSSID);
                }
                else if(dbm <= -51 && dbm >= -60) {
                    good_wifis.add(new_wifiNetworksList.get(i).BSSID);
                }
                else if(dbm <= -61 && dbm >= -70) {
                    fair_wifis.add(new_wifiNetworksList.get(i).BSSID);
                }
                else if (dbm <= -71) {
                    poor_wifis.add(new_wifiNetworksList.get(i).BSSID);
                }
            }

            Collections.sort(excellent_wifis);
            Collections.sort(good_wifis);

            HashCalculator hashCalculator = new HashCalculator();
            sharedPreferences = c.getSharedPreferences(MainActivity.PREFERENCES_NAME, 0);

            if(!(excellent_wifis.isEmpty())) {
                excelWifisHash = hashCalculator.calculateHash(excellent_wifis.toString());
                String excelWifisValue = sharedPreferences.getString(MainActivity.EXCEL_WIFI, null);
                if(!(excelWifisHash.trim().equals(excelWifisValue))) {
                    editor = sharedPreferences.edit();
                    editor.putString(MainActivity.EXCEL_WIFI, excelWifisHash);
                    editor.apply();
                    isWifiChanged = true;
                }
            }
            if(!(good_wifis.isEmpty())) {
                goodWifisHash = hashCalculator.calculateHash(good_wifis.toString());
                String goodWifisValue = sharedPreferences.getString(MainActivity.GOOD_WIFI, null);
                if(!(goodWifisHash.trim().equals(goodWifisValue))) {
                    editor = sharedPreferences.edit();
                    editor.putString(MainActivity.GOOD_WIFI, goodWifisHash);
                    editor.apply();
                    isWifiChanged = true;
                }
            }

            try {
                excellentJSONWifi.put("hash", excelWifisHash);
                excellentJSONWifi.put("data", String.valueOf(excellent_wifis));
                goodJSONWifi.put("hash", goodWifisHash);
                goodJSONWifi.put("data", String.valueOf(good_wifis));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            myWifiJSON();
            excellent_wifis.clear();
            good_wifis.clear();
        }

        public void myWifiJSON() {

        myWIFIJSON = new JSONObject();
        if(isWifiChanged) {
            try {
                myWIFIJSON.put("excellent", excellentJSONWifi);
                myWIFIJSON.put("good", goodJSONWifi);
                myWIFIJSON.put("trigger", "wifi");
                isWifiChanged = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }else {
            try {
                myWIFIJSON.put("excellent", excellentJSONWifi);
                myWIFIJSON.put("good", goodJSONWifi);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
        }
}
