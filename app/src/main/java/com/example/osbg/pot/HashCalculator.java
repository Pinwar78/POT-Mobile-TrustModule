package com.example.osbg.pot;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * HashCalculator class that provides a method for calculating a new SHA-256 Hash when needed.
 */

public class HashCalculator {
    public String calculateHash(String s) {
        try {
            //Create SHA256 hash
            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            //Create Hex String
            StringBuffer hexString = new StringBuffer();
            for(int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
