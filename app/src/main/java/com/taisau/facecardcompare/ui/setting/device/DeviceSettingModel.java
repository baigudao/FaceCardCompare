package com.taisau.facecardcompare.ui.setting.device;

import com.taisau.facecardcompare.FaceCardApplication;
import com.taisau.facecardcompare.http.AutoLogin;
import com.taisau.facecardcompare.http.BaseRespose;
import com.taisau.facecardcompare.http.NetClient;
import com.taisau.facecardcompare.model.Device;
import com.taisau.facecardcompare.util.FileUtils;
import com.taisau.facecardcompare.util.Preference;

import java.io.File;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.taisau.facecardcompare.http.Config.DEV_SIGN;
import static com.taisau.facecardcompare.util.Constant.LIB_DIR;

/**
 * Created by Administrator on 2017/8/21 0021
 */

public class DeviceSettingModel implements IDeviceSettingModel {
    private File file[] = new File[4];
    private File[] whiteFile = new File[2];
    private DeviceSettingPresenter presenter;

    public DeviceSettingModel(DeviceSettingPresenter deviceSettingPresenter) {
        this.presenter = deviceSettingPresenter;
        file[0] = new File(LIB_DIR + "/card_fea");
        file[1] = new File(LIB_DIR + "/face_fea");
        file[2] = new File(LIB_DIR + "/face_img");
        file[3] = new File(LIB_DIR + "/card_img");
        whiteFile[0] = new File(LIB_DIR + "/white_img");
        whiteFile[1] = new File(LIB_DIR + "/white_fea");
    }

    @Override
    public String getCurrentStatus(int flag) {
        String res;
        switch (flag) {
            case 0:
                res = Preference.getDevSno();
                break;
            case 1:
                res = Preference.getServerIp();
                break;
            case 2:
                res = Preference.getServerPort();
                break;
            case 3:
                res = null;
                break;
            case 4:
                res = null;
                break;
            case 5:
                res = Preference.getDevProvince();
                if (res == null)
                    res = "请选择省";
//                Log.e("DeviceSettingModel", "获取省名称：res=" + res);
                break;
            case 6:
                res = Preference.getDevCity();
                if (res == null)
                    res = "请选择市";
//                Log.e("DeviceSettingModel", "获取市名称：res=" + res);
                break;
            case 7:
                res = Preference.getDevTownShip();
                if (res == null)
                    res = "请选择县";
//                Log.e("DeviceSettingModel", "获取县名称：res=" + res);
                break;
            default:
                res = "";
                break;
        }
        return res;
    }

    @Override
    public void setStatusChange(int pos, String content) {
        switch (pos) {
            case 0:
                Preference.setDevSno(content);
                break;
            case 1:
                Preference.setServerIp(content);
                Preference.setServerUrl("http://" + Preference.getServerIp() + ":" + Preference.getServerPort() + "/FaceNew/");
                reLogin();
                break;
            case 2:
                Preference.setServerPort(content);
                Preference.setServerUrl("http://" + Preference.getServerIp() + ":" + Preference.getServerPort() + "/FaceNew/");
                reLogin();
                break;
            case 5:
                if (content.contains("请选择")) {
                    content = null;
                }
                Preference.setDevProvince(content);
                Preference.setDevCity(null);
                Preference.setDevTownShip(null);
                break;
            case 6:
                if (content.contains("请选择")) {
                    content = null;
                }
                Preference.setDevCity(content);
                Preference.setDevTownShip(null);
                break;
            case 7:
                if (content.contains("请选择")) {
                    content = null;
                }
                Preference.setDevTownShip(content);
                break;
            default:
                break;
        }
    }


    @Override
    public void clearSoft() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FaceCardApplication.getApplication().getDaoSession().getHistoryListDao().deleteAll();
                FaceCardApplication.getApplication().getDaoSession().getReLoadInfoDao().deleteAll();
                deleteFile(file);
                Preference.setHisLastId(0);
                Preference.setHisFirstId(1);
                presenter.clearComplete(3);
            }
        }).start();

    }

    @Override
    public void clearList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FaceCardApplication.getApplication().getDaoSession().getGroupDao().deleteAll();
                FaceCardApplication.getApplication().getDaoSession().getPersonDao().deleteAll();
                FaceCardApplication.getApplication().getDaoSession().getFaceListDao().deleteAll();
                FaceCardApplication.getApplication().getDaoSession().getGroupJoinDao().deleteAll();
                FaceCardApplication.getApplication().getDaoSession().getWhiteFeaDao().deleteAll();
                FaceCardApplication.getApplication().getDaoSession().getGroupListDao().deleteAll();
                deleteFile(whiteFile);
                presenter.clearComplete(4);
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

    public void getFileMemorySize() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long totalLength = 0;
                for (File file1 : file) {
                    totalLength += FileUtils.getFolderSize(file1);
                }

                String memorySize = "";
                if (totalLength > 1024 && totalLength < 1024 * 1024)
                    memorySize = totalLength / 1024 + "KB";
                else if (totalLength > 1024 * 1024 && totalLength < 1024 * 1024 * 1024)
                    memorySize = totalLength / 1024 / 1024 + "MB";
                else if (totalLength > 1024 * 1024 * 1024)
                    memorySize = totalLength / 1024 / 1024 / 1024 + "GB";
                else
                    memorySize = totalLength + "B";
                presenter.updateOccupation(3,memorySize);
            }
        }).start();

    }

    public void getPersonListSize() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long totalLength = FileUtils.getFolderSize(whiteFile[0]) + FileUtils.getFolderSize(whiteFile[1]);

                String memorySize = "";
                if (totalLength > 1024 && totalLength < 1024 * 1024)
                    memorySize = totalLength / 1024 + "KB";
                else if (totalLength > 1024 * 1024 && totalLength < 1024 * 1024 * 1024)
                    memorySize = totalLength / 1024 / 1024 + "MB";
                else if (totalLength > 1024 * 1024 * 1024)
                    memorySize = totalLength / 1024 / 1024 / 1024 + "GB";
                else
                    memorySize = totalLength + "B";
                presenter.updateOccupation(4,memorySize);
            }
        }).start();

    }

    @Override
    public void checkSerialNum(final AutoLogin.AutoLoginListener listener, String num) {
        NetClient.getInstance().getDeviceAPI().deviceLogin(Preference.getServerUrl() + "user/login", num, DEV_SIGN)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseRespose<Device>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        listener.onLoginFinish(-1);
                    }

                    @Override
                    public void onNext(BaseRespose<Device> device) {
                        System.out.println("auto login next:" + device.isSuccess() + " msg:" + device.getMsg() + " code:" + device.getCode());
                        if (device.isSuccess()) {
                            Preference.setSession(device.getSession());
                            Preference.setSid(device.getSid());
                            listener.onLoginFinish(1);
                            System.out.println("改序列号，重新登录成功，重启MQ");
                            FaceCardApplication.getApplication().setMqServiceRestart();
                        } else {
                            listener.onLoginFinish(-1);
                        }
                    }
                });
    }

    @Override
    public void getOccupation(int flag) {
        switch (flag){
            case 3:
                getFileMemorySize();
                break;
            case 4:
                getPersonListSize();
                break;
        }
    }

    private void reLogin() {
        AutoLogin.autologin(code -> {
            if (code == 1) {
                System.out.println("改ip或端口，重新登录成功，重启MQ");
                FaceCardApplication.getApplication().setMqServiceRestart();
            }
        });
    }
}
