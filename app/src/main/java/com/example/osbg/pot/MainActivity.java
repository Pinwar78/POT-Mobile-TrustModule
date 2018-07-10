package com.example.osbg.pot;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.util.Enumeration;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * MainActivity class when you open the app...
 */

public class MainActivity extends AppCompatActivity {
    private WifiReceiver mWifiScanReceiver;

    public static final String PREFERENCES_NAME = "networks_pref"; //file name
    public static final String CELL_ID = "cellid"; //key name
    public static final String EXCEL_WIFI = "excelwifi";
    public static final String GOOD_WIFI = "goodwifi";
    public static final String IS_LOGGEDIN = "login";
    public static final String UUID = "uuid";
    public static final String PRIV_KEY = "privkey";
    public static final String PUB_KEY = "pubkey";
    public static final String ALIAS = "potkeys";
    public static final String NODE_PUB_KEY = "nodepubkey";
    public static final String HOSTST = "hosts";
    public static final String NODE_IP = "nodeip";
    public static int IS_CONNECTED = 0;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private SharedPreferences passwordPreferences;
    private SharedPreferences loginPrefferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        passwordPreferences = getApplicationContext().getSharedPreferences("Password", 0);
        String passwordValue = passwordPreferences.getString("mypassword", null);
        if (passwordValue == null || passwordValue.trim().isEmpty()) {
            Intent intent = new Intent(this, CreateAccount.class);
            this.startActivity(intent);
        }
    }


    @Override
    protected void onResume() {
        registerReceiver(mWifiScanReceiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_main);

        TextView firstLogin = (TextView) findViewById(R.id.firstLogin);

        TextView clickToOpenWebsite = (TextView) findViewById(R.id.pot_site);
        clickToOpenWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://www.processofthings.io/"));
                startActivity(intent);
            }
        });

        try {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                if (extras.getString("Activity_Name").equals("CreateAccount")) {
                    firstLogin.setVisibility(View.VISIBLE);
                    getIntent().removeExtra("Activity_Name");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button logInButton = (Button) findViewById(R.id.logInButton);
        loginPrefferences = getApplicationContext().getSharedPreferences(IS_LOGGEDIN, 0);
        boolean loginStatus = loginPrefferences.getBoolean("login", false);
        if (loginStatus) {
            logInButton.setText("LOG OUT");
            firstLogin.setVisibility(View.GONE);
            logInButton.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
            TextView trustText = (TextView) findViewById(R.id.trustText);
            trustText.setVisibility(View.VISIBLE);
            logInButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    sharedPreferences = getApplicationContext().getSharedPreferences(IS_LOGGEDIN, 0);
                    editor = sharedPreferences.edit();
                    editor.putBoolean(IS_LOGGEDIN, false);
                    editor.apply();
                    onStart();
                }
            });
        } else {
            logInButton.setText("LOG IN");
            logInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                    startActivity(intent);
                }
            });
        }

        Button scanButton = (Button) findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String resultString = "7b2275756964223a22746573746964222c2267656e6b657973223a2274727565222c226e6f64657075626b6579223a224d4947664d413047435371475349623344514542415155414134474e4144434269514b426751432f67765376312f3836635962554c42586e3748764b6639716e3849767256343858496f7364676963553643763565646163486a4f6f70743445766a7155566976556130545159583144742f786341352b495751636a364443534765494761796e4632754b5472304b574271394970523165562f447431532f53744a5a61583068716a3979704e534e667473324e556b344469465857772b4e7273694c45764e386b382f7168744a37347751494441514142222c22686f737473223a5b22687474703a2f2f7777772e6d6f636b792e696f2f76322f356233663061393233303030303036343030616263383361222c2022687474703a2f2f3132372e302e302e313a39303930222c2022687474703a2f2f31302e31302e34302e3133383a39303930225d7d";
                if (resultString.matches("^[0-9A-Fa-f]+$")) {
                    StringBuilder QRCode = new StringBuilder("");
                    //convert Hex to JSON
                    for (int i = 0; i < resultString.length(); i += 2) {
                        String str = resultString.substring(i, i + 2);
                        QRCode.append((char) Integer.parseInt(str, 16));
                    }
                    try {
                        JSONObject resultAsJSON = new JSONObject(QRCode.toString());
                        Log.d("resultAsJSON", QRCode.toString());
                        JSONHandler jsonHandler = new JSONHandler(resultAsJSON, getApplicationContext());
                        jsonHandler.processJSON();
                    } catch (JSONException e) {
                        Log.d("resultAsJSON", "exception");
                        e.printStackTrace();
                    }
                    //new IntentIntegrator(MainActivity.this).setCaptureActivity(ScannerActivity.class).initiateScan();
                }
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

                    if (!(currentCellHash.trim().equals(cellIDValue))) {
                        editor = sharedPreferences.edit();
                        editor.putString(CELL_ID, currentCellHash);
                        editor.apply();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

            Intent serviceIntent = new Intent(this, LocationService.class);
            startService(serviceIntent);
            Intent serviceIntent2 = new Intent(this, AirplaneModeListenerService.class);
            startService(serviceIntent2);

        Button messagesButton = (Button) findViewById(R.id.messagesButton);
        messagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                startActivity(intent);
            }
        });

        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //We will get scan results here
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        //check for null
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show();
            } else {
                //process result contents
                final String resultString = result.getContents();
                if (resultString.matches("^[0-9A-Fa-f]+$")) {
                    StringBuilder QRCode = new StringBuilder("");
                    //convert Hex to JSON
                    for (int i = 0; i < resultString.length(); i += 2) {
                        String str = resultString.substring(i, i + 2);
                        QRCode.append((char) Integer.parseInt(str, 16));
                    }
                    try {
                        JSONObject resultAsJSON = new JSONObject(QRCode.toString());
                        JSONHandler jsonHandler = new JSONHandler(resultAsJSON, getApplicationContext());
                        jsonHandler.processJSON();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    QRCode = null;
                    System.gc();
                }
                else {
                    showResultDialogue(resultString);
                }
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void showResultDialogue(final String result) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Scan Result")
                .setMessage("QR Code result: " + result)
                .setPositiveButton("COPY", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Scan Result", result);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(MainActivity.this, "Result copied to clipboard", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
                .show();
    }
}