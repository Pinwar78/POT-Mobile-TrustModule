package com.example.osbg.pot.services.messaging;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.example.osbg.pot.MainActivity;
import com.example.osbg.pot.services.KeyGenerator;
import com.example.osbg.pot.services.messaging.MessagingService;

import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;

public class ContactInviter {
    private SharedPreferences sharedPreferences;
    private Context context;
    private String genContactKey;
    private String pubId;

    public ContactInviter(Context context){
        this.context = context;
        this.pubId = new MessagingService(context).getPubId();
        this.generateNewKey();
    }

    public String getInviteString(){
        return (this.pubId + this.genContactKey + this.getPubKey());
    }

    private void generateNewKey(){
        this.genContactKey = new KeyGenerator(context).generate();
    }

    private String getPubKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null, null);
            if (keyStore.containsAlias(MainActivity.ALIAS)) {
                Certificate cert = keyStore.getCertificate(MainActivity.ALIAS);
                PublicKey getPublicKey = cert.getPublicKey();
                byte[] publicKeyBytes = getPublicKey.getEncoded();
                return new String(Base64.encode(publicKeyBytes, 0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
