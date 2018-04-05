package com.taisau.facecardcompare.model;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/12/28 0028.
 */

public class CompareInfo {
     /*  void setCompareLayoutVisibility(int visitable);
        //对比结果有5个参数，实际照、证件照、对比信息（结果）、对比结果图标、对比分值（成功才有分值）
        void updateCompareRealRes(Bitmap real);//现场实际照
        void updateCompareCardRes( Bitmap card);//证件照
        void updateCompareResultInfo(String result,int textColor);//对比信息(结果)
        void updateCompareResultScore(String result,int visitable);//对比分值,包含“核查通过（白名单自动比对）”显示绿色
        void updateCompareResultImg(int resId,int visitable);//对比结果图标*/
     private Bitmap real;
     private Bitmap card;
     private String infoResult;
     private String scoreResult;
     private int resultID;
     private int textColor;
     private int scoreVisible;
     private int resultVisible;
     private int layoutVisible;
     private ChangeType type;
     public enum ChangeType{
         ONLY_TEXT,CLEAR_All,CARD_WITH_TEXT,FACE_WITH_TEXT_PASS,FACE_WITH_TEXT_FAIL,WHITE_RES,ERROR_TEXT
     }

    public CompareInfo(Bitmap real, Bitmap card, String infoResult, String scoreResult, int resultID, int textColor, int scoreVisible, int resultVisible, int layoutVisible,ChangeType type) {
        this.real = real;
        this.card = card;
        this.infoResult = infoResult;
        this.scoreResult = scoreResult;
        this.resultID = resultID;
        this.textColor = textColor;
        this.scoreVisible = scoreVisible;
        this.resultVisible = resultVisible;
        this.layoutVisible = layoutVisible;
        this.type=type;
    }

    public int getLayoutVisible() {
        return layoutVisible;
    }

    public void setLayoutVisible(int layoutVisible) {
        this.layoutVisible = layoutVisible;
    }

    public Bitmap getReal() {
        return real;
    }

    public void setReal(Bitmap real) {
        this.real = real;
    }

    public Bitmap getCard() {
        return card;
    }

    public void setCard(Bitmap card) {
        this.card = card;
    }

    public String getInfoResult() {
        return infoResult;
    }

    public void setInfoResult(String infoResult) {
        this.infoResult = infoResult;
    }

    public String getScoreResult() {
        return scoreResult;
    }

    public void setScoreResult(String scoreResult) {
        this.scoreResult = scoreResult;
    }

    public int getResultID() {
        return resultID;
    }

    public void setResultID(int resultID) {
        this.resultID = resultID;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getScoreVisible() {
        return scoreVisible;
    }

    public void setScoreVisible(int scoreVisible) {
        this.scoreVisible = scoreVisible;
    }

    public int getResultVisible() {
        return resultVisible;
    }

    public void setResultVisible(int resultVisible) {
        this.resultVisible = resultVisible;
    }

    public ChangeType getType() {
        return type;
    }

    public void setType(ChangeType type) {
        this.type = type;
    }
}
