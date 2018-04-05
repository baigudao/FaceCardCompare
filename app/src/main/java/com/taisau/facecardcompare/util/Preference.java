package com.taisau.facecardcompare.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.taisau.facecardcompare.FaceCardApplication;

/**
 * Created by Administrator on 2016/7/14 0014.
 */
public class Preference {

    //http相关类别 session sid 用于服务器
    private static final String SESSION="session";
    private static final String SID="sid";
    //设备号
    private static final String DEV_SNO="dev_sno";

    //his:History
    //最后添加项id
    private static final String HIS_LAST_ID="his_last_id";
    //历史记录首项id
    private static final String HIS_FIRST_ID="his_first_id";
    //机器码
    private static final String  MACHINE_KEY="machine_key";


    //setting
    //黑名单预警
    private static final String BLACK_WARNING="black_warning";
    //语音提示
    private static final String VOICE_TIPS="voice_tips";
    //年龄预警
    private static final String AGE_WARNING="age_warning";
    //年龄预警上限范围
    private static final String AGE_WARNING_MAX="age_warning_max";
    //年龄预警下限范围
    private static final String AGE_WARNING_MIN="age_warning_min";
    //白名单预警
    private static final String WHITE_CHECK="white_check";
    //比对通过模式（easy/hard/diy）
    private static final String SCORE_RANK="score_rank";
    //对比通过具体值
    private static final String SCORE_RANK_VALUE="score_rank_value";
    //服务器地址
    private static final String SERVER_URL="server_url";
    private static final String SERVER_IP="server_ip";
    private static final String SERVER_PORT="server_port";
    //设备地址
    private static final String DEV_PROVINCE="dev_province";
    private static final String DEV_CITY="dev_city";
    private static final String DEV_TOWN_SHIP="dev_town_ship";

    //活体检测
    private static final String ALIVE_CHECK="alive_check";
    //无人脸数量
    private static final String NO_FACE_COUNT="no_face_count";
    //自定义名称
    private static final String CUSTOM_NAME="custom_name";
    //主标题
    private static final String MAIN_TITTLE="main_tittle";
    //副标题
    private static final String SUB_TITTLE="sub_tittle";
    //广告图片路径
    private static final String ADS_PATH="ads_path";
    //音量
    private static final String DEV_VOLUME = "dev_volume";
    public  static void setSession(String session){
        saveString(SESSION,session);
    }
    public static String getSession(){
        return getString(SESSION);
    }

    public  static void setSid(String sid){
        saveString(SID,sid);
    }
    public static String getSid(){
        return getString(SID);
    }

    public  static void setDevSno(String devSno){
        saveString(DEV_SNO,devSno);
    }
    public static String getDevSno(){
        return getString(DEV_SNO);
    }

    public  static void setMachineKey(String machineKey){
        saveString(MACHINE_KEY,machineKey);
    }
    public static String getMachineKey(){
        return getString(MACHINE_KEY);
    }

    public  static void setHisLastId(int hisLastId){
        saveInt( HIS_LAST_ID,hisLastId);
    }
    public static int getHisLastId(){
        return getInt( HIS_LAST_ID);
    }

    public  static void setHisFirstId(int hisFirstId){
        saveInt( HIS_FIRST_ID,hisFirstId);
    }
    public static int getHisFirstIdId(){
        return getInt( HIS_FIRST_ID);
    }

    public static void setBlackWarning(String blackWarning){ saveString(BLACK_WARNING,blackWarning);}
    public static String getBlackWarning(){return getString(BLACK_WARNING);}

    public static void setVoiceTips(String voiceTips){ saveString(VOICE_TIPS,voiceTips);}
    public static String getVoiceTips(){return getString(VOICE_TIPS);}

    public static void setAgeWarning(String ageWarning){saveString(AGE_WARNING,ageWarning);}
    public static String getAgeWarning(){return getString(AGE_WARNING);}

    public static void setAgeWarningMIN(String ageWarningMIN){saveString(AGE_WARNING_MIN,ageWarningMIN);}
    public static String getAgeWarningMIN(){return getString(AGE_WARNING_MIN);}

    public static void setAgeWarningMAX(String ageWarningMAX){saveString(AGE_WARNING_MAX,ageWarningMAX);}
    public static String getAgeWarningMAX(){return getString(AGE_WARNING_MAX);}

