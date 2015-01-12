package com.example.emperor.cryptoexample1;

import android.util.Log;

/**
 * Created by emperor on 12/31/14.
 */
public class SimpleHexEncoder {

    private static final String HEX_FORMATTER_LOWER = "%02x"; //Read two bytes as lower case hex
    private static final String HEX_FORMATTER_UPPER = "%02x"; //Read two bytes as lower case hex
    private String hexFormatStyle = null;

    public static enum FontCase{UPPER, LOWER}

    /**
     * Creates a simple hex encoder that uses string
     * formatters to encode bytes to hex.
     * @param fontCaseEnum
     */
    public SimpleHexEncoder(FontCase fontCaseEnum){

        switch (fontCaseEnum){
            case UPPER:
                hexFormatStyle = HEX_FORMATTER_UPPER;
                break;
            case LOWER:
                hexFormatStyle = HEX_FORMATTER_LOWER;
                break;
            default:
                break;
        }

    }

    /**
     * Takes a Message Digest or any other byte
     * array and converts it into a hexidecimal
     * string using a format string for hex.
     * @param byteArray
     * @return
     */
    public String encodeHexString(byte[] byteArray){
        StringBuilder sb = new StringBuilder();

        for(byte b : byteArray){
            sb.append(String.format(hexFormatStyle, b));
        }

        Log.d(LogTag.TAG, "encodeHexString(): Byte array encoded to HEX string.");

        return sb.toString();
    }

}//End Class
