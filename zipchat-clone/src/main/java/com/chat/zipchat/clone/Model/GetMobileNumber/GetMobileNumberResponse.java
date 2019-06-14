package com.chat.zipchat.clone.Model.GetMobileNumber;

public class GetMobileNumberResponse {

    private String _id;
    private String full_name;
    private String profile_picture;
    private String status;
    private String mobile_number;
    private String mobile_with_country_code;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getMobile_with_country_code() {
        return mobile_with_country_code;
    }

    public void setMobile_with_country_code(String mobile_with_country_code) {
        this.mobile_with_country_code = mobile_with_country_code;
    }
}
