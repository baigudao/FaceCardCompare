package com.taisau.facecardcompare.model;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Unique;

import java.util.List;

/**
 * Created by Administrator on 2017/3/13 0013.
 * list:[
 {"birthday": 出生日期,"qq": qq号,"live_address": 现居住地,"other": 入库原因,"address": 户籍地,"sex": 性别1男2女,"id_card": 身份证号,"mobile": 手机号,"wechat": 微信,"person_name": 姓名,
 "groupList":
 [{"group_code": 分组代码,"group_id": 分组id,"group_name": 分组名，“valid_time”:到期时间},…],
 "group_type": 分组类型0陌生人1白名单2黑名单,
 "faceList":
 [{"img_url": 模板图地址,"face_id":  模板图id},…],
 "work_address": 工作地址,
 "age": 年龄,
 "email": 邮箱,
 "person_id": 模板id
 }
 ]
 */
@Entity
public class Person {
    @Unique
    @Id
    Long person_id;
    String person_name;
    String id_card;
    String birthday;
    String qq;
    String live_address;
    String other;
    String address;
    String sex;
    String mobile;
    String wechat;
    String work_address;
    String age;
    String email;
    @ToMany(referencedJoinProperty = "personId")
    List<FaceList> faceList;
    @ToMany
    @JoinEntity(
            entity = GroupJoin.class,
            sourceProperty ="person_id",
            targetProperty = "group_id"
    )
    List<GroupList> groupList;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 778611619)
    private transient PersonDao myDao;
    @Generated(hash = 714101464)
    public Person(Long person_id, String person_name, String id_card, String birthday, String qq, String live_address, String other, String address,
            String sex, String mobile, String wechat, String work_address, String age, String email) {
        this.person_id = person_id;
        this.person_name = person_name;
        this.id_card = id_card;
        this.birthday = birthday;
        this.qq = qq;
        this.live_address = live_address;
        this.other = other;
        this.address = address;
        this.sex = sex;
        this.mobile = mobile;
        this.wechat = wechat;
        this.work_address = work_address;
        this.age = age;
        this.email = email;
    }
    @Generated(hash = 1024547259)
    public Person() {
    }
    public Long getPerson_id() {
        return this.person_id;
    }
    public void setPerson_id(Long person_id) {
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
    public String getBirthday() {
        return this.birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public String getQq() {
        return this.qq;
    }
    public void setQq(String qq) {
        this.qq = qq;
    }
    public String getLive_address() {
        return this.live_address;
    }
    public void setLive_address(String live_address) {
        this.live_address = live_address;
    }
    public String getOther() {
        return this.other;
    }
    public void setOther(String other) {
        this.other = other;
    }
    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getSex() {
        return this.sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public String getMobile() {
        return this.mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getWechat() {
        return this.wechat;
    }
    public void setWechat(String wechat) {
        this.wechat = wechat;
    }
    public String getWork_address() {
        return this.work_address;
    }
    public void setWork_address(String work_address) {
        this.work_address = work_address;
    }
    public String getAge() {
        return this.age;
    }
    public void setAge(String age) {
        this.age = age;
    }
    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 53577012)
    public List<FaceList> getFaceList() {
        if (faceList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            FaceListDao targetDao = daoSession.getFaceListDao();
            List<FaceList> faceListNew = targetDao._queryPerson_FaceList(person_id);
            synchronized (this) {
                if (faceList == null) {
                    faceList = faceListNew;
                }
            }
        }
        return faceList;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1860669821)
    public synchronized void resetFaceList() {
        faceList = null;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1850035194)
    public List<GroupList> getGroupList() {
        if (groupList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            GroupListDao targetDao = daoSession.getGroupListDao();
            List<GroupList> groupListNew = targetDao._queryPerson_GroupList(person_id);
            synchronized (this) {
                if (groupList == null) {
                    groupList = groupListNew;
                }
            }
        }
        return groupList;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 114754500)
    public synchronized void resetGroupList() {
        groupList = null;
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2056799268)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPersonDao() : null;
    }
  
}

