package com.example.osbg.pot;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * LogInActivity class - Activity that helps the user to prove his/her identity with a password OR
 * a Fingerprint, if the device supports this function !
 */

public class LogInActivity extends AppCompatActivity implements FingerprintSuccess{

    private EditText inputPassword;
    private TextInputLayout inputLayoutPassword;
    private TextView fingerprintInstructions;
    private ImageView fingerprintImage;
    private Button btnLogInActivity;

    private Button btnDeleteKeys;

    private static final String KEY_NAME = "yourKey";
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    private final String PREFERENCES_NAME = "Password"; //file name
    private final String MY_PASSWORD = "mypassword"; //key name
    private SharedPreferences passwordPreferences;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Toolbar logInToolbar = (Toolbar) findViewById(R.id.log_in_toolbar);
        setSupportActionBar(logInToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbarLogInTitle = (TextView) findViewById(R.id.toolbarLogInTitle);
        toolbarLogInTitle.setText("Login to Process of Things");

        // Check if we're running on Android 6.0 (M) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Fingerprint API only available on from Android 6.0 (M)
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            //FingerprintManager fingerprintManager = (FingerprintManager) getApplicationContext().getSystemService(Context.FINGERPRINT_SERVICE);
            if (!fingerprintManager.isHardwareDetected()) {
                fingerprintImage = (ImageView) findViewById(R.id.fingerprintImage);
                fingerprintImage.setBackgroundResource(R.mipmap.fingerprint);

                fingerprintInstructions = (TextView) findViewById(R.id.fingeprintInstructions);
                fingerprintInstructions.setText("This device doesn't support fingerprint authorization. You can use password instead.");
                fingerprintInstructions.setGravity(Gravity.CENTER);

            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                fingerprintImage = (ImageView) findViewById(R.id.fingerprintImage);
                fingerprintImage.setBackgroundResource(R.mipmap.fingerprint);

                fingerprintInstructions = (TextView) findViewById(R.id.fingeprintInstructions);
                fingerprintInstructions.setText("This device supports fingerprint authorization, but the option is not enabled in the device's settings.");
            }

            if (!fingerprintManager.hasEnrolledFingerprints()) {
                // User hasn't enrolled any fingerprints to authenticate with
                fingerprintImage = (ImageView) findViewById(R.id.fingerprintImage);
                fingerprintImage.setBackgroundResource(R.mipmap.fingerprint);

                fingerprintInstructions = (TextView) findViewById(R.id.fingeprintInstructions);
                fingerprintInstructions.setText("This device supports fingerprint authorization, but no fingerprint is configured (check device's security settings).");
                fingerprintInstructions.setGravity(Gravity.CENTER);
                return;
            }

            if(!keyguardManager.isKeyguardSecure()) {
                fingerprintImage = (ImageView) findViewById(R.id.fingerprintImage);
                fingerprintImage.setBackgroundResource(R.mipmap.fingerprint);

                fingerprintInstructions = (TextView) findViewById(R.id.fingeprintInstructions);
                fingerprintInstructions.setText("This device supports fingerprint authorization. Please enable lockscreen security in your device's Settings first.");
                fingerprintInstructions.setGravity(Gravity.CENTER);
            }
            else {
                try {
                    fingerprintImage = (ImageView) findViewById(R.id.fingerprintImage);
                    fingerprintImage.setBackgroundResource(R.mipmap.fingerprint);

                    fingerprintInstructions = (TextView) findViewById(R.id.fingeprintInstructions);
                    fingerprintInstructions.setText(R.string.fingerprint_instructions);
                    fingerprintInstructions.setGravity(Gravity.CENTER);
                    generateKey();
                } catch (FingerprintException e) {
                    e.printStackTrace();
                }

                if (initCipher()) {
                    cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerprintHandler helper = new FingerprintHandler(this);
                    helper.startAuth(fingerprintManager, cryptoObject);
                }
            }
        }


        //inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);

        //inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);

        btnLogInActivity = (Button) findViewById(R.id.btnLogInActivity);
        btnDeleteKeys = (Button) findViewById(R.id.btnDeleteKeys);

        //inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

        btnLogInActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

