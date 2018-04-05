package com.taisau.facecardcompare.ui.main.network;

import android.os.Handler;

import com.taisau.facecardcompare.http.AutoLogin;
import com.taisau.facecardcompare.ui.main.contract.MainContract;
import com.taisau.facecardcompare.ui.main.network.upload.UploadAction;

/**
 * Created by Administrator on 2017/12/28 0028.
 * 用于网络部分
 **/

public class MainNet<T> implements AutoLogin.AutoLoginListener{
    public enum NetAction{
        UPLOAD_FILE,DO_LOGIN
    }
    private MainContract.Presenter presenter;
    private T t;
    private NetAction action;
    public MainNet(NetAction action,T t,MainContract.Presenter presenter) {
        this.t=t;
        this.action=action;
        this.presenter=presenter;
        doAction();
    }

    private int doAction(){
        switch (this.action)
        {
            case UPLOAD_FILE:
                new UploadAction(this);
                return 1;
            case DO_LOGIN:
                AutoLogin.autologin(this);
                return 1;
            default:
                return 0;
        }
    }
    @Override
    public void onLoginFinish(int code) {
        if (code == -1) {
            //  Preference.clearUser();
//            Toast.makeText(context, "非法登录", Toast.LENGTH_SHORT).show();
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    presenter.showToast("非法登录");
                }
            });

          /*  Intent intent = new Intent(context, WelcomeActivity.class);
            context.startActivity(intent)*/
        } else if (code == 1) {
            switch (this.action)
            {
                case UPLOAD_FILE:
                    new UploadAction(this);
                    break;
                case DO_LOGIN:
                    break;
                default:
                    break;
            }
        }
    }
}
