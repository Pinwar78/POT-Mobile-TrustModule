package com.example.osbg.pot.services;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.android.volley.Request;
import com.example.osbg.pot.activities.DappActivity;
import com.example.osbg.pot.domain_models.Contact;
import com.example.osbg.pot.infrastructure.db.ContactRepository;
import com.example.osbg.pot.infrastructure.db.IDbCallback;
import com.example.osbg.pot.infrastructure.db.entities.ContactEntity;
import com.example.osbg.pot.services.api.INodeRequestCallback;
import com.example.osbg.pot.services.api.NodeRequest;
import com.example.osbg.pot.services.dapps.DappLoader;
import com.example.osbg.pot.services.messaging.MessagingPollingService;
import com.example.osbg.pot.services.messaging.MessagingService;
import com.example.osbg.pot.utilities.FormatHelper;
import com.example.osbg.pot.utilities.HashCalculator;
import com.example.osbg.pot.utilities.encryption.AESEncryptor;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ScanHandler {
    private Context context;
    private ContactRepository contactRepo;

    public ScanHandler(Context context){
        this.context = context;
        this.contactRepo = new ContactRepository((Application) context.getApplicationContext());
    }

    public void handle(String scanResult) throws InvalidPotQrCode{
        Log.d("POTT scan", scanResult);
        if (hasPotsum(scanResult)) {
            String scanResultStr = scanResult.substring(4);
            if (FormatHelper.isUUID(scanResultStr.substring(0, 36))){
                connectToNode(scanResultStr);
            } else {
                addNewContact(scanResultStr);
            }
        } else if (URLUtil.isValidUrl(scanResult)){
            openDapp(scanResult);
        }
        else {
            throw new InvalidPotQrCode();
        }
    }

    private void openDapp(String scanResult){
        final DappLoader dappLoader = new DappLoader(context, "testing") {
            @Override
            public void onDataLoaded(final File dappDir){
                NodeRequest nodeRequest = new NodeRequest(context);
                try {
                    nodeRequest.getBytesDapp("", Request.Method.GET, "", new INodeRequestCallback<byte[]>() {
                        @Override
                        public void onSuccess(byte[] response) {
                            File dappDir = new File(filesHandler.getDappsDir(), "testing");
                            File indexFile = new File(dappDir, "index.html");
//                            try {
//                                indexFile.createNewFile();
//                                FileOutputStream fileStream = new FileOutputStream(indexFile);
//                                fileStream.write(response);
//                                fileStream.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                                Log.d("POTT ERROR", "CANT SAVE");
//                            }

                            Intent webviewIntent = new Intent(context.getApplicationContext(), DappActivity.class);
                            webviewIntent.putExtra("indexFile", indexFile);
                            context.startActivity(webviewIntent);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        dappLoader.loadDapp();
    }

    private void connectToNode(String scanResult){
        String uuid = scanResult.substring(0, 36);
        String nodePubKey = scanResult.substring(36, 252);
        String host = scanResult.substring(252);

        final NodeConnector nodeConnector = new NodeConnector(context.getApplicationContext(), uuid, nodePubKey, host);

        final Intent messagingIntent = new Intent(context, MessagingPollingService.class);
        nodeConnector.connectToNode(new INodeRequestCallback<JSONObject>(){
            @Override
            public void onSuccess(JSONObject response){
                Toast.makeText(context, "Connected to node!", Toast.LENGTH_SHORT).show();
                context.startService(messagingIntent);
            }
        });
    }

    private void addNewContact(String scanResult){
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
                    String aesKey = Base64.encodeToString(KeyGenerator.genRandomBytes(AESEncryptor.AES_BYTES_NUM), Base64.DEFAULT);

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

    private boolean hasPotsum(String scanResult){
        return HashCalculator.calculatePotMD5(scanResult.substring(4, scanResult.length())).equals(scanResult.substring(0, 4));
    }

    public class InvalidPotQrCode extends Exception{}
}