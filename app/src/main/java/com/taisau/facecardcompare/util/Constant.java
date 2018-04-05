package com.taisau.facecardcompare.util;

import android.os.Environment;

/**
 * Created by Administrator on 2017/2/22 0022.
 */

public interface Constant {
    String LIB_DIR= Environment.getExternalStorageDirectory().getAbsolutePath()+"/caffe_mobile";
    String FILE_FACE6=LIB_DIR+"/face_GFace6";
    String FILE_FACE7=LIB_DIR+"/face_GFace7";
    String CARD_FEA=LIB_DIR+"/card_fea";
    String CARD_IMG=LIB_DIR+"/card_fea";
    String FACE_FEA=LIB_DIR+"/face_fea";
    String FACE_IMG=LIB_DIR+"/face_img";
    String WHITE_IMG=LIB_DIR+"/white_img";
    String WHITE_FEA=LIB_DIR+"/white_fea";
    String SOFT_NAME="FaceCompare_Android";
    String ADS_IMG = LIB_DIR+"/ads_img";
    String []WEATHER_ARRAY=new String[]{"中雨","暴雪","暴雨","大暴雨","大雪","大雨","冻雨","多云","浮尘","雷阵雨","霾","晴","沙尘暴","特大暴雨","特强沙尘暴"
                                        ,"雾","小雪","小雨","扬尘","阴","雨夹雪","阵雪","阵雨","中雪","暴雨转大暴雨","大暴雨转特大暴雨","大雪转暴雪","大雨转暴雨"
                                        ,"雷阵雨伴有冰雹","小雪转中雪","小雨转中雨","中雪转大雪","中雨转大雨"};
}
