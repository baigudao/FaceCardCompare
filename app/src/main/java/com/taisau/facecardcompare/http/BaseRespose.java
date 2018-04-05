package com.taisau.facecardcompare.http;

import java.util.ArrayList;

/**
 * Created by ds on 2016/11/24 14:12.
 */

public class BaseRespose<T> {
    public boolean success;
    public String msg;
    public String code;
    public String session;
    public T result;
    public String sid;
    public String data;
    public int count;
    public ArrayList<T> list;
    public double score;
    public int group_id;


    public ArrayList<T> getList() {
        return list;
    }

    public int getCount() {
        return count;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    public String getCode() {
        return code;
    }

    public String getSession() {
        return session;
    }

    public T getResult() {
        return result;
    }

    public String getSid() {
        return sid;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "BaseRespose{" +
                "success=" + success +
                ", msg='" + msg + '\'' +
                ", code='" + code + '\'' +
                ", session='" + session + '\'' +
                ", result=" + result +
                ", sid='" + sid + '\'' +
                ", data='" + data + '\'' +
                ", count=" + count +
                ", list=" + list +
                '}';
    }
}
