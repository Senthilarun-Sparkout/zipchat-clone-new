package com.chat.zipchat.clone.Model.ReceiveTransaction;

public class ReceiveTransListPojo {

    private String _id;
    private String receiver;
    private String amount;
    private String currency;
    private String fee;
    private String hash;
    private String walletAmount;
    private String walletFee;
    private String createdTs;
    public ReceiveSenderDetails sender;

    public String get_id() {

        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(String walletAmount) {
        this.walletAmount = walletAmount;
    }

    public String getWalletFee() {
        return walletFee;
    }

    public void setWalletFee(String walletFee) {
        this.walletFee = walletFee;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public ReceiveSenderDetails getSender() {
        return sender;
    }

    public void setSender(ReceiveSenderDetails sender) {
        this.sender = sender;
    }
}
