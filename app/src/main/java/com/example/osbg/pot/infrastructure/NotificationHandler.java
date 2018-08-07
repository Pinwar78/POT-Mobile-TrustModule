package com.example.osbg.pot.infrastructure;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.osbg.pot.MainActivity;
import com.example.osbg.pot.R;
import com.example.osbg.pot.activities.MessageActivity;

/**
 * NotificationHandler class that helps to send STATUS-BAR NOTIFICATIONS
 */

public class NotificationHandler extends android.app.Notification{
    private Context context;
    private SharedPreferences cellIDPreferences;

    private String subject = "";
    private String message = "";

    public NotificationHandler(Context c) {
        this.context = c;
    }

    public void sendNotification(String mySubject, String myMessage) {

        this.subject = mySubject;
        this.message = myMessage;

        cellIDPreferences = context.getSharedPreferences(MainActivity.PREFERENCES_NAME, 0);

        NotificationManager notificationManager = (NotificationManager) this.context.getSystemService(this.context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = "default";
            String description = "default";
            NotificationChannel channel = new NotificationChannel("default", name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel);
        }

        //Here is a block of code that helps to do something when the user click on the notification
        Intent notificationIntent = new Intent(context, MessageActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = TaskStackBuilder.create(context).addNextIntent(notificationIntent).getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.context, "default")
                .setSmallIcon(R.mipmap.ic_chat_black_24dp)
                .setContentTitle(subject)
                .setContentText(message)
                .setOngoing(true)
                .setLights(Color.CYAN, 150, 5000)
                .setVibrate(new long[] {200, 200, 200, 200})
                .setContentIntent(pendingIntent);

        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }
}
