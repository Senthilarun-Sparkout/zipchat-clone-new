package com.chat.zipchat.clone.Model.SentTransaction;

public class SentReceiverDetails {

    private String _id;
    private String stellarAddress;
    private String full_name;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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
