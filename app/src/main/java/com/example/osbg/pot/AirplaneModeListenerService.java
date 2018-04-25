package com.example.osbg.pot;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;

/**
 * AirplaneModeListenerService - running always in background, listens for the airplane mode status,
 * if the AirplaneMode is turned on - stop the location service...
 */

public class AirplaneModeListenerService extends IntentService {
    private final Handler handler = new Handler();

    //default constructor...
    public AirplaneModeListenerService() {
        super("AirplaneModeListenerService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //Required method...
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        doInback();
        return START_STICKY;
    }

    private static boolean isAirplaneModeOn(Context context) {
        return Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
    }

    private boolean isLocationServiceRunning(Class<?> LocationService) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        try {
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if(LocationService.getName().equals(service.service.getClassName())) {
                return true;
            }
          }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void doInback() {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                if(String.valueOf(isAirplaneModeOn(getApplicationContext())).equals("false")) {
                    if(isLocationServiceRunning(LocationService.class)) {
                        //if true, do nothing
                    } else {
                        Intent serviceIntentStart;
                        serviceIntentStart = new Intent(getApplicationContext(), LocationService.class);
                        startService(serviceIntentStart);
                    }

                } else {
                    if(!(isLocationServiceRunning(LocationService.class))) {
                        //not running, do nothing...
                    } else {
                        Intent serviceIntentStop;
                        serviceIntentStop = new Intent(getApplicationContext(), LocationService.class);
                        stopService(serviceIntentStop);
                    }
                }

                doInback();
            }
        }, 1000);

    }

}