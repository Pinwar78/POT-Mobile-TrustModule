package com.example.osbg.pot.utilities.encryption;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Base64.Decoder;
import android.util.Log;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryptor {
    public static final int AES_BYTES_NUM = 16;
    private final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private byte[] keyValue;
    private byte[] IV;

    public AESEncryptor(String key, String IV){
        keyValue = Base64.decode(key, Base64.CRLF);
        this.IV = Base64.decode(IV, Base64.CRLF);
    }

    public AESEncryptor(){
        keyValue = genRandomBytes(AES_BYTES_NUM);
        IV = genRandomBytes(AES_BYTES_NUM);
    }

    public String encrypt (String Data) throws Exception{
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV));
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;
    }

    private Key generateKey(){
        return new SecretKeySpec(keyValue, ALGORITHM);
    }

    public byte[] genRandomBytes(int x) {
        SecureRandom r = new SecureRandom();
        byte[] randBytes = new byte[x];
        r.nextBytes(randBytes);
        return randBytes;
    }

    public byte[] getKeyValue() {
        return keyValue;
    }

    public byte[] getIV() {
        return IV;
    }
}