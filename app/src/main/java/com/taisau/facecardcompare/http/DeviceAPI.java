package com.taisau.facecardcompare.http;

import com.taisau.facecardcompare.model.Device;
import com.taisau.facecardcompare.model.UpgradePKG;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

import static com.taisau.facecardcompare.http.Config.CATEGORY;
import static com.taisau.facecardcompare.http.Config.DEV_SNO;
import static com.taisau.facecardcompare.http.Config.SESSION;
import static com.taisau.facecardcompare.http.Config.SID;
import static com.taisau.facecardcompare.http.Config.SIGN_TYPE;

/**
 * Created by Administrator on 2017/3/7 0007.
 */

public interface DeviceAPI {
    @FormUrlEncoded
    @POST
    Observable<BaseRespose<Device>> deviceLogin(@Url String server,@Field(DEV_SNO) String dev_sno, @Field(SIGN_TYPE) String sign_type);

    @FormUrlEncoded
    @POST
    Observable<BaseRespose<Device>> deviceLogout(@Url String server,@Field(SID) String sid);

    @POST("http://php.weather.sina.com.cn/iframe/index/w_cl.php?code=js&day=0&city=&dfc=1&charset=utf-8")
    Call<ResponseBody>getWeatherInfo();

    @FormUrlEncoded
    @POST
    Observable<BaseRespose<UpgradePKG>> updateDevice(@Url String server,@Field(SID) String sid, @Field(SESSION) String session, @Field(CATEGORY) String category);

    @Streaming
    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);
}
