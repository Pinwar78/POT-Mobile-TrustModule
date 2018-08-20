package com.example.osbg.pot.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;

/**
 * LocationService class that keeps running in the background,
 * On location change = send hashes with the new location to server if AIRPLANE MODE IS NOT TURNED ON...
 */

public class LocationService extends IntentService {
    private final Handler handler = new Handler();
    public LocationService() {
        super("LocationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //Required method...leave it just so :)
    }

    //CAN BE ONLY FORCE CLOSED :)
    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Timer timer = new Timer();
        TimerTask locationAsyncTask = new TimerTask() {
            @Override
            public void run() {
                if (!isAirplaneModeOn(getApplicationContext())) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                new LocationAsyncTask(getApplicationContext()).execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        };
        timer.schedule(locationAsyncTask, 0, 10000);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //stops the Location service
        handler.removeCallbacksAndMessages(null);
        Log.d("ondestroy", "stopping service");
        super.onDestroy();
    }

    private boolean isAirplaneModeOn(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }
}