package com.example.osbg.pot.services.messaging;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.example.osbg.pot.domain_models.Contact;
import com.example.osbg.pot.domain_models.Message;
import com.example.osbg.pot.infrastructure.db.ContactRepository;
import com.example.osbg.pot.infrastructure.db.IDbCallback;
import com.example.osbg.pot.infrastructure.db.MessageRepository;
import com.example.osbg.pot.infrastructure.db.entities.ContactEntity;
import com.example.osbg.pot.infrastructure.db.entities.MessageEntity;
import com.example.osbg.pot.services.api.INodeRequestCallback;
import com.example.osbg.pot.services.KeyGenerator;
import com.example.osbg.pot.services.api.NodeRequest;
import com.example.osbg.pot.utilities.encryption.AESEncryptor;
import com.example.osbg.pot.utilities.encryption.RSAEncryptor;

import org.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MessagingService {
    public static final String MESSAGING_PREFERENCES = "messaging";
    public static final String MESSAGING_API_PREFIX = "/device/messages";
    public static final String MESSAGING_SEND_ENDPOINT = "/send";
    public static final String MESSAGING_MOVE_ENDPOINT = "/move";
    public static final String MESSAGING_NEW_CONTACT_ENDPOINT = "/new_contact";
    public static final String MESSAGING_NEW_CONTACT_INFO_ENDPOINT = "/new_contact_info";

    private Context context;
    private ContactRepository contactRepo;
    private MessageRepository messageRepo;
    private SharedPreferences sharedPreferences;
    private NodeRequest nodeRequest;

    public MessagingService(Context context) {
        this.context = context;
        contactRepo = new ContactRepository((Application) context.getApplicationContext());
        messageRepo = new MessageRepository((Application) context.getApplicationContext());
        sharedPreferences = context.getSharedPreferences(MESSAGING_PREFERENCES, 0);
        nodeRequest = new NodeRequest(context);
    }

    public void sendMessage(final Message message){
        contactRepo.getByContactKeyAsync(message.getContactKey(), new IDbCallback<ContactEntity>() {
            @Override
            public void onSuccess(ContactEntity toContactEntity) {
                try {
                    Contact toContact = new Contact(toContactEntity);
                    JSONObject jsonRequest = new JSONObject();
                    jsonRequest.put("pubid", toContact.getPubid());
                    jsonRequest.put("sender", toContact.getSenderkey());
                    jsonRequest.put("data", new AESEncryptor(toContact.getAeskey(), toContact.getAeskey()).encrypt(message.getText()));
                    nodeRequest.sendDataToNode(MESSAGING_API_PREFIX+MESSAGING_SEND_ENDPOINT, Request.Method.POST, jsonRequest.toString(), new INodeRequestCallback<JSONObject>() {
                        @Override public void onSuccess(JSONObject response) {
                        }
                    });
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void addNewMessage(Message message, String type){
        messageRepo.addMessage(new MessageEntity(message.getSeqno(), message, type));
    }

    public void sendNewContact(String pubId, Contact contact, String pubKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        JSONObject newContactData = new JSONObject();

        try {
            newContactData.put("name", contact.getName());
            newContactData.put("pubid", contact.getPubid());
            newContactData.put("contactkey", contact.getContactkey());
            newContactData.put("senderkey", contact.getSenderkey());
            newContactData.put("aeskey", contact.getAeskey());

            String encrData = "";

            encrData = new AESEncryptor(contact.getAeskey(), contact.getAeskey()).encrypt(newContactData.toString());

            JSONObject jsonRequest = new JSONObject();

            jsonRequest.put("pubid", pubId);
            jsonRequest.put("sender", contact.getSenderkey());
            jsonRequest.put("aeskey", new RSAEncryptor(contact.getAeskey(), pubKey).encryptData());
            jsonRequest.put("data", encrData);

            nodeRequest.sendDataToNode(MESSAGING_API_PREFIX+MESSAGING_NEW_CONTACT_ENDPOINT, Request.Method.POST, jsonRequest.toString(), new INodeRequestCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addNewContact(Contact contact){
        ContactEntity newContact = new ContactEntity(contact);
        contactRepo.addContact(newContact);
    }

    public void removeContact(Contact contact){
        ContactEntity contactToRemove = new ContactEntity(contact);
        contactRepo.removeContact(contactToRemove);
    }

    public void genNewPubId(){
        if (sharedPreferences.getString("pubId", "").isEmpty()){
            String newPubId = new KeyGenerator(context).generate();
            sharedPreferences.edit().putString("pubId", newPubId).apply();
        }
    }

    public String getPubId(){
        genNewPubId();
        return sharedPreferences.getString("pubId", "");
    }

    public void updateContactInfo(Contact contact){
        contactRepo.updateContact(new ContactEntity(contact));
    }

    public void setProfileName(String name){
        sharedPreferences.edit().putString("name", name).apply();
    }

    public void sendContactInfo(Contact contact){
        JSONObject newContactInfo = new JSONObject();

        try {
            String name = sharedPreferences.getString("name", "");
            newContactInfo.put("name", name);

            String encrData = new AESEncryptor(contact.getAeskey(), contact.getAeskey()).encrypt(newContactInfo.toString());

            JSONObject jsonRequest = new JSONObject();

            jsonRequest.put("pubid", contact.getPubid());
            jsonRequest.put("sender", contact.getSenderkey());
            jsonRequest.put("data", encrData);

            nodeRequest.sendDataToNode(MESSAGING_API_PREFIX+MESSAGING_NEW_CONTACT_INFO_ENDPOINT, Request.Method.POST, jsonRequest.toString(), new INodeRequestCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
