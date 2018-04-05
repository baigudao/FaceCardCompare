//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.cvr.device;

import java.util.Date;

public class IDCardInfo {
    private String _PeopleName = "";
    private String _Sex = "";
    private String _People = "";
    private Date _BirthDay = new Date();
    private String _Addr = "";
    private String _IDCard = "";
    private String _Department = "";
    private String _StartDate = "";
    private String _EndDate = "";
    private byte[] _FpDate = new byte[1024];
    private static byte[] _bmpdata = new byte['韎'];
    private static byte[] _wltdata = new byte[1024];

    public IDCardInfo() {
    }

    public String getPeopleName() {
        return this._PeopleName;
    }

    protected void setPeopleName(String value) {
        this._PeopleName = value;
    }

    protected void setSex(String value) {
        this._Sex = value;
    }

    public String getSex() {
        return this._Sex;
    }

    public String getPeople() {
        return this._People;
    }

    protected void setPeople(String value) {
        this._People = value;
    }

    public Date getBirthDay() {
        return this._BirthDay;
    }

    protected void setBirthDay(Date value) {
        this._BirthDay = value;
    }

    public String getAddr() {
        return this._Addr;
    }

    protected void setAddr(String value) {
        this._Addr = value;
    }

    public String getIDCard() {
        return this._IDCard;
    }

    protected void setIDCard(String value) {
        this._IDCard = value;
    }

    public String getDepartment() {
        return this._Department;
    }

    protected void setDepartment(String value) {
        this._Department = value;
    }

    public String getStrartDate() {
        return this._StartDate;
    }

    protected void setStrartDate(String value) {
        this._StartDate = value;
    }

    public String getEndDate() {
        return this._EndDate;
    }

    protected void setEndDate(String value) {
        this._EndDate = value;
    }

    public byte[] getFpDate() {
        return this._FpDate;
    }

    protected void setFpDate(byte[] value) {
        this._FpDate = value;
    }

    public byte[] getbmpdata() {
        return _bmpdata;
    }

    protected void setbmpdata(byte[] bmpdata) {
        _bmpdata = bmpdata;
    }

    public byte[] getwltdata() {
        return _wltdata;
    }

    protected void setwltdata(byte[] wltdata) {
        _wltdata = wltdata;
    }
}
