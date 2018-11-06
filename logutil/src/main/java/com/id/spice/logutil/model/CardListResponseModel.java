package com.id.spice.logutil.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.id.spice.logutil.network.BaseParserInterface;

import java.util.ArrayList;

public class CardListResponseModel implements BaseParserInterface {

    @SerializedName("respStatus")
    @Expose
    private RespStatus respStatus;
    @SerializedName("cardList")
    @Expose
    private ArrayList<CardList> cardList = null;

    public RespStatus getRespStatus() {
        return respStatus;
    }

    public void setRespStatus(RespStatus respStatus) {
        this.respStatus = respStatus;
    }

    public ArrayList<CardList> getCardList() {
        return cardList;
    }

    public void setCardList(ArrayList<CardList> cardList) {
        this.cardList = cardList;
    }

    @Override
    public int statusCode() {
        return getRespStatus().getStatusCode();
    }

    @Override
    public String errorMessage() {
        return null;
    }
}