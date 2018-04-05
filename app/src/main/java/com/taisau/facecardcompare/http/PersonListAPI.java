package com.taisau.facecardcompare.http;

import com.taisau.facecardcompare.model.Person;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;

import static com.taisau.facecardcompare.http.Config.LIMIT;
import static com.taisau.facecardcompare.http.Config.PERSON_ID_LIST;
import static com.taisau.facecardcompare.http.Config.SESSION;
import static com.taisau.facecardcompare.http.Config.SID;
import static com.taisau.facecardcompare.http.Config.START;

/**
 * Created by Administrator on 2017/3/7 0007.
 */

public interface PersonListAPI {
    @FormUrlEncoded
    @POST
    Observable<BaseRespose<Person>>getSendPerson(@Url String server,@Field(SID) String sid, @Field(SESSION) String session);

    @FormUrlEncoded
    @POST
    Observable<BaseRespose<Person>>getSendPerson(@Url String server,@Field(SID) String sid, @Field(SESSION) String session,@Field(PERSON_ID_LIST) String person_id_list,@Field(START) String start,@Field(LIMIT) String limit);
    @FormUrlEncoded
    @POST
    Call<ResponseBody>getUpdatePersonInfo(@Url String server,@Field(SID) String sid, @Field(SESSION) String session);

    @POST
    Call<ResponseBody>getPersonFile(@Url String fileUrl);

    @POST
    Call<ResponseBody>getImg(@Url String imgUrl);
}
