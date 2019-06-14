package com.chat.zipchat.clone.Model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class GroupMember {

    public Long grp_id;

    private String id;
    private String name;
    private String mobile_number;
    private String profile_picture;
    private String status;
    private String isAdmin;

    @Generated(hash = 1591260569)
    public GroupMember(Long grp_id, String id, String name, String mobile_number,
                       String profile_picture, String status, String isAdmin) {
        this.grp_id = grp_id;
        this.id = id;
        this.name = name;
        this.mobile_number = mobile_number;
        this.profile_picture = profile_picture;
        this.status = status;
        this.isAdmin = isAdmin;
    }

    @Generated(hash = 1668463032)
    public GroupMember() {
    }

    public Long getGrp_id() {
        return this.grp_id;
    }

    public void setGrp_id(Long grp_id) {
        this.grp_id = grp_id;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile_number() {
        return this.mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getProfile_picture() {
        return this.profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsAdmin() {
        return this.isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

}