package com.chat.zipchat.clone.Model.OtpVerify;

import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("authorization")
    private String authorization;

    @SerializedName("country_code")
    private String countryCode;

    @SerializedName("mobile_verified")
    private int mobileVerified;

    @SerializedName("device_token")
    private String deviceToken;

    @SerializedName("__v")
    private int V;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("device_type")
    private String deviceType;

    @SerializedName("otp")
    private int otp;

    @SerializedName("_id")
    private String id;

    @SerializedName("mobile_number")
    private String mobileNumber;

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setMobileVerified(int mobileVerified) {
        this.mobileVerified = mobileVerified;
    }

    public int getMobileVerified() {
        return mobileVerified;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setV(int V) {
        this.V = V;
    }

    public int getV() {
        return V;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }

    public int getOtp() {
        return otp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

}