package com.example.osbg.pot.utilities.encryption;

import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESDecryptor {
    public final static String ALGORITHM = "AES/CBC/PKCS5Padding";
    private byte[] IV;
    private byte[] aesKey;

    public AESDecryptor(String aesKey, String IV) {
        this.aesKey = Base64.decode(aesKey.getBytes(), Base64.DEFAULT);
        this.IV = Base64.decode(IV.getBytes(), Base64.DEFAULT);
    }

    public AESDecryptor(byte[] aesKey, byte[] IV){
        this.aesKey = aesKey;
        this.IV = IV;
    }

    public String decryptAESData(String dataToDecrypt) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(aesKey, "AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(IV);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] decryptedData = cipher.doFinal(Base64.decode(dataToDecrypt.getBytes(), Base64.DEFAULT));

            return new String(decryptedData);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void encryptFile(File inputFile, File outputFile) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(aesKey, "AES");
        IvParameterSpec iv = new IvParameterSpec(IV);

        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);

        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }

        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, c);

        byte[] buffer = new byte[1024];
        int read;

        while((read = inputStream.read(buffer)) != -1){
            cipherOutputStream.write(buffer, 0, read);
        }

        inputStream.close();
        outputStream.flush();
        cipherOutputStream.close();
    }

    public void decryptFile(File inputFile, File outputFile) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(aesKey, "AES");
        IvParameterSpec iv = new IvParameterSpec(IV);

        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, skeySpec, iv);

        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);

        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }

        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, c);

        byte[] buffer = new byte[1024];
        int read;

        while((read = inputStream.read(buffer)) != -1){
            cipherOutputStream.write(buffer, 0, read);
        }

        inputStream.close();
        outputStream.flush();
        cipherOutputStream.close();
    }
}
