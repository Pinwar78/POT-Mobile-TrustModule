package com.example.osbg.pot.services;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.osbg.pot.MainActivity;

public class NodeSettingsService {
    public static final String SP_NODE_SETTINGS = "node_settings";
    private Context context;
    private SharedPreferences sharedPreferences;

    public NodeSettingsService(Context context){
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(SP_NODE_SETTINGS, 0);
    }

    public void save(String key, String value) {
            sharedPreferences.edit()
                .putString(key, value)
                .commit();
    }

    public void clear(){
        sharedPreferences.edit().clear().commit();
    }

    public String get(String name){
        return sharedPreferences.getString(name, "");
    }
}
