package com.taisau.facecardcompare.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2017/5/15 0015.
 */

@Entity
public class ReLoadInfo {
    @Id(autoincrement = true)
    Long id;
    String data_type;
    String id_card;
    String person_name;
    String person_id;
    String card_img_file;
    String face_img_path;
    String score;
    String mistake_status;
    String mistake_value;
    String capture_time;
    @Generated(hash = 1474673042)
    public ReLoadInfo(Long id, String data_type, String id_card, String person_name,
            String person_id, String card_img_file, String face_img_path,
            String score, String mistake_status, String mistake_value,
            String capture_time) {
        this.id = id;
        this.data_type = data_type;
        this.id_card = id_card;
        this.person_name = person_name;
        this.person_id = person_id;
        this.card_img_file = card_img_file;
        this.face_img_path = face_img_path;
        this.score = score;
        this.mistake_status = mistake_status;
        this.mistake_value = mistake_value;
        this.capture_time = capture_time;
    }
    @Generated(hash = 1559606637)
    public ReLoadInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getData_type() {
        return this.data_type;
    }
    public void setData_type(String data_type) {
        this.data_type = data_type;
    }
    public String getId_card() {
        return this.id_card;
    }
    public void setId_card(String id_card) {
        this.id_card = id_card;
    }
    public String getPerson_name() {
        return this.person_name;
    }
    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }
    public String getPerson_id() {
        return this.person_id;
    }
    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }
    public String getCard_img_file() {
        return this.card_img_file;
    }
    public void setCard_img_file(String card_img_file) {
        this.card_img_file = card_img_file;
    }
    public String getFace_img_path() {
        return this.face_img_path;
    }
    public void setFace_img_path(String face_img_path) {
        this.face_img_path = face_img_path;
    }
    public String getScore() {
        return this.score;
    }
    public void setScore(String score) {
        this.score = score;
    }
    public String getMistake_status() {
        return this.mistake_status;
    }
    public void setMistake_status(String mistake_status) {
        this.mistake_status = mistake_status;
    }
    public String getMistake_value() {
        return this.mistake_value;
    }
    public void setMistake_value(String mistake_value) {
        this.mistake_value = mistake_value;
    }
    public String getCapture_time() {
        return this.capture_time;
    }
    public void setCapture_time(String capture_time) {
        this.capture_time = capture_time;
    }
}
