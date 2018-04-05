package com.example.osbg.pot;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * CellReceiver class that gets the Cell tower's ID and stores it in the SharedPreferences file.
 * cellid is the key, currentCellHash is the value that is being stored.
 */



public class CellReceiver extends LocationService{
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public JSONObject myCellJSON = new JSONObject();
    public static boolean isCellChanged = false;

    public CellReceiver(Context context){
        this.context = context;
    }

    public int getCellID() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //no permissions available...
            return 0;
        } else {
            try {
                GsmCellLocation gsmCellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
                int cellID = gsmCellLocation.getCid();
                return cellID;
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public void saveCellID() {
        int id = getCellID();
        sharedPreferences = context.getApplicationContext().getSharedPreferences(MainActivity.PREFERENCES_NAME, 0);
        String cellIDValue = sharedPreferences.getString(MainActivity.CELL_ID, null);

        HashCalculator cellIDHashCheck = new HashCalculator();
        String currentCellHash = cellIDHashCheck.calculateHash(String.valueOf(id));

        if (!currentCellHash.trim().equals("5feceb66ffc86f38d952786c6d696c79c2dbc239dd4e91b46729d73a27fb57e9") && !currentCellHash.trim().equals("1bad6b8cf97131fceab8543e81f7757195fbb1d36b376ee994ad1cf17699c464")) {

            if (!(currentCellHash.trim().equals(cellIDValue))) {
                editor = sharedPreferences.edit();
                editor.putString(MainActivity.CELL_ID, currentCellHash);
                editor.apply();
                Log.d("onchanged", sharedPreferences.getString(MainActivity.CELL_ID, null));
                /*NotificationHandler notification = new NotificationHandler(context.getApplicationContext());
                notification.sendNotification();*/

                try {
                    myCellJSON.put("hash", String.valueOf(currentCellHash));
                    myCellJSON.put("data", String.valueOf(getCellID()));
                    myCellJSON.put("trigger", "cell");
                    isCellChanged = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    myCellJSON.put("hash", String.valueOf(currentCellHash));
                    myCellJSON.put("data", String.valueOf(getCellID()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
