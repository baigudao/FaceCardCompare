package com.taisau.facecardcompare.ui.setting.presenter;

import com.taisau.facecardcompare.http.AutoLogin;
import com.taisau.facecardcompare.http.BaseRespose;
import com.taisau.facecardcompare.http.NetClient;
import com.taisau.facecardcompare.model.UpgradePKG;
import com.taisau.facecardcompare.ui.setting.model.ISettingModel;
import com.taisau.facecardcompare.ui.setting.model.SettingModel;
import com.taisau.facecardcompare.ui.setting.view.ISettingView;
import com.taisau.facecardcompare.util.Preference;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.taisau.facecardcompare.util.Constant.SOFT_NAME;

/**
 * Created by whx on 2017-08-14
 */

public class SettingPresenter implements AutoLogin.AutoLoginListener{

    private ISettingView view;
    private ISettingModel model;

    private boolean hasNew=false;
    public String version="";
    public UpgradePKG pkg=null;

    String msg="";

    public SettingPresenter(ISettingView view){
        this.view=view;
        model=new SettingModel(this);
    }

    public void checkVersion(final String version){
        this.version=version;
        NetClient.getInstance().getDeviceAPI().updateDevice(Preference.getServerUrl() + "upgradePkg/getLastUpgradePkg", Preference.getSid(), Preference.getSession(), SOFT_NAME)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseRespose<UpgradePKG>>() {
                    @Override
                    public void onCompleted() {
                        if (hasNew)
                            view.setNewImgShow(true,pkg);
                        else
                            view.setNewImgShow(false,pkg);
                        if (!msg.equals(""))
                            view.toastMsg(msg);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (hasNew)
                            view.setNewImgShow(true,pkg);
                        else
                            view.setNewImgShow(false,pkg);
                        msg="无法检查更新，无法连接到数据平台";
                        view.toastMsg(msg);
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(BaseRespose<UpgradePKG> upgradePKGBaseRespose) {
                        if (upgradePKGBaseRespose.isSuccess())
                        {
                            final UpgradePKG pkg = upgradePKGBaseRespose.getResult();
                            if (pkg != null&&!pkg.getVersion().equals(SettingPresenter.this.version)) {
                                String netVersion=pkg.getVersion().substring(pkg.getVersion().length()-7,pkg.getVersion().length());
                                String localVersion=SettingPresenter.this.version.substring(SettingPresenter.this.version.length()-7,SettingPresenter.this.version.length());
                                hasNew=compareVersion(netVersion,localVersion);
                            }
                            SettingPresenter.this.pkg=pkg;
                            msg="";
                        }
                        else if (upgradePKGBaseRespose.getCode().equals("50004") || upgradePKGBaseRespose.getCode().equals("40004") || upgradePKGBaseRespose.getCode().equals("40005")) {
                            AutoLogin.autologin(SettingPresenter.this);
                        }
                        else {
                            msg="无法检查更新，未知原因";
                            pkg=null;
                        }
                    }
                });

    }

    boolean compareVersion(String net,String loc){
        String[] netSplit=net.split("\\.");
        String[] locSplit=loc.split("\\.");
        int locV = 0,netV = 0;
        for (int i=0;i<4;i++)
        {
            locV= locV+(int) (Integer.parseInt(locSplit[i])*Math.pow(10,3-i));
            netV=netV+(int) (Integer.parseInt(netSplit[i])*Math.pow(10,3-i));
            if (locV<netV)
                return true;
        }
        return false;

    }

    public void clearData(){
        model.clearDevice();
    }
    public void clearDataComplete(){
        view.clearDataComplete();
    }

    @Override
    public void onLoginFinish(int code) {
         if (code == 1) {
             checkVersion(version);
        }
    }

}
