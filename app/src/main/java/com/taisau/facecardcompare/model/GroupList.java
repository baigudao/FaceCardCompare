package com.taisau.facecardcompare.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2017/5/17 0017.
 */

/**
 * {"group_code":"bb1","create_time":"2017-05-12 13:47:51",
 * "text_warning":0,"group_id":121,"group_name":"白名单11",
 * "is_valid":1,"defence_level_name":"禁止外出",
 * "defence_level":3,"group_type":"white_group","voice_warning":0}
 */
@Entity
public class GroupList {
    @Unique
    @Id
    Long group_id;
    String group_code;
    String create_time;
    String text_warning;
    String group_name;
    String is_valid;
    String defence_level_name;
    String defence_level;
    String group_type;
    String voice_warning;
    @Generated(hash = 705066980)
    public GroupList(Long group_id, String group_code, String create_time,
            String text_warning, String group_name, String is_valid,
            String defence_level_name, String defence_level, String group_type,
            String voice_warning) {
        this.group_id = group_id;
        this.group_code = group_code;
        this.create_time = create_time;
        this.text_warning = text_warning;
        this.group_name = group_name;
        this.is_valid = is_valid;
        this.defence_level_name = defence_level_name;
        this.defence_level = defence_level;
        this.group_type = group_type;
        this.voice_warning = voice_warning;
    }
    @Generated(hash = 856125526)
    public GroupList() {
    }
    public Long getGroup_id() {
        return this.group_id;
    }
    public void setGroup_id(Long group_id) {
        this.group_id = group_id;
    }
    public String getGroup_code() {
        return this.group_code;
    }
    public void setGroup_code(String group_code) {
        this.group_code = group_code;
    }
    public String getCreate_time() {
        return this.create_time;
    }
    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
    public String getText_warning() {
        return this.text_warning;
    }
    public void setText_warning(String text_warning) {
        this.text_warning = text_warning;
    }
    public String getGroup_name() {
        return this.group_name;
    }
    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }
    public String getIs_valid() {
        return this.is_valid;
    }
    public void setIs_valid(String is_valid) {
        this.is_valid = is_valid;
    }
    public String getDefence_level_name() {
        return this.defence_level_name;
    }
    public void setDefence_level_name(String defence_level_name) {
        this.defence_level_name = defence_level_name;
    }
    public String getDefence_level() {
        return this.defence_level;
    }
    public void setDefence_level(String defence_level) {
        this.defence_level = defence_level;
    }
    public String getGroup_type() {
        return this.group_type;
    }
    public void setGroup_type(String group_type) {
        this.group_type = group_type;
    }
    public String getVoice_warning() {
        return this.voice_warning;
    }
    public void setVoice_warning(String voice_warning) {
        this.voice_warning = voice_warning;
    }

}
