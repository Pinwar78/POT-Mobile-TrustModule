package com.example.osbg.pot.utilities.encryption;

import org.json.JSONException;
import org.json.JSONObject;

public class HexHelper {
    public static boolean checkIfHEX(String s) {
        return s.matches("^[0-9A-Fa-f]+$") && !(s.matches("\\d+"));
    }

    public static JSONObject convertHEXtoJSON(String hexString) {
        StringBuilder QRCode = new StringBuilder("");
        for (int i = 0; i < hexString.length(); i += 2) {
            String str = hexString.substring(i, i + 2);
            QRCode.append((char) Integer.parseInt(str, 16));
        }
        try {
            return new JSONObject(QRCode.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String stringtoHex(String str){
        return Integer.toHexString(Integer.parseInt(str));
    }

    public static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }
}
