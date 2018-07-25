package com.example.osbg.pot;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.support.design.widget.FloatingActionButton;
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

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

import static app.akexorcist.bluetotohspp.library.BluetoothState.EXTRA_DEVICE_ADDRESS;
import static app.akexorcist.bluetotohspp.library.BluetoothState.REQUEST_ENABLE_BT;

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
    public static final String ALIAS = "potkeys";
    public static final String NODE_PUB_KEY = "nodepubkey";
    public static final String HOSTST = "hosts";
    public static final String CONTACT_ID = "contactid";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public FloatingActionButton fab;

    private SharedPreferences passwordPreferences;
    private SharedPreferences loginPrefferences;

    public final BluetoothSPP bt = new BluetoothSPP(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        passwordPreferences = getApplicationContext().getSharedPreferences("Password", 0);
        String passwordValue = passwordPreferences.getString("mypassword", null);
        if (passwordValue == null || passwordValue.trim().isEmpty()) {
            generateKeys();
            Intent intent = new Intent(this, CreateAccount.class);
            this.startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.fab);

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
            logInButton.setOnClickListener(new View.OnClickListener() {
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
                new IntentIntegrator(MainActivity.this).setCaptureActivity(ScannerActivity.class).initiateScan();
                /*final String resultString = "d7bb1B099E74-8A54-11E8-892B-76327CE7F85DMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC/gvSv1/86cYbULBXn7HvKf9qn8IvrV48XIosdgicU6Cv5edacHjOopt4EvjqUVivUa0TQYX1Dt/xcA5+IWQcj6DCSGeIGaynF2uKTr0KWBq9IpR1eV/Dt1S/StJZaX0hqj9ypNSNfts2NUk4DiFXWw+NrsiLEvN8k8/qhtJ74wQIDAQAB10.10.40.174:9090/device/new";
                if (checkIfHEX(resultString)) {
                    JSONObject resultAsJSON = convertHEXtoJSON(resultString);
                    JSONHandler jsonHandler = new JSONHandler(resultAsJSON, getApplicationContext());
                    jsonHandler.processJSON();
                } else {
                    HashCalculator hashCalculator = new HashCalculator();
                    try {
                        if (hashCalculator.calculateMD5(resultString.substring(4, resultString.length())).equals(resultString.substring(0, 4))) {
                            NodeConnector nodeConnector = new NodeConnector(getApplicationContext(), resultString);
                            nodeConnector.saveAllSettings();
                            nodeConnector.connectToNode();
                        } else {
                            showResultDialogue(resultString);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }*/
            }
        });

        //get cell id once on app open
        final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, 1);
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

        /*Intent serviceIntent = new Intent(this, LocationService.class);
        startService(serviceIntent);*/

        Button messagesButton = (Button) findViewById(R.id.messagesButton);
        messagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ContactsActivity.class);
                startActivity(intent);
            }
        });

        /*if(bt.isBluetoothEnabled()){
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        }

        if(!(bt.getServiceState() == STATE_CONNECTED)) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!bt.isBluetoothAvailable()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }

                    if (!bt.isBluetoothEnabled()) {
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
                    }

                    if(bt.isBluetoothAvailable() && bt.isBluetoothEnabled()){
                        bt.setupService();
                        bt.startService(BluetoothState.DEVICE_ANDROID);
                        bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
                        Intent intent = new Intent(MainActivity.this, DeviceList.class);
                        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                    }
                }
            });
        } else {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.LightGreen)));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bt.disconnect();
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                    Toast.makeText(getApplicationContext(), "BT device disconnected!", Toast.LENGTH_SHORT).show();
                }
            });
        }*/

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
                Log.d("BT Received", new String(data));
                final String resultString = new String(data);
                if (checkIfHEX(resultString)) {
                    JSONObject resultAsJSON = convertHEXtoJSON(resultString);
                    JSONHandler jsonHandler = new JSONHandler(resultAsJSON, getApplicationContext());
                    jsonHandler.processJSON();
                } else {
                    HashCalculator hashCalculator = new HashCalculator();
                    try {
                        if (hashCalculator.calculateMD5(resultString.substring(4, resultString.length())).equals(resultString.substring(0, 4))) {
                            NodeConnector nodeConnector = new NodeConnector(getApplicationContext(), resultString);
                            nodeConnector.saveAllSettings();
                            nodeConnector.connectToNode();
                        } else {
                            showResultDialogue(resultString);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext()
                        , "BT Disconnected"
                        , Toast.LENGTH_SHORT).show();
                fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!bt.isBluetoothAvailable()) {
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        }

                        if (!bt.isBluetoothEnabled()) {
                            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
                        }

                        if(bt.isBluetoothAvailable() && bt.isBluetoothEnabled()){
                            bt.setupService();
                            bt.startService(BluetoothState.DEVICE_ANDROID);
                            bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
                            Intent intent = new Intent(MainActivity.this, DeviceList.class);
                            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                        }
                    }
                });
            }

            public void onDeviceConnectionFailed() {
                Toast.makeText(getApplicationContext()
                        , "BT Failed"
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnected(String name, final String address) {
                Toast.makeText(getApplicationContext()
                        , "BT Connected : " + address
                        , Toast.LENGTH_SHORT).show();
                fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.LightGreen)));
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bt.disconnect();
                    }
                });

            }
        });
        super.onStart();
        }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mWifiScanReceiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                try {
                    String mdevice = data.getStringExtra(EXTRA_DEVICE_ADDRESS);
                    bt.connect(mdevice);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
                fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
            }
        }

        //We will get scan results here
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        //check for null
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show();
            } else {
                final String resultString = result.getContents();
                //process result contents
                if(checkIfHEX(resultString)) {
                    JSONObject resultAsJSON = convertHEXtoJSON(resultString);
                    JSONHandler jsonHandler = new JSONHandler(resultAsJSON, getApplicationContext());
                    jsonHandler.processJSON();
                }
                else {
                    HashCalculator hashCalculator = new HashCalculator();
                    resultString.substring(0, 4);
                    try {
                        if (hashCalculator.calculateMD5(resultString.substring(4, resultString.length())).equals(resultString.substring(0, 4))) {
                            NodeConnector nodeConnector = new NodeConnector(getApplicationContext(), resultString);
                            nodeConnector.saveAllSettings();
                            nodeConnector.connectToNode();
                        }
                        else {
                            showResultDialogue(resultString);
                        }
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private boolean checkIfHEX(String s) {
        if (s.matches("^[0-9A-Fa-f]+$") && !(s.matches("\\d+"))) {
            return true;
        } else return false;
    }

    private JSONObject convertHEXtoJSON(String hexString) {
        StringBuilder QRCode = new StringBuilder("");
        for (int i = 0; i < hexString.length(); i += 2) {
            String str = hexString.substring(i, i + 2);
            QRCode.append((char) Integer.parseInt(str, 16));
        }
        try {
            JSONObject resultAsJSON = new JSONObject(QRCode.toString());
            return resultAsJSON;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void generateKeys() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null, null);
            if(!keyStore.containsAlias(MainActivity.ALIAS)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(getApplicationContext())
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

    private void generateContactID() {

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