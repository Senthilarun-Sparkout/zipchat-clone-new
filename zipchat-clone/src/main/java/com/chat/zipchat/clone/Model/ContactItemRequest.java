package com.chat.zipchat.clone.Model;

import com.google.gson.annotations.SerializedName;

public class ContactItemRequest {

    @SerializedName("name")
    private String name;

    @SerializedName("mobile_number")
    private String mobileNumber;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }
}