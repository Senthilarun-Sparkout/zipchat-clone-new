package com.chat.zipchat.clone.Model.OtpVerify;

public class OtpVerifyRequest {

    private String user;
    private String otp;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