        btnDeleteKeys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteKey(MainActivity.ALIAS);
            }
        });
    }

    @Override
    public void onSuccess() {
        changeLoginBtnSuccess();
        changeLoginStatus();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 300);
    }


    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }

    private void submitForm() {
        if(validatePassword() && isPasswordRight()) {
            onSuccess();
            Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_SHORT).show();
        }
    }

    public void changeLoginBtnSuccess() {
        btnLogInActivity.setEnabled(false);
        btnLogInActivity.setAlpha(.5f);
        btnLogInActivity.setText("SUCCESS !");
        btnLogInActivity.setBackgroundColor(btnLogInActivity.getContext().getResources().getColor(R.color.LightGreen));
    }

    public void changeLoginStatus() {
        sharedPreferences = getApplicationContext().getSharedPreferences(MainActivity.IS_LOGGEDIN, 0);
        editor = sharedPreferences.edit();
        editor.putBoolean(MainActivity.IS_LOGGEDIN, true);
        editor.apply();
    }

    private void requestFocus(View view) {
        if(view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validatePassword() {
        if(inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    private boolean isPasswordRight() {
        HashCalculator hashCalculator = new HashCalculator();
        String inputPasswordHash = hashCalculator.calculateHash(inputPassword.getText().toString());

        passwordPreferences = getApplicationContext().getSharedPreferences(PREFERENCES_NAME, 0);
        String passwordValue = passwordPreferences.getString(MY_PASSWORD, null);

        if(!inputPasswordHash.equals(passwordValue)) {
            inputLayoutPassword.setError(getString(R.string.err_msg_wrong_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    private void generateKey() throws FingerprintException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
                keyStore = KeyStore.getInstance("AndroidKeyStore");

                //Generate the key//
                keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

                //Initialize an empty KeyStore//
                keyStore.load(null);

                //Initialize the KeyGenerator//
                keyGenerator.init(new

                        //Specify the operation(s) this key can be used for//
                        KeyGenParameterSpec.Builder(KEY_NAME,
                        KeyProperties.PURPOSE_ENCRYPT |
                                KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)

                        //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(
                                KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());

                //Generate the key//
                keyGenerator.generateKey();

            } catch (KeyStoreException
                    | NoSuchAlgorithmException
                    | NoSuchProviderException
                    | InvalidAlgorithmParameterException
                    | CertificateException
                    | IOException exc) {
                exc.printStackTrace();
                throw new FingerprintException(exc);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean initCipher() {
            try {
                //Obtain a cipher instance and configure it with the properties required for fingerprint authentication//
                cipher = Cipher.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES + "/"
                                + KeyProperties.BLOCK_MODE_CBC + "/"
                                + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            } catch (NoSuchAlgorithmException |
                    NoSuchPaddingException e) {
                throw new RuntimeException("Failed to get Cipher", e);
            }

            try {
                keyStore.load(null);
                SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                        null);
                cipher.init(Cipher.ENCRYPT_MODE, key);
                //Return true if the cipher has been initialized successfully//
                return true;
            } catch (KeyPermanentlyInvalidatedException e) {

                //Return false if cipher initialization failed//
                return false;
            } catch (KeyStoreException | CertificateException
                    | UnrecoverableKeyException | IOException
                    | NoSuchAlgorithmException | InvalidKeyException e) {
                throw new RuntimeException("Failed to init Cipher", e);
            }
    }

            @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class FingerprintException extends Exception {
        public FingerprintException(Exception e) {
            super(e);
        }
    }

    public void deleteKey(final String alias) {
        AlertDialog alertDialog =new AlertDialog.Builder(this)
                .setTitle("Delete Key")
                .setMessage("Do you want to delete the key \"" + alias + "\" from the keystore?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if(keyStore.containsAlias(MainActivity.ALIAS)) {
                                keyStore.deleteEntry(alias);
                                Toast.makeText(LogInActivity.this, "Keys deleted!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LogInActivity.this, "Public/Private key not found!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (KeyStoreException e) {
                            Toast.makeText(LogInActivity.this,
                                    "Exception " + e.getMessage() + " occured",
                                    Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }

}