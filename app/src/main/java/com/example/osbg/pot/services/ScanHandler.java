package com.example.osbg.pot.services;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.widget.Toast;

import com.example.osbg.pot.domain_models.Contact;
import com.example.osbg.pot.infrastructure.KeyGenerator;
import com.example.osbg.pot.infrastructure.db.ContactRepository;
import com.example.osbg.pot.infrastructure.db.IDbCallback;
import com.example.osbg.pot.infrastructure.db.entities.ContactEntity;
import com.example.osbg.pot.utilities.FormatHelper;
import com.example.osbg.pot.utilities.HashCalculator;

import org.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ScanHandler {
    private Context context;
    private ContactRepository contactRepo;

    public ScanHandler(Context context){
        this.context = context;
        this.contactRepo = new ContactRepository((Application) context.getApplicationContext());
    }

    public void handle(String scanResultWithPotsum) throws InvalidPotQrCode{
        if (isValid(scanResultWithPotsum)) {
            String scanResult = scanResultWithPotsum.substring(4);
            if (FormatHelper.isUUID(scanResult.substring(0, 36))){
                connectToNode(scanResultWithPotsum);
            } else {
                try{
                    addNewContact(scanResult);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        else {
            throw new InvalidPotQrCode();
        }
    }

    private void connectToNode(String scanResultWithPotsum){
        final NodeConnector nodeConnector = new NodeConnector(context.getApplicationContext(), scanResultWithPotsum);
        final Intent messagingIntent = new Intent(context, MessagingPollingService.class);
        nodeConnector.connectToNode(new INodeRequestCallback(){
            @Override
            public void onSuccess(JSONObject response){
                Toast.makeText(context, "Connected to node!", Toast.LENGTH_SHORT).show();
                context.startService(messagingIntent);
            }
        });
    }

    private void addNewContact(String scanResult) throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        final String pubId = scanResult.substring(0, 44);
        final String contactKey = scanResult.substring(44, 88);
        final String pubkey = scanResult.substring(88);

        contactRepo.getByPubIdAsync(pubId, new IDbCallback<ContactEntity>() {
            @Override
            public void onSuccess(ContactEntity contactEntity) {
                if (contactEntity != null){
                    Toast.makeText(context, "This contact already exists.", Toast.LENGTH_LONG).show();
                    return;
                }

                try{
                    KeyGenerator keyGenerator = new KeyGenerator(context);
                    String senderKey = keyGenerator.generate();
                    String aesKey = Base64.encodeToString(KeyGenerator.genRandomBytes(16), Base64.DEFAULT);

                    Contact newContact = new Contact( contactKey, pubId, contactKey, senderKey, aesKey);

                    MessagingService messagingService = new MessagingService(context);
                    messagingService.addNewContact(newContact);

                    String myName = context.getSharedPreferences(MessagingService.MESSAGING_PREFERENCES, 0).getString("name", "");
                    if (myName.isEmpty()){
                        myName = contactKey;
                    }

                    Contact meContact = new Contact(myName, messagingService.getPubId(), senderKey, contactKey, aesKey);
                    messagingService.sendNewContact(pubId, meContact, pubkey);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean isValid(String scanResult){
        return HashCalculator.calculatePotMD5(scanResult.substring(4, scanResult.length())).equals(scanResult.substring(0, 4));
    }

    public class InvalidPotQrCode extends Exception{}
}