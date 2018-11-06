package com.id.spice.logutil.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.id.spice.logutil.network.BaseParserInterface;


public class RespStatus implements BaseParserInterface {

    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("statusDesc")
    @Expose
    private String statusDesc;
    @SerializedName("trxId")
    @Expose
    private String trxId;
    @SerializedName("respTime")
    @Expose
    private long respTime;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }

    public long getRespTime() {
        return respTime;
    }

    public void setRespTime(long respTime) {
        this.respTime = respTime;
    }

    @Override
    public int statusCode() {
        return statusCode;
    }

    @Override
    public String errorMessage() {
        return null;
    }
}