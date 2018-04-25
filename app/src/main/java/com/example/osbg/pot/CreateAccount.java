package com.example.osbg.pot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * CreateAccount class - Activity, that helps you to create your password/account when you open
 * the app for first time...
 */

public class CreateAccount extends AppCompatActivity {
    private final String PREFERENCES_NAME = "Password"; //file name
    private final String MY_PASSWORD = "mypassword"; //key name
    private SharedPreferences passwordPreferences;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private EditText createPassword;
    private TextInputLayout inputCreatePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Toolbar createAccountToolbar = (Toolbar) findViewById(R.id.create_account_toolbar);
        setSupportActionBar(createAccountToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView createAccountTitle = (TextView) findViewById(R.id.toolbarCreateAccountTitle);
        createAccountTitle.setText("Create New Account");

        createPassword = (EditText) findViewById(R.id.input_create_password);
        inputCreatePassword = (TextInputLayout) findViewById(R.id.input_create_password_layout);

        createPassword.addTextChangedListener(new MyTextWatcher(createPassword));

        final Button savePasswordButton = (Button) findViewById(R.id.btnSavePassword);
        savePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    passwordPreferences = getApplicationContext().getSharedPreferences(PREFERENCES_NAME, 0);

                    if(createPassword.getText().length() > 5 && validatePassword()) {
                        HashCalculator hashCalculator = new HashCalculator();
                        String inputPasswordHash = hashCalculator.calculateHash(createPassword.getText().toString());
                        editor = passwordPreferences.edit();
                        editor.putString(MY_PASSWORD, inputPasswordHash);
                        editor.apply();
                        Toast.makeText(getApplicationContext(), "Your password is successfully saved!", Toast.LENGTH_LONG).show();
                        savePasswordButton.setEnabled(false);
                        savePasswordButton.setAlpha(.5f);
                        savePasswordButton.setText("SAVED !");
                        savePasswordButton.setBackgroundColor(getResources().getColor(R.color.LightGreen));
                        sharedPreferences = getApplicationContext().getSharedPreferences(MainActivity.IS_LOGGEDIN, 0);
                        editor = sharedPreferences.edit();
                        editor.putBoolean(MainActivity.IS_LOGGEDIN, false);
                        editor.apply();

                        onBackPressed();
                    } else {
                        Toast.makeText(getApplicationContext(), "Your password should be at least 6 symbols long!", Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean validatePassword() {
        if(createPassword.getText().toString().trim().isEmpty()) {
            inputCreatePassword.setError(getString(R.string.err_msg_password));
            requestFocus(createPassword);
            return false;
        } else {
            inputCreatePassword.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if(view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
                case R.id.input_create_password:
                    validatePassword();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Activity_Name", "CreateAccount");
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}