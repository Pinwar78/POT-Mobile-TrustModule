package com.example.osbg.pot.services;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;

import com.android.volley.Request;
import com.example.osbg.pot.MainActivity;
import com.example.osbg.pot.infrastructure.NotificationHandler;
import com.example.osbg.pot.infrastructure.db.MessageRepository;
import com.example.osbg.pot.infrastructure.db.entities.MessageEntity;
import com.example.osbg.pot.domain_models.ReceivedMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessagingPollingAsyncTask extends AsyncTask<Void, Void, Void> {
    private final Handler handler = new Handler();
    private Context context;
    private SharedPreferences sharedPreferences;
    public static ArrayList<ReceivedMessage> notificationsList = new ArrayList<>();
    public MessageRepository messageRepo;
    public MessagingPollingAsyncTask(Context context) {
        this.context = context;
        messageRepo = new MessageRepository((Application) context);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        boolean post = handler.post(new Runnable() {

            @Override
            public void run() {
                // Getting the pubId from SP
                SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.PREFERENCES_NAME, 0);
//                String publicId = sharedPreferences.getString("public_id", "");

               // Putting the pubId into the POST body
                JSONObject messageRequestJson = new JSONObject();
                try {
                    messageRequestJson.put("pubid", "test");
                    String[] senders = {"test"};
                    messageRequestJson.put("senders", new JSONArray(senders));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                // Sending request
                VolleyDataService volleyData = new VolleyDataService(context);
                try {
                    volleyData.sendDataToNode("/device/messages/get", Request.Method.POST, messageRequestJson.toString(), new IVolleyDataCallback(){
                        @Override
                        public void onSuccess(JSONObject response){
                            // On response OK sends notification and adds the message
                            try {
                                NotificationHandler notification = new NotificationHandler(context);
                                JSONArray messages = response.getJSONArray("messages");
                                for (int i = 0; i < messages.length(); i++){
                                    String messageJson = messages.getString(i);
                                    JSONObject message = new JSONObject(messageJson);
//                                    ReceivedMessage newReceivedMessage = new ReceivedMessage(new Contact("Sender", "test", "test", "test"), message, "time");
//                                    notificationsList.add(newReceivedMessage);
                                    messageRepo.addMessage(new MessageEntity(
                                            message.getString("seqno"),
                                            message.getString("message"),
                                            message.getString("sender"),
                                            message.getString("sender"),
                                            message.getString("date"),
                                            "received"
                                            ));
                                    notification.sendNotification("subject", message.getString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        return null;
    }
}