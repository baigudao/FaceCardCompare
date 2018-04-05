package com.taisau.facecardcompare.ui.setting.device;


/**
 * Created by whx on 2017-08-17
 */

public interface IDeviceSettingView {
    void showChangeResult(int flag, String value);
    void showCheckSerialNumResult(boolean result, String value);
    void clearComplete(int flag);
    void showOccupation(int flag,String occupation);
}
