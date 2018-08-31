package com.example.osbg.pot.utilities;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class Base58Helper {

    private static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
            .toCharArray();
    private static final int BASE_58 = ALPHABET.length;
    private static final int BASE_256 = 256;

    private static final int[] INDEXES = new int[128];
    static {
        for (int i = 0; i < INDEXES.length; i++) {
            INDEXES[i] = -1;
        }
        for (int i = 0; i < ALPHABET.length; i++) {
            INDEXES[ALPHABET[i]] = i;
        }
    }

    public static String encode(byte[] input) {
        if (input.length == 0) {
            return "";
        }

        //
        // Make a copy of the input since we are going to modify it.
        //
        input = copyOfRange(input, 0, input.length);

        //
        // Count leading zeroes
        //
        int zeroCount = 0;
        while (zeroCount < input.length && input[zeroCount] == 0) {
            ++zeroCount;
        }

        //
        // The actual encoding
        //
        byte[] temp = new byte[input.length * 2];
        int j = temp.length;

        int startAt = zeroCount;
        while (startAt < input.length) {
            byte mod = divmod58(input, startAt);
            if (input[startAt] == 0) {
                ++startAt;
            }

            temp[--j] = (byte) ALPHABET[mod];
        }

        //
        // Strip extra '1' if any
        //
        while (j < temp.length && temp[j] == ALPHABET[0]) {
            ++j;
        }

        //
        // Add as many leading '1' as there were leading zeros.
        //
        while (--zeroCount >= 0) {
            temp[--j] = (byte) ALPHABET[0];
        }

        byte[] output = copyOfRange(temp, j, temp.length);
        return new String(output);
    }

    public static String decode(String str) {

        int decimal = 0;

        // restore decimal.
        char []chars = str.toCharArray();
        for (char temp_c : chars) {

            int index_num = 0;
            for (int j = 0; j < ALPHABET.length; j++) {
                if (ALPHABET[j] == temp_c) {
                    index_num = j;
                }
            }

            decimal = decimal*58;
            decimal = decimal + index_num;
        }

        // Decimal -> Hex
        String s_hex = Integer.toHexString(decimal);

        // Hex -> string
        byte[] bytes = new byte[0];
        try {
            bytes = Hex.decodeHex(s_hex.toCharArray());
        } catch (DecoderException e) {
            e.printStackTrace();
        }
        return new String(bytes);
    }


    private static byte divmod58(byte[] number, int startAt) {
        int remainder = 0;
        for (int i = startAt; i < number.length; i++) {
            int digit256 = (int) number[i] & 0xFF;
            int temp = remainder * BASE_256 + digit256;

            number[i] = (byte) (temp / BASE_58);

            remainder = temp % BASE_58;
        }

        return (byte) remainder;
    }


    private static byte[] copyOfRange(byte[] source, int from, int to) {
        byte[] range = new byte[to - from];
        System.arraycopy(source, from, range, 0, range.length);

        return range;
    }
}