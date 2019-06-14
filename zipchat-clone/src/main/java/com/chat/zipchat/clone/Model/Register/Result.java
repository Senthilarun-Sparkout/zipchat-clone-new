package com.chat.zipchat.clone.Model.Register;


import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("authorization")
    private String authorization;

    @SerializedName("mobile_verified")
    private int mobileVerified;

    @SerializedName("__v")
    private int V;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("otp")
    private int otp;

    @SerializedName("_id")
    private String id;

    @SerializedName("stellarAddress")
    private String stellarAddress;

    @SerializedName("stellarSeed")
    private String stellarSeed;



    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setMobileVerified(int mobileVerified) {
        this.mobileVerified = mobileVerified;
    }

    public int getMobileVerified() {
        return mobileVerified;
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

    public String getStellarAddress() {
        return stellarAddress;
    }

    public void setStellarAddress(String stellarAddress) {
        this.stellarAddress = stellarAddress;
    }

    public String getStellarSeed() {
        return stellarSeed;
    }

    public void setStellarSeed(String stellarSeed) {
        this.stellarSeed = stellarSeed;
    }

}