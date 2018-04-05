package com.taisau.facecardcompare.model;

/**
 * Created by Administrator on 2017/5/3 0003.
 */

public class UpgradePKG {
    /**
     file_name:文件名,
     file_old_name:文件原名,
     category:程序包类别,
     version:版本号,
     file_size:文件大小,
     create_time:上传时间,
     file_path:下载地址,
     remark:文件说明
     */
    String file_name;
    String file_old_name;
    String category;
    String version;
    String file_size;
    String create_time;
    String file_path;
    String remark;

    public UpgradePKG(String file_name, String file_old_name, String category, String version, String file_size, String create_time, String file_path, String remark) {
        this.file_name = file_name;
        this.file_old_name = file_old_name;
        this.category = category;
        this.version = version;
        this.file_size = file_size;
        this.create_time = create_time;
        this.file_path = file_path;
        this.remark = remark;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_old_name() {
        return file_old_name;
    }

    public void setFile_old_name(String file_old_name) {
        this.file_old_name = file_old_name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
