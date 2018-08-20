package com.example.osbg.pot.utilities;

import com.example.osbg.pot.utilities.encryption.HexHelper;

import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * HashCalculator class that provides a method for calculating a new SHA-256 Hash when needed.
 */

public class HashCalculator {
    public static String calculateHash(String s, String salt) {
        try {
            //Create SHA256 hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            //Adding salt
            if (!salt.isEmpty()){
                digest.update(salt.getBytes());
            }

            digest.update(s.getBytes());

            return String.valueOf(Hex.encodeHex(digest.digest()));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String calculateHash(String s){
        return HashCalculator.calculateHash(s, "");
    }

    public static String calculatePotMD5(String s){
        StringBuffer hexString = new StringBuffer();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hash = md.digest(s.getBytes());
        String md5 = "";

        for (int i = 0; i < hash.length; i++) {
            if ((0xff & hash[i]) < 0x10) {
                hexString.append("0"
                        + Integer.toHexString((0xFF & hash[i])));
            } else {
                hexString.append(Integer.toHexString(0xFF & hash[i]));
            }
        }
        md5 = hexString.substring(3, 7);
        return md5;
    }
}