    public static void setWhiteCheck(String whiteCheck){
        saveString(WHITE_CHECK,whiteCheck);
    }
    public static String getWhiteCheck() {
        return  getString(WHITE_CHECK);
    }

    public static void setScoreRank(String scoreRank){saveString(SCORE_RANK,scoreRank);}
    public static String getScoreRank(){return getString(SCORE_RANK);}
    public static void setScoreRankValue(String scoreRankValue){saveString(SCORE_RANK_VALUE,scoreRankValue);}
    public static String getScoreRankValue(){return getString(SCORE_RANK_VALUE);}

    public static void setServerUrl(String serverUrl){saveString(SERVER_URL,serverUrl);}
    public static String getServerUrl() {
        return getString(SERVER_URL);
    }

    public static void setServerIp(String serverIp){saveString(SERVER_IP,serverIp);}
    public static String getServerIp() {
        return getString(SERVER_IP);
    }

    public static void setServerPort(String serverPort){saveString(SERVER_PORT,serverPort);}
    public static String getServerPort() {
        return getString(SERVER_PORT);
    }

    public static void setDevProvince(String devProvince){saveString(DEV_PROVINCE,devProvince);}
    public static String getDevProvince() {
        return getString(DEV_PROVINCE);
    }

    public static void setDevCity(String devCity){saveString(DEV_CITY,devCity);}
    public static String getDevCity() {
        return getString(DEV_CITY);
    }

    public static void setDevTownShip(String devTownShip){saveString(DEV_TOWN_SHIP,devTownShip);}
    public static String getDevTownShip() {
        return getString(DEV_TOWN_SHIP);
    }

    public static void setAliveCheck(String aliveCheck){saveString(ALIVE_CHECK,aliveCheck);}
    public static String getAliveCheck(){return getString(ALIVE_CHECK);}

    public static void setNoFaceCount(String count){saveString(NO_FACE_COUNT,count);}
    public static String getNoFaceCount(){return getString(NO_FACE_COUNT);}

    public static void setCustomName(String customName){saveString(CUSTOM_NAME,customName);}
    public static String getCustomName(){return getString(CUSTOM_NAME);}

    public static void setMainTittle(String mainTittle){saveString(MAIN_TITTLE,mainTittle);}
    public static String getMainTittle(){return getString(MAIN_TITTLE);}

    public static void setSubTittle(String subTittle){saveString(SUB_TITTLE,subTittle);}
    public static String getSubTittle(){return getString(SUB_TITTLE);}

    public static void setAdsPath(String adsPath){saveString(ADS_PATH,adsPath);}
    public static String getAdsPath(){return getString(ADS_PATH);}

    public static void setDevVolume(String volume){saveString(DEV_VOLUME,volume);}
    public static String getDevVolume(){return getString(DEV_VOLUME);}

    private static String getString(String key){
        return getSharedPreference().getString(key,null);
    }

    private static float getFloat(String key){
        return getSharedPreference().getFloat(key,0);
    }
    private static int getInt(String key){
        return getSharedPreference().getInt(key,0);
    }
    private static void saveString(String key,String value){
        SharedPreferences.Editor editor=getSharedPreference().edit();
        editor.putString(key,value);
        editor.commit();
    }
    private static void saveFloat(String key,float value){
        SharedPreferences.Editor editor=getSharedPreference().edit();
        editor.putFloat(key,value);
        editor.commit();
    }

    private static void saveInt(String key,int value){
        SharedPreferences.Editor editor=getSharedPreference().edit();
        editor.putInt(key,value);
        editor.commit();
    }

    public static void clearUser(){
        setPreferenceNull();
    }
    private static void setPreferenceNull(){

        setSid(null);
        setSession(null);
        setDevSno(null);
        setScoreRank(null);
        setHisFirstId(0);
        setAgeWarning(null);
        setBlackWarning(null);
        setHisLastId(0);
        setAliveCheck(null);
        setScoreRank(null);
        setAgeWarningMIN(null);
        setAgeWarningMAX(null);
        setAdsPath(null);
        setVoiceTips(null);
        setScoreRankValue(null);
        setDevProvince(null);
        setDevCity(null);
        setDevTownShip(null);


    }
    static SharedPreferences getSharedPreference(){
        return FaceCardApplication.getApplication().getSharedPreferences("Door_Setting_Info", Context.MODE_PRIVATE);
    }
}
