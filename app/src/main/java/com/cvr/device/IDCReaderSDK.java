//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.cvr.device;

public class IDCReaderSDK {
    private static final String TAG = "unpack";
    private static volatile IDCReaderSDK _instance = new IDCReaderSDK();
    private static final byte[] byLicData = new byte[]{5, 0, 1, 0, 91, 3, 51, 1, 90, -77, 30, 0};

    public IDCReaderSDK() {
    }

    public static IDCReaderSDK getInstance() {
        if(_instance == null) {
            Class var0 = IDCReaderSDK.class;
            synchronized(IDCReaderSDK.class) {
                if(_instance == null) {
                    _instance = new IDCReaderSDK();
                }
            }
        }

        return _instance;
    }

    public int Init(String filepath) {
        return com.ivsign.android.IDCReader.IDCReaderSDK.wltInit(filepath);
    }

    public int unpack(byte[] wltdata) {
        return com.ivsign.android.IDCReader.IDCReaderSDK.wltGetBMP(wltdata, byLicData);
    }
}
