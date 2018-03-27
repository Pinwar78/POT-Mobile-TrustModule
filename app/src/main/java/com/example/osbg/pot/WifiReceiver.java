package com.example.osbg.pot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

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
    public static ArrayList<String> fair_wifis = new ArrayList<String>();
    public static ArrayList<String> poor_wifis = new ArrayList<String>();
    public static JSONObject myWIFIJSON = new JSONObject();

    public static String excelWifisHash = "";
    public static String goodWifisHash = "";

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
                }
            }
            if(!(good_wifis.isEmpty())) {
                goodWifisHash = hashCalculator.calculateHash(good_wifis.toString());
                String goodWifisValue = sharedPreferences.getString(MainActivity.GOOD_WIFI, null);
                if(!(goodWifisHash.trim().equals(goodWifisValue))) {
                    editor = sharedPreferences.edit();
                    editor.putString(MainActivity.GOOD_WIFI, goodWifisHash);
                    editor.apply();
                }
            }

            JSONObject excellentJSONWifi = new JSONObject();
            JSONObject goodJSONWifi = new JSONObject();
            try {
                excellentJSONWifi.put("hash", excelWifisHash);
                excellentJSONWifi.put("data", String.valueOf(excellent_wifis));
                goodJSONWifi.put("hash", goodWifisHash);
                goodJSONWifi.put("data", String.valueOf(good_wifis));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                myWIFIJSON.put("excellent", excellentJSONWifi);
                myWIFIJSON.put("good", goodJSONWifi);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new_wifiNetworksList.clear();
            excellent_wifis.clear();
            good_wifis.clear();
            fair_wifis.clear();
            poor_wifis.clear();
            /*Collections.sort(fair_wifis);
            Collections.sort(poor_wifis);*/
        }
}
