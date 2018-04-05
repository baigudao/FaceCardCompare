package com.taisau.facecardcompare.http;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

import static com.taisau.facecardcompare.http.Config.CAPTURE_TIME;
import static com.taisau.facecardcompare.http.Config.DATA_TYPE;
import static com.taisau.facecardcompare.http.Config.ID_CARD;
import static com.taisau.facecardcompare.http.Config.MISTAKE_STATUS;
import static com.taisau.facecardcompare.http.Config.MISTAKE_VALUE;
import static com.taisau.facecardcompare.http.Config.PERSON_ID;
import static com.taisau.facecardcompare.http.Config.PERSON_NAME;
import static com.taisau.facecardcompare.http.Config.SCORE;
import static com.taisau.facecardcompare.http.Config.SESSION;
import static com.taisau.facecardcompare.http.Config.SID;

/**
 * Created by Administrator on 2017/4/18 0018.
 */

public interface UpdateInfoAPI {

    /**
     sid	String	签名值	签名值	不为空
     session	String	session值	session值	不为空
     face_img	IMG	正脸图	抓拍人脸图片
     side_body_img	IMG	侧脸图	抓拍侧身图片
     whole_body_img	IMG	全身图	抓拍全身图片
     veplate_img	IMG	车牌图	抓拍车牌图片
     panorama_img	IMG	全景图	抓拍全景图
     base_face_img	IMG	模板图	比对第三方模板图
     face_id	int	模板id	比对服务器存在的模板图id
     score	double	比对分值	比对分值结果
     address	String	抓拍地址	抓拍地址
     capture_time	String	抓拍时间	抓拍时间
     person_id	int	人员id	服务器存在的人员id
     person_name	String	人员姓名	服务器不存在的人员姓名
     id_card	String	人员身份证号	服务器不存在的人员身份证号
     addr	String	户籍地/身份证上住址	服务器不存在的人员
     data_type	int	数据类型	见上描述
     veplate_num	String	车牌号码	车牌号
     veplate_color	int	车牌颜色
     mistake_status	int	确认状态	见上描述
     mistake_value	String	确认状态详情	确认结果详细描述
     */
    @Multipart
    @POST
    Call<ResponseBody> UpdateData(@Url String server, @Part(SID) RequestBody sid, @Part(SESSION) RequestBody session,
                                  @Part MultipartBody.Part card_img_file,
                                  @Part MultipartBody.Part face_img,
                                  @Part(SCORE)RequestBody score,
                                  @Part(DATA_TYPE) RequestBody data_type,
                                  @Part(PERSON_NAME) RequestBody person_name, @Part(PERSON_ID) RequestBody person_id, @Part(ID_CARD) RequestBody id_card,
                                  @Part(MISTAKE_STATUS)RequestBody mistake_status, @Part(MISTAKE_VALUE)RequestBody mistake_values,@Part(CAPTURE_TIME) RequestBody capture_time);
}
