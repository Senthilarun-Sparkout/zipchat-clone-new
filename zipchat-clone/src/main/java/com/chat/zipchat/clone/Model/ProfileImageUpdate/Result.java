package com.chat.zipchat.clone.Model.ProfileImageUpdate;

import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("url")
    private String url;

    @SerializedName("type")
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}