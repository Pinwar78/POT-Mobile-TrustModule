package com.example.osbg.pot.services.messaging;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.android.volley.Request;
import com.example.osbg.pot.activities.ContactListActivity;
import com.example.osbg.pot.activities.MessageListActivity;
import com.example.osbg.pot.domain_models.Contact;
import com.example.osbg.pot.domain_models.Message;
import com.example.osbg.pot.infrastructure.NotificationHandler;
import com.example.osbg.pot.infrastructure.db.ContactRepository;
import com.example.osbg.pot.infrastructure.db.IDbCallback;
import com.example.osbg.pot.infrastructure.db.MessageRepository;
import com.example.osbg.pot.infrastructure.db.entities.ContactEntity;
import com.example.osbg.pot.services.api.INodeRequestCallback;
import com.example.osbg.pot.services.api.INodeRequestError;
import com.example.osbg.pot.services.api.NodeRequest;
import com.example.osbg.pot.services.NodeSettingsService;
import com.example.osbg.pot.utilities.encryption.AESDecryptor;
import com.example.osbg.pot.utilities.encryption.RSADecryptor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;

public class MessagingPollingAsyncTask extends AsyncTask<Void, Void, Void> {
    private final Handler handler = new Handler();
    private Context context;
    private MessageRepository messageRepo;
    private ContactRepository contactRepo;
    private MessagingService messagingService;
    private NotificationHandler notificationHandler;
    private Timer timer;

    public MessagingPollingAsyncTask(Context context, Timer timer) {
        if (new NodeSettingsService(context).get("uuid") == null){
            MessagingPollingService.isRunning = false;
            timer.cancel();
            timer.purge();
            return;
        }
        this.context = context;
        messageRepo = new MessageRepository((Application) context);
        contactRepo = new ContactRepository((Application) context);
        messagingService = new MessagingService(context);
        notificationHandler = new NotificationHandler(context);
        this.timer = timer;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        boolean post = handler.post(new Runnable() {

            @Override
            public void run() {
                try {
                    // Putting the pubId into the POST body
                    JSONObject messageRequestJson = new JSONObject();
                    messageRequestJson.put("pubid", messagingService.getPubId());

                    // Sending request
                    NodeRequest nodeRequest = new NodeRequest(context);
                    nodeRequest.sendDataToNode("/device/messages/get", Request.Method.POST, messageRequestJson.toString(), new INodeRequestCallback<JSONObject>(){
                        @Override
                        public void onSuccess(JSONObject response){
                            // On response OK sends notification and adds the messages and the new contacts
                            try {
                                JSONArray messages = response.getJSONArray("messages");
                                addMessages(messages);

                                JSONArray contacts = response.getJSONArray("contacts");
                                addContacts(contacts);

                                JSONArray  contactInfos = response.getJSONArray("contact_info");
                                updateContactInfos(contactInfos);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new INodeRequestError(){
                        @Override
                        public void onError(Exception response){
                            timer.cancel();
                            timer.purge();
                            MessagingPollingService.isRunning = false;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        return null;
    }

    private void addMessages(JSONArray messages) throws JSONException{
        for (int i = 0; i < messages.length(); i++){
            String messageJson = messages.getString(i);
            final JSONObject message = new JSONObject(messageJson);
            contactRepo.getByContactKeyAsync(message.getString("sender"), new IDbCallback<ContactEntity>() {
                @Override
                public void onSuccess(ContactEntity sender) {
                    try{
                        String messageBody = new AESDecryptor(sender.aeskey, sender.aeskey).decryptAESData(message.getString("data"));
                        messagingService.addNewMessage(new Message(message.getString("seqno"), sender.contactkey, messageBody, message.getString("date")), "received");
                        Contact senderContact = new Contact(sender);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("contact", senderContact);
                        notificationHandler.sendNotification("New message!", messageBody, MessageListActivity.class, bundle);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void addContacts(JSONArray contacts) throws JSONException{
        for (int i = 0; i < contacts.length(); i++){
            String contactJson = contacts.getString(i);
            final JSONObject contactInfo = new JSONObject(contactJson);
            try{
                String aesKey = new RSADecryptor().decryptData(contactInfo.getString("aeskey"));
                String contactDataJson = new AESDecryptor(aesKey, aesKey).decryptAESData(contactInfo.getString("data"));
                JSONObject contactData = new JSONObject(contactDataJson);

                Contact newContact = new Contact(
                        contactData.getString("name"),
                        contactData.getString("pubid"),
                        contactData.getString("contactkey"),
                        contactData.getString("senderkey"),
                        aesKey
                );
                messagingService.addNewContact(newContact);

                messagingService.sendContactInfo(newContact);
                notificationHandler.sendNotification("New contact!", contactInfo.getString("sender"), ContactListActivity.class, null);

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void updateContactInfos(JSONArray contactInfos) throws JSONException{
        for (int i = 0; i < contactInfos.length(); i++){
            final String contactInfoJson = contactInfos.getString(i);
            final JSONObject contactInfo = new JSONObject(contactInfoJson);
            contactRepo.getByContactKeyAsync(contactInfo.getString("sender"), new IDbCallback<ContactEntity>() {
                @Override
                public void onSuccess(ContactEntity sender) {
                    try{
                        String contactInfoBody = new AESDecryptor(sender.aeskey, sender.aeskey).decryptAESData(contactInfo.getString("data"));
                        JSONObject newContactInfo = new JSONObject(contactInfoBody);
                        String newName = newContactInfo.getString("name");
                        if (newName != null){
                            sender.name = newName;
                        }
                        messagingService.updateContactInfo(new Contact(sender));
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}