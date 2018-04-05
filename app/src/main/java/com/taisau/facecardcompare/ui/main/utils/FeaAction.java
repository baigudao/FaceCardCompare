package com.taisau.facecardcompare.ui.main.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.GFace;
import com.taisau.facecardcompare.ui.main.utils.listener.GetWFeaListener;
import com.taisau.facecardcompare.util.YUVUtils;

/**
 * Created by Administrator on 2017/8/7 0007.
 */

public class FeaAction {
    private HandlerThread getFeaInThread;
    private Handler handler;
    private GetWFeaListener listener;

    public enum FEA_CASE {
        DO_CHECK_WHITE, DO_FACE_COMPARE
    }

    public byte[] data;
    public GFace.FacePointInfo point;
    public GFace.FaceInfo info;

    public FeaAction() {
    }

    public void setFeaWhiteListener(final GetWFeaListener listener) {
        this.listener = listener;
        if (getFeaInThread == null)
            getFeaInThread = new HandlerThread("GET_WHITE_FEA", Thread.MIN_PRIORITY);
        if (getFeaInThread.getState() == Thread.State.NEW) {
            getFeaInThread.start();
            handler = new Handler(getFeaInThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    //提取特真值 0.9~1.2s左右
                    if (data != null) {

                        float[] com_fea = GFace.getFea(data, 320, 180, (int) point.ptEyeLeft.x, (int) point.ptEyeLeft.y, (int) point.ptEyeRight.x, (int) point.ptEyeRight.y,
                                (int) point.ptNose.x, (int) point.ptNose.y, (int) point.ptMouthLeft.x, (int) point.ptMouthLeft.y,
                                (int) point.ptMouthRight.x, (int) point.ptMouthRight.y);
                        GFace.FaceInfo aa = FeaAction.this.info;
                        listener.onFeaGet(com_fea, info);
                    }
                }
            };
        }
    }

    public float[] doFeaAction(FEA_CASE feaCase) {
        this.point.ptEyeLeft.x=this.point.ptEyeLeft.x-170;
        this.point.ptEyeLeft.y=this.point.ptEyeLeft.y-80;
        this.point.ptEyeRight.x=this.point.ptEyeRight.x-170;
        this.point.ptEyeRight.y=this.point.ptEyeRight.y-80;
        this.point.ptNose.x=this.point.ptNose.x-170;
        this.point.ptNose.y=this.point.ptNose.y-80;
        this.point.ptMouthLeft.x=this.point.ptMouthLeft.x-170;
        this.point.ptMouthLeft.y=this.point.ptMouthLeft.y-80;
        this.point.ptMouthRight.x=this.point.ptMouthRight.x-170;
        this.point.ptMouthRight.y=this.point.ptMouthRight.y-80;
        switch (feaCase) {
            case DO_CHECK_WHITE:
                return getFeaInThread();
            case DO_FACE_COMPARE:
                return getFeaInMain();
        }
        return null;
    }

    public float[] getFeaInThread() {
        handler.sendEmptyMessage(0);
        return null;
    }

    public float[] getFeaInMain() {
        float[] com_fea = null;
        if (data != null) {

            com_fea = GFace.getFea(data, 300, 200, (int) point.ptEyeLeft.x, (int) point.ptEyeLeft.y, (int) point.ptEyeRight.x, (int) point.ptEyeRight.y,
                    (int) point.ptNose.x, (int) point.ptNose.y, (int) point.ptMouthLeft.x, (int) point.ptMouthLeft.y,
                    (int) point.ptMouthRight.x, (int) point.ptMouthRight.y);
        }
        return com_fea;
    }

    //YUV预处理
    public GFace.FaceInfo feaPretreatment(byte[] data) {
        this.data = YUVUtils.scaleYUV300_200(data, 1280, 720);
        byte[] ret = GFace.detectFace(this.data, 300, 200);
        if (ret != null && ret[0] > 0) {
            GFace.FaceInfo aa = GFace.getFaceInfo(ret);
            this.info=aa;
            this.point=info.info[0];
            aa.info[0].ptNose.x = aa.info[0].ptNose.x + 170;
            aa.info[0].ptNose.y = aa.info[0].ptNose.y + 80;
            aa.info[0].ptMouthRight.x = aa.info[0].ptMouthRight.x + 170;
            aa.info[0].ptMouthRight.y = aa.info[0].ptMouthRight.y + 80;
            aa.info[0].ptEyeLeft.x = aa.info[0].ptEyeLeft.x + 170;
            aa.info[0].ptEyeLeft.y = aa.info[0].ptEyeLeft.y + 80;
            aa.info[0].ptEyeRight.x = aa.info[0].ptEyeRight.x + 170;
            aa.info[0].ptEyeRight.y = aa.info[0].ptEyeRight.y + 80;
            aa.info[0].ptMouthLeft.x = aa.info[0].ptMouthLeft.x + 170;
            aa.info[0].ptMouthLeft.y = aa.info[0].ptMouthLeft.y + 80;
            aa.rc[0].left = aa.rc[0].left + 170;
            aa.rc[0].right = aa.rc[0].right + 170;
            aa.rc[0].top = aa.rc[0].top + 80;
            aa.rc[0].bottom = aa.rc[0].bottom + 80;
            return aa;
            //生成人脸框
        } else return null;
    }
}
