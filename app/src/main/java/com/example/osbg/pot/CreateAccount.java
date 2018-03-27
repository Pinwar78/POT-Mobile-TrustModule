package com.example.osbg.pot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
    private SharedPreferences.Editor editor;
    private EditText createPassword;

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

        final Button savePasswordButton = (Button) findViewById(R.id.btnSavePassword);
        savePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    passwordPreferences = getApplicationContext().getSharedPreferences(PREFERENCES_NAME, 0);
                    String passwordValue = passwordPreferences.getString(MY_PASSWORD, null);

                    //check if the value is null or empty
                    if(passwordValue == null || passwordValue.trim().isEmpty()) {
                        HashCalculator hashCalculator = new HashCalculator();
                        String inputPasswordHash = hashCalculator.calculateHash(createPassword.getText().toString());
                        editor = passwordPreferences.edit();
                        editor.putString(MY_PASSWORD, inputPasswordHash);
                        editor.apply();
                        Toast.makeText(getApplicationContext(), "Your password: " + inputPasswordHash + " saved !", Toast.LENGTH_LONG).show();
                        savePasswordButton.setEnabled(false);
                        savePasswordButton.setAlpha(.5f);
                        savePasswordButton.setText("SAVED !");
                        savePasswordButton.setBackgroundColor(getResources().getColor(R.color.LightGreen));
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
}
