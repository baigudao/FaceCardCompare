package com.taisau.facecardcompare.ui.setting.model;

import com.taisau.facecardcompare.FaceCardApplication;
import com.taisau.facecardcompare.ui.setting.presenter.SettingPresenter;
import com.taisau.facecardcompare.util.FileUtils;
import com.taisau.facecardcompare.util.Preference;

import java.io.File;

import static com.taisau.facecardcompare.util.Constant.LIB_DIR;

/**
 * Created by whx on 2017-08-15
 */

public class SettingModel implements ISettingModel {
    File file[] = new File[4];
    File[] whiteFile = new File[2];
    private SettingPresenter presenter;

    public SettingModel(SettingPresenter settingPresenter) {
        this.presenter = settingPresenter;
        file[0] = new File(LIB_DIR + "/card_fea");
        file[1] = new File(LIB_DIR + "/face_fea");
        file[2] = new File(LIB_DIR + "/face_img");
        file[3] = new File(LIB_DIR + "/card_img");
        whiteFile[0] = new File(LIB_DIR + "/white_img");
        whiteFile[1] = new File(LIB_DIR + "/white_fea");
    }


    @Override
    public void clearDevice() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Preference.clearUser();
                deleteFile(whiteFile);
                deleteFile(file);
                FaceCardApplication.getApplication().getDaoSession().getHistoryListDao().deleteAll();
                FaceCardApplication.getApplication().getDaoSession().getReLoadInfoDao().deleteAll();
                FaceCardApplication.getApplication().getDaoSession().getGroupDao().deleteAll();
                FaceCardApplication.getApplication().getDaoSession().getPersonDao().deleteAll();
                FaceCardApplication.getApplication().getDaoSession().getFaceListDao().deleteAll();
                FaceCardApplication.getApplication().getDaoSession().getGroupJoinDao().deleteAll();
                FaceCardApplication.getApplication().getDaoSession().getWhiteFeaDao().deleteAll();
                FaceCardApplication.getApplication().getDaoSession().getGroupListDao().deleteAll();
                presenter.clearDataComplete();
            }
        }).start();

    }

    public void deleteFile(File[] fileList) {
        int totalLength = 0;
        for (File file : fileList) {
            totalLength += file.list().length;
        }
        String[] fileNames = new String[totalLength];
        int currentLength = 0;
        for (File file : fileList) {
            String[] temp = file.list();
            for (int i = 0; i < temp.length; i++) {
                temp[i] = file.getAbsolutePath() + "/" + temp[i];
            }
            System.arraycopy(temp, 0, fileNames, currentLength, file.list().length);
            currentLength += file.list().length;
            FileUtils.deleteDirectory(file.getAbsolutePath());
            file.mkdir();
        }
        FileUtils.updateFileToSystem(FaceCardApplication.getApplication(), fileNames, null);
    }
}
