package com.chat.zipchat.clone.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContactRequest {

    @SerializedName("contact")
    private List<ContactItemRequest> contact;

    @SerializedName("device_type")
    private String deviceType;

    public void setContact(List<ContactItemRequest> contact) {
        this.contact = contact;
    }

    public List<ContactItemRequest> getContact() {
        return contact;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceType() {
        return deviceType;
    }
}