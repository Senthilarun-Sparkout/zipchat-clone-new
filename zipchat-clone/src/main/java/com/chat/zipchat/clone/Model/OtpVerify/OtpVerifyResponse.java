package com.chat.zipchat.clone.Model.OtpVerify;

import com.google.gson.annotations.SerializedName;

public class OtpVerifyResponse {

    @SerializedName("result")
    private Result result;

    @SerializedName("success_message")
    private String successMessage;

    @SerializedName("status")
    private boolean status;

    public void setResult(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return result;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }
}