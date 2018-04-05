package com.taisau.facecardcompare.model;

/**
 * Created by Administrator on 2017/3/7 0007.
 */

public class Device {
    private String session;
    private String sid;

    public Device(String session, String sid) {
        this.session = session;
        this.sid = sid;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
