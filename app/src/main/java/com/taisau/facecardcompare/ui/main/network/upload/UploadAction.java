package com.taisau.facecardcompare.ui.main.network.upload;

import com.taisau.facecardcompare.FaceCardApplication;
import com.taisau.facecardcompare.http.AutoLogin;
import com.taisau.facecardcompare.http.NetClient;
import com.taisau.facecardcompare.model.ReLoadInfo;
import com.taisau.facecardcompare.ui.main.network.MainNet;
import com.taisau.facecardcompare.util.Preference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/12/28 0028.
 */

public class UploadAction {
    private MainNet net;
    public static volatile boolean isRun=false;
    public UploadAction(MainNet net) {
        this.net=net;
        if (!isRun) {
            isRun=true;
            uploadFile();
        }
    }

    private void uploadFile() {
        Thread uploadFileThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    ReLoadInfo reLoadInfo = FaceCardApplication.getApplication().getDaoSession().getReLoadInfoDao().queryBuilder().limit(1).list().get(0);
                    File face=new File(reLoadInfo.getFace_img_path());
                    File card=new File(reLoadInfo.getCard_img_file());
                    if (!face.exists())
                    {
                        FaceCardApplication.getApplication().getDaoSession().getReLoadInfoDao().deleteByKey(reLoadInfo.getId());
                        if (FaceCardApplication.getApplication().getDaoSession().getReLoadInfoDao().count() > 0) {
                            uploadFile();
                        }
                        return;
                    }
                    RequestBody requestFile1 = RequestBody.create(MediaType.parse("multipart/form-data"),face);
                    MultipartBody.Part body1 = MultipartBody.Part.createFormData("base_face_img", "base_face_img.jpg", requestFile1);
                    RequestBody requestFile2 = RequestBody.create(MediaType.parse("multipart/form-data"), card);
                    MultipartBody.Part body2 = MultipartBody.Part.createFormData("face_img", "face_img.jpg", requestFile2);
                    MediaType textType = MediaType.parse("text/plain");
                    RequestBody sidBody = RequestBody.create(textType, Preference.getSid());
                    RequestBody sessionBody = RequestBody.create(textType, Preference.getSession());
                    RequestBody dataTypeBody = RequestBody.create(textType, reLoadInfo.getData_type());
                    RequestBody idCardBody = RequestBody.create(textType, reLoadInfo.getId_card());
                    RequestBody personNameBody = RequestBody.create(textType, reLoadInfo.getPerson_name());
                    RequestBody personIdBody = RequestBody.create(textType, reLoadInfo.getPerson_id());
                    RequestBody scoreBody = RequestBody.create(textType, reLoadInfo.getScore());
                    RequestBody mistakeStatusBody = RequestBody.create(textType, reLoadInfo.getMistake_status());
                    RequestBody mistakeValueBody = RequestBody.create(textType, reLoadInfo.getMistake_value());
                    RequestBody captureTimeBody = RequestBody.create(textType, reLoadInfo.getCapture_time());
                    NetClient.getInstance().getUpdateInfoAPI().UpdateData(Preference.getServerUrl()
                                    + "hisFaceData/insertHisFaceData", sidBody, sessionBody, body1, body2,
                            scoreBody, dataTypeBody, personNameBody, personIdBody,
                            idCardBody, mistakeStatusBody, mistakeValueBody, captureTimeBody).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String msg = "";
                                if (response.body() != null)
                                    msg = response.body().string();
                                else {
                                    return;
                                }
                                JSONObject object = new JSONObject(msg);
                                if (!object.getBoolean("success")) {
                                    if (object.getInt("code") == 40005 || object.getInt("code") == 50004 || object.getInt("code") == 40004)
                                        AutoLogin.autologin(net);
                                } else {
                                        FaceCardApplication.getApplication().getDaoSession().getReLoadInfoDao().deleteByKey(reLoadInfo.getId());
                                    if (FaceCardApplication.getApplication().getDaoSession().getReLoadInfoDao().count() > 0) {
                                     uploadFile();
                                    }
                                    else
                                        isRun=false;
                                }
                            } catch (NullPointerException | IOException | JSONException e) {
                                e.printStackTrace();
                                isRun=false;
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                            isRun=false;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    isRun=false;
                }
            }
        });
        uploadFileThread.start();
    }
}
