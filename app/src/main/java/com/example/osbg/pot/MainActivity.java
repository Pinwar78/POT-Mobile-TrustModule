package com.example.osbg.pot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * MainActivity class when you open the app...
 */

public class MainActivity extends AppCompatActivity {
    private WifiReceiver mWifiScanReceiver;

    public static final String PREFERENCES_NAME = "networks_pref"; //file name
    public static final String CELL_ID = "cellid"; //key name
    public static final String EXCEL_WIFI = "excelwifi";
    public static final String GOOD_WIFI = "goodwifi";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private SharedPreferences passwordPreferences;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        passwordPreferences = getApplicationContext().getSharedPreferences("Password", 0);
        String passwordValue = passwordPreferences.getString("mypassword", null);
        if(passwordValue == null || passwordValue.trim().isEmpty()) {
            Intent intent = new Intent(this, CreateAccount.class);
            this.startActivity(intent);
            }
        }


    @Override
    protected void onResume()
    {
        registerReceiver(mWifiScanReceiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_main);

        Button logInButton = (Button) findViewById(R.id.logInButton);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(intent);
            }
        });

        //get cell id once on app open
        final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        } else {
            try {
                final GsmCellLocation gsmCellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
                int currentCellID = gsmCellLocation.getCid();
                //get SharedPreferences data
                sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES_NAME, 0);
                String cellIDValue = sharedPreferences.getString(CELL_ID, null);

                HashCalculator cellIDHashCheck = new HashCalculator();
                String currentCellHash = cellIDHashCheck.calculateHash(String.valueOf(currentCellID));

                if(!(currentCellHash.trim().equals(cellIDValue))) {
                    editor = sharedPreferences.edit();
                    editor.putString(CELL_ID, currentCellHash);
                    editor.apply();
                    Toast.makeText(this, "Cell location: " + currentCellHash + " saved !", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        try {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String wifiSSID = wifiInfo.getSSID();
            Toast.makeText(this, "Wifi SSID: " + wifiSSID, Toast.LENGTH_LONG).show();
        }catch (Exception e) {
            e.printStackTrace();
        }

        Intent serviceIntent = new Intent(this, LocationService.class);
        startService(serviceIntent);
        Intent serviceIntent2 = new Intent(this, AirplaneModeListenerService.class);
        startService(serviceIntent2);
    }
}