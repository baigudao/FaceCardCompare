package com.taisau.facecardcompare.util;

/**
 * Created by LUA on 2016/4/22.
 */
public class ByteUtlis {
    
    public static byte[] hexStrToBytes(String hexString){
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     *  bytesTo16String
     * @param src
     * @return
     */
    public static String bytesToHexStr(byte[] src){
        if (src == null || src.length <= 0){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < src.length; i++){
            int v = src[i] & 0xFF;
            String s = Integer.toHexString(v);
            if (s.length()<2){
                sb.append("0");
            }
            sb.append(s);
        }
        return sb.toString();
    }
}
