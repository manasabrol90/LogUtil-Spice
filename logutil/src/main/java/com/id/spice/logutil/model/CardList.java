package com.id.spice.logutil.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CardList implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("cardMaskNumber")
    @Expose
    private String cardMaskNumber;
    @SerializedName("cardHolderName")
    @Expose
    private String cardHolderName;
    @SerializedName("expiredMonth")
    @Expose
    private String expiredMonth;
    @SerializedName("expiredYear")
    @Expose
    private String expiredYear;
    @SerializedName("cardIssuer")
    @Expose
    private String cardIssuer;

    @SerializedName("isPrimary")
    @Expose
    private String isPrimary;

    @SerializedName("schemeLogo")
    @Expose
    private String schemeLogo;

    public String getSchemeLogo() {
        return schemeLogo;
    }

    public void setSchemeLogo(String schemeLogo) {
        this.schemeLogo = schemeLogo;
    }

    public String getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(String isPrimary) {
        this.isPrimary = isPrimary;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCardMaskNumber() {
        return cardMaskNumber;
    }

    public void setCardMaskNumber(String cardMaskNumber) {
        this.cardMaskNumber = cardMaskNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getExpiredMonth() {
        return expiredMonth;
    }

    public void setExpiredMonth(String expiredMonth) {
        this.expiredMonth = expiredMonth;
    }

    public String getExpiredYear() {
        return expiredYear;
    }

    public void setExpiredYear(String expiredYear) {
        this.expiredYear = expiredYear;
    }

    public String getCardIssuer() {
        return cardIssuer;
    }

    public void setCardIssuer(String cardIssuer) {
        this.cardIssuer = cardIssuer;
    }

}