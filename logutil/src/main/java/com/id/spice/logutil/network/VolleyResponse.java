package com.id.spice.logutil.network;

public interface VolleyResponse<T> {
    void onResult(T resultModel, ApiEnumeration apiEnumeration);
    void onHttpResultHandler(String errorMessage, ApiEnumeration apiEnumeration, int httpCode);
    void onFailure(String errorMessage, ApiEnumeration apiEnumeration);
}
