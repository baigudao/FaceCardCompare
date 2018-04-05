//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ivsign.android.IDCReader;

public class IDCReaderSDK {
    static {
        System.loadLibrary("wltdecode");
    }

    private IDCReaderSDK() {
    }

    public static native int wltInit(String var0);

    public static native int wltGetBMP(byte[] var0, byte[] var1);
}