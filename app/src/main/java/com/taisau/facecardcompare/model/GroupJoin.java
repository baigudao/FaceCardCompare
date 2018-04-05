package com.taisau.facecardcompare.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2017/5/17 0017.
 */
@Entity
public class GroupJoin {
    @Id(autoincrement = true)
    Long id;
    long person_id;
    long group_id;
    @Generated(hash = 519341597)
    public GroupJoin(Long id, long person_id, long group_id) {
        this.id = id;
        this.person_id = person_id;
        this.group_id = group_id;
    }
    @Generated(hash = 1174832034)
    public GroupJoin() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getPerson_id() {
        return this.person_id;
    }
    public void setPerson_id(long person_id) {
        this.person_id = person_id;
    }
    public long getGroup_id() {
        return this.group_id;
    }
    public void setGroup_id(long group_id) {
        this.group_id = group_id;
    }
    public void setId(Long id) {
        this.id = id;
    }
}
