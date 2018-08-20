package com.example.osbg.pot.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * MessagingPollingService class that keeps running in the background,
 * and checks for messages every few seconds
 */

public class MessagingPollingService extends IntentService {
    private final Handler handler = new Handler();
    public static boolean isRunning;
    public MessagingPollingService() {
        super("MessagingPollingService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //Required method...leave it just so
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (isRunning) return START_STICKY;
        final Timer timer = new Timer();
        TimerTask locationAsyncTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new MessagingPollingAsyncTask(getApplicationContext(), timer).execute();
                        } catch (Exception e) {
                            timer.cancel();
                            timer.purge();
                            MessagingPollingService.isRunning = false;
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        MessagingPollingService.isRunning = true;
        timer.schedule(locationAsyncTask, 0, 5000);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //stops the Messaging service
        handler.removeCallbacksAndMessages(null);
        Log.d("ondestroy", "stopping messaging service");
        super.onDestroy();
    }

}