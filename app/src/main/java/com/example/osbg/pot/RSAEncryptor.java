package com.example.osbg.pot;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSAEncryptor {
    private final String ALGORITHM = "RSA/ECB/PKCS1Padding";
    private Context context;
    private String dataToEncrypt = "";

    public RSAEncryptor(Context context, String dataToEncrypt) {
        this.context = context;
        this.dataToEncrypt = dataToEncrypt;
    }

    private PublicKey getNodePublicKey() {
        SharedPreferences shared = context.getSharedPreferences(MainActivity.PREFERENCES_NAME, 0);
        String nodePublicKey = shared.getString("nodepubkey", "");
        PublicKey generatedNodePubKey = createPublicKey(nodePublicKey);
        Log.d("generatedPubKey", generatedNodePubKey.toString());
        return generatedNodePubKey;
    }

    private PublicKey createPublicKey(String key) {
        try{
            byte[] encodedPublicKey = Base64.decode(key, Base64.DEFAULT);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(encodedPublicKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String encryptData() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        PublicKey nodePubKey = getNodePublicKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, nodePubKey);
        byte[] encVal = c.doFinal(dataToEncrypt.getBytes());
        String encryptedData = Base64.encodeToString(encVal, Base64.DEFAULT);
        Log.d("encryptedwithRSA", encryptedData);
        return encryptedData;
    }
}
