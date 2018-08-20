package com.example.osbg.pot.utilities;

public class FormatHelper {
    public static boolean isUUID(String uuid){
        return uuid.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    }

    public static boolean isHex(String hex){
        return hex.matches("^[0-9A-Fa-f]+$");
    }

    public static boolean isBase58(String base58str){
        return base58str.matches("^[1-9A-HJ-NP-Za-km-z]+$");
    }
}
