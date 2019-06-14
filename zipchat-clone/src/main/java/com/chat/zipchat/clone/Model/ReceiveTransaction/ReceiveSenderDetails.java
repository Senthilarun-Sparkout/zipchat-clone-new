package com.chat.zipchat.clone.Model.ReceiveTransaction;

public class ReceiveSenderDetails {

    private String _id;
    private String mobile_number;
    private String stellarAddress;
    private String full_name;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getStellarAddress() {
        return stellarAddress;
    }

    public void setStellarAddress(String stellarAddress) {
        this.stellarAddress = stellarAddress;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }
}
