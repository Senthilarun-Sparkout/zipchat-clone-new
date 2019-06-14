package com.chat.zipchat.clone.Model.DepositTrans;

public class DepositTranssResponce {

    DepositResponseDetails data;
    private String fiat_amount;

    public DepositResponseDetails getData() {
        return data;
    }

    public void setData(DepositResponseDetails data) {
        this.data = data;
    }

    public String getFiat_amount() {
        return fiat_amount;
    }

    public void setFiat_amount(String fiat_amount) {
        this.fiat_amount = fiat_amount;
    }
}
