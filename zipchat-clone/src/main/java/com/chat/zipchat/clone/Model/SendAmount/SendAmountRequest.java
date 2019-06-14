package com.chat.zipchat.clone.Model.SendAmount;

public class SendAmountRequest {

    private String sender;
    private String receiver;
    private String amount;
    private String fee;
    private String walletAmount;
    private String walletFee;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
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

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
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
}
