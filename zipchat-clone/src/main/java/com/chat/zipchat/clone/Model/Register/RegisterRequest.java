package com.chat.zipchat.clone.Model.Register;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {

    @SerializedName("country_code")
    private String countryCode;

    @SerializedName("device_token")
    private String deviceToken;

    @SerializedName("device_type")
    private String deviceType;

    @SerializedName("mobile_number")
    private String mobileNumber;

    @SerializedName("email")
    private String Email;

    @SerializedName("mobile_with_country_code")
    private String MobileWithCountryCode;

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getMobileWithCountryCode() {
        return MobileWithCountryCode;
    }

    public void setMobileWithCountryCode(String mobileWithCountryCode) {
        MobileWithCountryCode = mobileWithCountryCode;
    }


}