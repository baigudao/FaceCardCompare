package com.taisau.facecardcompare.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
@Entity
public class HistoryList {
    @Unique
    @Id(autoincrement = true)
    Long id;
    @NotNull
    String person_name;
    @NotNull
    String id_card;
    String sex;
    String ethnic;
    String birthday;
    String address;
    String card_release_org;
    String valid_time;
    String face_path;
    String card_path;
    String face_fea_path;
    String card_fea_path;
    Date time;
    String com_status;

    float score;

    @Generated(hash = 136960220)
    public HistoryList(Long id, @NotNull String person_name,
            @NotNull String id_card, String sex, String ethnic, String birthday,
            String address, String card_release_org, String valid_time,
            String face_path, String card_path, String face_fea_path,
            String card_fea_path, Date time, String com_status, float score) {
        this.id = id;
        this.person_name = person_name;
        this.id_card = id_card;
        this.sex = sex;
        this.ethnic = ethnic;
        this.birthday = birthday;
        this.address = address;
        this.card_release_org = card_release_org;
        this.valid_time = valid_time;
        this.face_path = face_path;
        this.card_path = card_path;
        this.face_fea_path = face_fea_path;
        this.card_fea_path = card_fea_path;
        this.time = time;
        this.com_status = com_status;
        this.score = score;
    }

    @Generated(hash = 2007533974)
    public HistoryList() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEthnic() {
        return this.ethnic;
    }

    public void setEthnic(String ethnic) {
        this.ethnic = ethnic;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCard_release_org() {
        return this.card_release_org;
    }

    public void setCard_release_org(String card_release_org) {
        this.card_release_org = card_release_org;
    }

    public String getValid_time() {
        return this.valid_time;
    }

    public void setValid_time(String valid_time) {
        this.valid_time = valid_time;
    }

    public String getFace_path() {
        return this.face_path;
    }

    public void setFace_path(String face_path) {
        this.face_path = face_path;
    }

    public String getCard_path() {
        return this.card_path;
    }

    public void setCard_path(String card_path) {
        this.card_path = card_path;
    }

    public String getFace_fea_path() {
        return this.face_fea_path;
    }

    public void setFace_fea_path(String face_fea_path) {
        this.face_fea_path = face_fea_path;
    }

    public String getCard_fea_path() {
        return this.card_fea_path;
    }

    public void setCard_fea_path(String card_fea_path) {
        this.card_fea_path = card_fea_path;
    }

    public Date getTime() {
        return this.time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getCom_status() {
        return this.com_status;
    }

    public void setCom_status(String com_status) {
        this.com_status = com_status;
    }

    public float getScore() {
        return this.score;
    }

    public void setScore(float score) {
        this.score = score;
    }


    @Override
    public String toString() {
        return "HistoryList{" +
                "id=" + id +
                ", person_name='" + person_name + '\'' +
                ", id_card='" + id_card + '\'' +
                ", sex='" + sex + '\'' +
                ", ethnic='" + ethnic + '\'' +
                ", birthday='" + birthday + '\'' +
                ", address='" + address + '\'' +
                ", card_release_org='" + card_release_org + '\'' +
                ", valid_time='" + valid_time + '\'' +
                ", face_path='" + face_path + '\'' +
                ", card_path='" + card_path + '\'' +
                ", face_fea_path='" + face_fea_path + '\'' +
                ", card_fea_path='" + card_fea_path + '\'' +
                ", time=" + time +
                ", com_status='" + com_status + '\'' +
                ", score=" + score +
                '}';
    }
}
