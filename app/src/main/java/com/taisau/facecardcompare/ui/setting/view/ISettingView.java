package com.taisau.facecardcompare.ui.setting.view;

import com.taisau.facecardcompare.em.DialogCase;
import com.taisau.facecardcompare.model.UpgradePKG;

/**
 * Created by whx on 2017-08-15
 */

public interface ISettingView {

    void setRestoreDefaultSuccess();
    void updateAppVersion(String version);
    void setNewImgShow(boolean isShow,UpgradePKG pkg);
    void setAlertDialogShow(DialogCase dialogCase);
    void toastMsg(String msg);
    void clearDataComplete();

}
