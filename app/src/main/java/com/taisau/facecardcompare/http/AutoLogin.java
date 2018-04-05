package com.taisau.facecardcompare.http;

import com.taisau.facecardcompare.model.Device;
import com.taisau.facecardcompare.util.Preference;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.taisau.facecardcompare.http.Config.DEV_SIGN;

/**
 * Created by Administrator on 2017/3/10 0010.
 */

public class AutoLogin {
    public interface AutoLoginListener{
        void onLoginFinish(int code);
    }
    public static void autologin(final AutoLoginListener listener)
    {
        NetClient.getInstance().getDeviceAPI().deviceLogin(Preference.getServerUrl()+"user/login",Preference.getDevSno(),DEV_SIGN)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseRespose<Device>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(BaseRespose<Device> device) {
                     //   System.out.println("auto login next:"+device.isSuccess()+" msg:"+device.getMsg()+" code:"+device.getCode());
                        if (device.isSuccess())
                        {
                            Preference.setSession(device.getSession());
                            Preference.setSid(device.getSid());
                            listener.onLoginFinish(1);
                        }
                        else
                        {
                            if (device.getCode().equals("10002"))
                            {
                                listener.onLoginFinish(-1);
                            }
                            if (device.getCode().equals("40002"))
                            {
                                listener.onLoginFinish(-1);
                            }
                        }
                    }
                });
    }
}
