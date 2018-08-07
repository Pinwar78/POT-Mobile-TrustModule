package com.example.osbg.pot.utilities.encryption;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESDecryptor {
    private String IV;
    private String decryptedAESKey;

    public AESDecryptor(String IV, String decryptedAESKey) {
        this.IV = IV;
        this.decryptedAESKey = decryptedAESKey;
    }

    public String decryptAESData(String dataToDecrypt) {
        try {
            IvParameterSpec iv = new IvParameterSpec(Base64.decode(IV.getBytes(), Base64.DEFAULT));
            SecretKeySpec skeySpec = new SecretKeySpec(Base64.decode(decryptedAESKey.getBytes(), Base64.DEFAULT), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] decryptedData = cipher.doFinal(Base64.decode(dataToDecrypt.getBytes(), Base64.DEFAULT));

            return new String(decryptedData);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
