package com.example.osbg.pot.utilities.encryption;

import android.util.Base64;
import android.util.Log;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryptor {
    private final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private byte[] keyValue;
    private byte[] IV;
    private int numberBytes = 16;

    public AESEncryptor(String key, int numberBytes) {
        IV = genRandomBytes(numberBytes);
        if(key.length() != 16) {
           keyValue = genRandomBytes(16);
        } else {
            keyValue = key.getBytes();
        }
    }

    public String encrypt (String Data) throws Exception{
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV));
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        Log.d("key4enceto", new String(Base64.encodeToString(keyValue, Base64.DEFAULT)));
        Log.d("key4enceto", new String(Base64.encodeToString(IV, Base64.DEFAULT)));
        return encryptedValue;
    }

    public String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV));
        byte[] decodedValue = Base64.decode(encryptedData.getBytes(), Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGORITHM);
        return key;
    }

    public byte[] genRandomBytes(int x) {
        SecureRandom r = new SecureRandom();
        byte[] ivBytes = new byte[x];
        r.nextBytes(ivBytes);
        return ivBytes;
    }

    public byte[] getKeyValue() {
        return keyValue;
    }

    public byte[] getIV() {
        return IV;
    }
}