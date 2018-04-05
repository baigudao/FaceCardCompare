package com.taisau.facecardcompare.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by Administrator on 2017/3/7 0007.
 */
@Entity
public class Group {
 /* group_id:分组id
    group_name:分组名
    group_code:分组编码
    group_type:分组类型
    group_type_name:分组类型名
    person_count:分组内人员总数*/
    @Unique
    @Id
    private long group_id;
    private String group_name;
    private String group_code;
    private String group_type;
    private String group_type_name;
    private String person_count;
    @Generated(hash = 1798375780)
    public Group(long group_id, String group_name, String group_code,
            String group_type, String group_type_name, String person_count) {
        this.group_id = group_id;
        this.group_name = group_name;
        this.group_code = group_code;
        this.group_type = group_type;
        this.group_type_name = group_type_name;
        this.person_count = person_count;
    }
    @Generated(hash = 117982048)
    public Group() {
    }
    public long getGroup_id() {
        return this.group_id;
    }
    public void setGroup_id(long group_id) {
        this.group_id = group_id;
    }
    public String getGroup_name() {
        return this.group_name;
    }
    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }
    public String getGroup_code() {
        return this.group_code;
    }
    public void setGroup_code(String group_code) {
        this.group_code = group_code;
    }
    public String getGroup_type() {
        return this.group_type;
    }
    public void setGroup_type(String group_type) {
        this.group_type = group_type;
    }
    public String getGroup_type_name() {
        return this.group_type_name;
    }
    public void setGroup_type_name(String group_type_name) {
        this.group_type_name = group_type_name;
    }
    public String getPerson_count() {
        return this.person_count;
    }
    public void setPerson_count(String person_count) {
        this.person_count = person_count;
    }



   
}
