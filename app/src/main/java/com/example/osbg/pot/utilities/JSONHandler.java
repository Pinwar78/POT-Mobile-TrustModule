package com.example.osbg.pot.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONHandler {
    private JSONObject jsonResult;
    private JSONArray jsonNames;
    private Context context;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public JSONHandler(JSONObject jsonResult, Context context) {
        this.jsonResult = jsonResult;
        this.context = context;
    }

    public void processJSON() {
        if (jsonResult != null) {
            jsonNames = jsonResult.names();
            Log.d("jsonNames", jsonNames.toString());
            for (int i = 0; i < jsonNames.length(); i++) {
                try {
                    String key = jsonNames.get(i).toString();
                    switch (key) {
                        case "genkeys":
                            break;
                        default:
                            defaultCase();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void defaultCase() {
        Toast.makeText(context, "QR Code not from the POT Subsystem!",
                Toast.LENGTH_LONG).show();
    }
}