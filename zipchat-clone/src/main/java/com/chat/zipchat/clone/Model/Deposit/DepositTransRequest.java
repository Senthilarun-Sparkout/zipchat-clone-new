package com.chat.zipchat.clone.Model.Deposit;

import com.chat.zipchat.clone.Model.CardDetails;

public class DepositTransRequest {

    private String stripeToken;
    private String amount;
    private String user;
    private String xlmAmount;
    private Boolean saveCard;
    private CardDetails card;

    public Boolean getSaveCard() {
        return saveCard;
    }

    public void setSaveCard(Boolean saveCard) {
        this.saveCard = saveCard;
    }

    public String getStripeToken() {
        return stripeToken;
    }

    public void setStripeToken(String stripeToken) {
        this.stripeToken = stripeToken;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getXlmAmount() {
        return xlmAmount;
    }

    public void setXlmAmount(String xlmAmount) {
        this.xlmAmount = xlmAmount;
    }

    public CardDetails getCard() {
        return card;
    }

    public void setCard(CardDetails card) {
        this.card = card;
    }
}
