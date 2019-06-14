package com.chat.zipchat.clone.Model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.io.Serializable;

@Entity
public class ResultItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    public Long con_id;
    public Long contact_id;

    @Index(unique = true)
    private String id;
    private String name;
    private String mobile_number;
    private String profile_picture;
    private String status;
    private String isFromContact;
    private boolean isSelected;

    @Generated(hash = 449600386)
    public ResultItem(Long con_id, Long contact_id, String id, String name,
            String mobile_number, String profile_picture, String status,
            String isFromContact, boolean isSelected) {
        this.con_id = con_id;
        this.contact_id = contact_id;
        this.id = id;
        this.name = name;
        this.mobile_number = mobile_number;
        this.profile_picture = profile_picture;
        this.status = status;
        this.isFromContact = isFromContact;
        this.isSelected = isSelected;
    }
    @Generated(hash = 1771746323)
    public ResultItem() {
    }
    public Long getCon_id() {
        return this.con_id;
    }
    public void setCon_id(Long con_id) {
        this.con_id = con_id;
    }
    public Long getContact_id() {
        return this.contact_id;
    }
    public void setContact_id(Long contact_id) {
        this.contact_id = contact_id;
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
    public String getIsFromContact() {
        return this.isFromContact;
    }
    public void setIsFromContact(String isFromContact) {
        this.isFromContact = isFromContact;
    }
    public boolean getIsSelected() {
        return this.isSelected;
    }
    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    
}