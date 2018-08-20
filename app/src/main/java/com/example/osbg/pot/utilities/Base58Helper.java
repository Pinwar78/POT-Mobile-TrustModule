package com.example.osbg.pot.utilities;//package com.example.osbg.pot.utilities;//package com.example.osbg.pot.utilities;
//
//import com.example.osbg.pot.utilities.encryption.HexHelper;
//
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
//import java.math.BigInteger;
//public class Base58Helper {
//    public static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
//    private static final char ENCODED_ZERO = ALPHABET[0];
//
//    public static String encode(String str){
//        str = "wutenddd";
//        // string -> Hex
//        String s_hex = Hex.encodeHexString(str.getBytes());
//
//        // Hex -> Decimal
//        BigInteger decimal = new BigInteger(s_hex, 16);
//
//        // Decimal -> 58 kinds of characters
//        StringBuffer res = new StringBuffer();
//        while (decimal > 0) {
//            char c = ALPHABET[(int)decimal%58];
//            res.append(c);
//            decimal = decimal/58;
//        }
//
//        // zero byte confirm
//        byte [] temp_b = str.getBytes();
//        for (int i = 0; i < temp_b.length; i++) {
//            if (temp_b[i] != 0) {
//                break;
//            }
//            res.append(ENCODED_ZERO);
//        }
//
//        return res.reverse().toString();
//    }
//
//    public static String decode(String str) {
//
//        int decimal = 0;
//
//        // restore decimal.
//        char []chars = str.toCharArray();
//        for (int i = 0; i < chars.length; i++) {
//            char temp_c = chars[i];
//
//            int index_num = 0;
//            for (int j = 0; j < ALPHABET.length; j++) {
//                if (ALPHABET[j] == temp_c) {
//                    index_num = j;
//                }
//            }
//
//            decimal = decimal*58;
//            decimal = decimal + index_num;
//        }
//
//        // Decimal -> Hex
//        String s_hex = Integer.toHexString((int)decimal);
//
//        // Hex -> string
//        byte[] bytes = new byte[0];
//        try {
//            bytes = Hex.decodeHex(s_hex.toCharArray());
//        } catch (DecoderException e) {
//            e.printStackTrace();
//        }
//        return new String(bytes);
//    }
//}

//import android.nfc.FormatException;
//
//import java.math.BigInteger;
//import java.text.Format;
//import java.util.Arrays;
//
//public class Base58Helper {
//    public static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
//    private static final char ENCODED_ZERO = ALPHABET[0];
//    private static final int[] INDEXES = new int[128];
//    static {
//        Arrays.fill(INDEXES, -1);
//        for (int i = 0; i < ALPHABET.length; i++) {
//            INDEXES[ALPHABET[i]] = i;
//        }
//    }
//
//    /**
//     * Encodes the given bytes as a base58 string
//     *
//     * @param input the bytes to encode
//     * @return the base58-encoded string
//     */
//    public static String encode(byte[] input) {
//        if (input.length == 0) {
//            return "";
//        }
//        // Count leading zeros.
//        int zeros = 0;
//        while (zeros < input.length && input[zeros] == 0) {
//            ++zeros;
//        }
//        // Convert base-256 digits to base-58 digits (plus conversion to ASCII characters)
//        input = Arrays.copyOf(input, input.length); // since we modify it in-place
//        char[] encoded = new char[input.length * 2]; // upper bound
//        int outputStart = encoded.length;
//        for (int inputStart = zeros; inputStart < input.length; ) {
//            encoded[--outputStart] = ALPHABET[divmod(input, inputStart, 256, 58)];
//            if (input[inputStart] == 0) {
//                ++inputStart; // optimization - skip leading zeros
//            }
//        }
//        // Preserve exactly as many leading encoded zeros in output as there were leading zeros in input.
//        while (outputStart < encoded.length && encoded[outputStart] == ENCODED_ZERO) {
//            ++outputStart;
//        }
//        while (--zeros >= 0) {
//            encoded[--outputStart] = ENCODED_ZERO;
//        }
//        // Return encoded string (including encoded leading zeros).
//        return new String(encoded, outputStart, encoded.length - outputStart);
//    }
//
//    /**
//     * Decodes the given base58 string into the original data bytes.
//     *
//     * @param input the base58-encoded string to decode
//     * @return the decoded data bytes
//     * @throws FormatException if the given string is not a valid base58 string
//     */
//    public static byte[] decode(String input) throws FormatException {
//        if (input.length() == 0) {
//            return new byte[0];
//        }
//        // Convert the base58-encoded ASCII chars to a base58 byte sequence (base58 digits).
//        byte[] input58 = new byte[input.length()];
//        for (int i = 0; i < input.length(); ++i) {
//            char c = input.charAt(i);
//            int digit = c < 128 ? INDEXES[c] : -1;
//            if (digit < 0) {
//                throw new FormatException();
//            }
//            input58[i] = (byte) digit;
//        }
//        // Count leading zeros.
//        int zeros = 0;
//        while (zeros < input58.length && input58[zeros] == 0) {
//            ++zeros;
//        }
//        // Convert base-58 digits to base-256 digits.
//        byte[] decoded = new byte[input.length()];
//        int outputStart = decoded.length;
//        for (int inputStart = zeros; inputStart < input58.length; ) {
//            decoded[--outputStart] = divmod(input58, inputStart, 58, 256);
//            if (input58[inputStart] == 0) {
//                ++inputStart; // optimization - skip leading zeros
//            }
//        }
//        // Ignore extra leading zeroes that were added during the calculation.
//        while (outputStart < decoded.length && decoded[outputStart] == 0) {
//            ++outputStart;
//        }
//        // Return decoded data (including original number of leading zeros).
//        return Arrays.copyOfRange(decoded, outputStart - zeros, decoded.length);
//    }
//
//    public static BigInteger decodeToBigInteger(String input) throws FormatException {
//        return new BigInteger(1, decode(input));
//    }
//
//    /**
//     * Divides a number, represented as an array of bytes each containing a single digit
//     * in the specified base, by the given divisor. The given number is modified in-place
//     * to contain the quotient, and the return value is the remainder.
//     *
//     * @param number the number to divide
//     * @param firstDigit the index within the array of the first non-zero digit
//     *        (this is used for optimization by skipping the leading zeros)
//     * @param base the base in which the number's digits are represented (up to 256)
//     * @param divisor the number to divide by (up to 256)
//     * @return the remainder of the division operation
//     */
//    private static byte divmod(byte[] number, int firstDigit, int base, int divisor) {
//        // this is just long division which accounts for the base of the input digits
//        int remainder = 0;
//        for (int i = firstDigit; i < number.length; i++) {
//            int digit = (int) number[i] & 0xFF;
//            int temp = remainder * base + digit;
//            number[i] = (byte) (temp / divisor);
//            remainder = temp % divisor;
//        }
//        return (byte) remainder;
//    }
//}

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
            // paying with the same coin
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

    private static byte divmod256(byte[] number58, int startAt) {
        int remainder = 0;
        for (int i = startAt; i < number58.length; i++) {
            int digit58 = (int) number58[i] & 0xFF;
            int temp = remainder * BASE_58 + digit58;

            number58[i] = (byte) (temp / BASE_256);

            remainder = temp % BASE_256;
        }

        return (byte) remainder;
    }

    private static byte[] copyOfRange(byte[] source, int from, int to) {
        byte[] range = new byte[to - from];
        System.arraycopy(source, from, range, 0, range.length);

        return range;
    }
}