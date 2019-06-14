package com.chat.zipchat.clone.Model.Payments;

public class GetAmountPojo {

    private String balance;
    private String buying_liabilities;
    private String selling_liabilities;
    private String asset_type;

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getBuying_liabilities() {
        return buying_liabilities;
    }

    public void setBuying_liabilities(String buying_liabilities) {
        this.buying_liabilities = buying_liabilities;
    }

    public String getSelling_liabilities() {
        return selling_liabilities;
    }

    public void setSelling_liabilities(String selling_liabilities) {
        this.selling_liabilities = selling_liabilities;
    }

    public String getAsset_type() {
        return asset_type;
    }

    public void setAsset_type(String asset_type) {
        this.asset_type = asset_type;
    }
}
