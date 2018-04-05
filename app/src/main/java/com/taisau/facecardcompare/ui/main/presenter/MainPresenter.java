package com.taisau.facecardcompare.ui.main.presenter;

import android.hardware.Camera;

import com.taisau.facecardcompare.listener.OnCardDetectListener;
import com.taisau.facecardcompare.model.CompareInfo;
import com.taisau.facecardcompare.ui.main.contract.MainContract;
import com.taisau.facecardcompare.ui.main.model.MainModel;


/**
 * Created by Administrator on 2017-09-07
 */

public class MainPresenter implements MainContract.Presenter {
    private MainContract.View view;
    private MainContract.Model model;


    public MainPresenter( MainContract.View view) {
        this.view = view;
        model = new MainModel(this);

    }

    public  Camera.PreviewCallback getPreviewCallback(){
        return model.getPreviewCallback();
    }
    public OnCardDetectListener getCardDetectListener(){return model.getCardDetectListener();}

    public void updateAdsTitle(){
        try {
            view.updateAdsTitle(model.getAdsTitle());
        } catch (Exception e) {
            e.printStackTrace();
            view.updateAdsTitle("标题");
        }
    }
    public void updateAdsSubitle(){
        try {
            view.updateAdsSubtitle(model.getAdsSubtitle());
        } catch (Exception e) {
            e.printStackTrace();
            view.updateAdsTitle("副标题");
        }
    }
    public void updateAdsPath(){
        try {
            //包含默认、用户设置和服务器下发
            view.updateAdsImage(model.getAdsImagePath());//可能多图
        } catch (Exception e) {
            e.printStackTrace();
            view.updateAdsTitle(null);
        }
    }
    public void updateUserName(){
        try {
            //包含默认、用户设置和服务器下发
            view.updateUserName(model.getUserName());//可能多图
        } catch (Exception e) {
            e.printStackTrace();
            view.updateUserName("公安局");
        }
    }



    public void initTime(){
        model.startUpdateTime();
    }
    public void stopTime(){
        model.stopUpdateTime();
    }
    public void doAutoLogin(){
        model.doAutoLogin();
    }


    @Override
    public void updateTimeToView(String time){
        view.updateTimeStatus(time);
    }
    @Override
    public void showToast(String msg){
        view.showToastMsg(msg);
    }

    @Override
    public void reLoad() {
        view.reLoad();
    }


    @Override
    public void updateFaceFrame(long[] position, int pic_width, int pic_height) {
        view.updateFaceFrame(position,pic_width,pic_height);
    }


    @Override
    public void updateCompareInfoView(CompareInfo info){
        view.updateCompareInfo(info);
    }

    @Override
    public void updateSoundStatus(boolean isInit,int soundNum) {
        model.changeSound(isInit,soundNum);
        view.updateSoundStatus(soundNum);
    }

    @Override
    public void setRunDetect(boolean run) {
        model.setRunDetect(run);
    }
}
