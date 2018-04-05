package com.taisau.facecardcompare.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2017/5/17 0017.
 */
@Entity
public class FaceList {
    @Unique
    @Id
    Long face_id;
    String img_url;
    long personId;
    String img_path;
    String fea_path;
    @Generated(hash = 695433149)
    public FaceList(Long face_id, String img_url, long personId, String img_path,
            String fea_path) {
        this.face_id = face_id;
        this.img_url = img_url;
        this.personId = personId;
        this.img_path = img_path;
        this.fea_path = fea_path;
    }
    @Generated(hash = 1373281515)
    public FaceList() {
    }
    public Long getFace_id() {
        return this.face_id;
    }
    public void setFace_id(Long face_id) {
        this.face_id = face_id;
    }
    public String getImg_url() {
        return this.img_url;
    }
    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
    public long getPersonId() {
        return this.personId;
    }
    public void setPersonId(long personId) {
        this.personId = personId;
    }
    public String getImg_path() {
        return this.img_path;
    }
    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }
    public String getFea_path() {
        return this.fea_path;
    }
    public void setFea_path(String fea_path) {
        this.fea_path = fea_path;
    }

}
