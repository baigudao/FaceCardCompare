package com.taisau.facecardcompare.ui.main.contract;

import android.hardware.Camera;

import com.taisau.facecardcompare.listener.OnCardDetectListener;
import com.taisau.facecardcompare.model.CompareInfo;

/**
 * Created by whx on 2017-09-07
 */

public interface MainContract {
    interface Model {
        String getAdsTitle();//广告标题
        String getAdsSubtitle();//广告副标题
        String[] getAdsImagePath();//广告图片
        String getUserName();//用户名，例如：公安局
        void startUpdateTime();
        void stopUpdateTime();
        void doAutoLogin();

        Camera.PreviewCallback getPreviewCallback();
        OnCardDetectListener getCardDetectListener();
        void changeSound(boolean isInit,int soundNum);
        void setRunDetect(boolean run);
    }

    interface View {
        void updateNetworkStatus(boolean netEnable);//是否有网络
        void updateTimeStatus(String time);//时间
        void updateAdsTitle(String title);//广告标题
        void updateAdsSubtitle(String subtitle);//广告副标题
        void updateAdsImage(String[] paths);//广告图片
        //绘制人脸框。三个参数，左上右下的间距？数组、框的宽度、框的高度
        void updateFaceFrame(long[] position, int pic_width, int pic_height);
        //默认公安局，在显示设置里设置
        void updateUserName(String result);
        void showToastMsg(String msg);
        //对比结果有5个参数，实际照、证件照、对比信息（结果）、对比结果图标、对比分值（成功才有分值）
       void updateCompareInfo(CompareInfo info);
        void updateSoundStatus(int soundNum);
        void reLoad();
    }

    interface Presenter {
        void updateTimeToView(String time);
        void showToast(String msg);
        void reLoad();
        //绘制人脸框。三个参数，左上右下的间距？数组、框的宽度、框的高度
        void updateFaceFrame(long[] position, int pic_width, int pic_height);
      /*  void setCompareLayoutVisibility(int visitable);
        //对比结果有5个参数，实际照、证件照、对比信息（结果）、对比结果图标、对比分值（成功才有分值）
        void updateCompareRealRes(Bitmap real);//现场实际照
        void updateCompareCardRes( Bitmap card);//证件照
        void updateCompareResultInfo(String result,int textColor);//对比信息(结果)
        void updateCompareResultScore(String result,int visitable);//对比分值,包含“核查通过（白名单自动比对）”显示绿色
        void updateCompareResultImg(int resId,int visitable);//对比结果图标*/
        void updateCompareInfoView(CompareInfo info);
        void updateSoundStatus(boolean isInit,int soundNum);
        void setRunDetect(boolean run);
    }
}
