package com.taisau.facecardcompare.http;

import com.taisau.facecardcompare.util.Preference;

/**
 * Created by Administrator on 2017/3/7 0007.
 */

public interface Config {
    //String SERVICE_URL="http://192.168.2.7:8080/FaceNew/";
    String SERVICE_URL= Preference.getServerUrl();
    String GROUP_URL=SERVICE_URL+"group/";
    String USER_URL=SERVICE_URL+"user/";
    String SID = "sid";
    String SESSION = "session";
    String PERSON_ID_LIST="personIdList";
    String START="start";
    String LIMIT="limit";

    String CITY="city";
    String PASSWORD="password";
    String DAY="day";

    String GROUP_TYPE= "group_type";
    String GROUP_ID= "group_id";
    String GROUP_CODE="group_code";

    String BLACK_GROUP="black_group";
    String WHITE_GROUP="white_group";

    String SIGN_TYPE = "sign_type";
    String DEV_SIGN = "dev_sign";
    String DEV_SNO = "dev_sno";

    String DATA_TYPE="data_type";
    String ID_CARD="id_card";
    String PERSON_NAME="person_name";
    String PERSON_ID="person_id";
    String CARD_IMG_FILE="card_img_file";
    String FACE_IMG="face_img";
    String SCORE="score";
    String MISTAKE_STATUS="mistake_status";
    String MISTAKE_VALUE="mistake_value";
    String CAPTURE_TIME="capture_time";
    String CATEGORY="category";

}
