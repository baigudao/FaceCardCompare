package com.taisau.facecardcompare.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by Administrator on 2017/3/20 0020.
 */
@Entity
public class WhiteFea {
    @Unique
    @Id
    long person_id;
    String person_name;
    String id_card;
    String img_url;
    String http_url;
    String img_fea;
    @Generated(hash = 15465128)
    public WhiteFea(long person_id, String person_name, String id_card,
            String img_url, String http_url, String img_fea) {
        this.person_id = person_id;
        this.person_name = person_name;
        this.id_card = id_card;
        this.img_url = img_url;
        this.http_url = http_url;
        this.img_fea = img_fea;
    }
    @Generated(hash = 1198061011)
    public WhiteFea() {
    }
    public long getPerson_id() {
        return this.person_id;
    }
    public void setPerson_id(long person_id) {
        this.person_id = person_id;
    }
    public String getPerson_name() {
        return this.person_name;
    }
    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }
    public String getId_card() {
        return this.id_card;
    }
    public void setId_card(String id_card) {
        this.id_card = id_card;
    }
    public String getImg_url() {
        return this.img_url;
    }
    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
    public String getHttp_url() {
        return this.http_url;
    }
    public void setHttp_url(String http_url) {
        this.http_url = http_url;
    }
    public String getImg_fea() {
        return this.img_fea;
    }
    public void setImg_fea(String img_fea) {
        this.img_fea = img_fea;
    }
}
