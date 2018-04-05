package com.taisau.facecardcompare.model;

/**
 * Created by Administrator on 2017/6/9 0009.
 */

public class WFIndex {
    long face_id;
    long person_id;
    float [] fea;
    String face_path;

    public WFIndex(long face_id, long person_id, float[] fea, String face_path) {
        this.face_id = face_id;
        this.person_id = person_id;
        this.fea = fea;
        this.face_path = face_path;
    }

    public long getFace_id() {
        return face_id;
    }

    public void setFace_id(long face_id) {
        this.face_id = face_id;
    }

    public long getPerson_id() {
        return person_id;
    }

    public void setPerson_id(long person_id) {
        this.person_id = person_id;
    }

    public float[] getFea() {
        return fea;
    }

    public void setFea(float[] fea) {
        this.fea = fea;
    }

    public String getFace_path() {
        return face_path;
    }

    public void setFace_path(String face_path) {
        this.face_path = face_path;
    }
}
