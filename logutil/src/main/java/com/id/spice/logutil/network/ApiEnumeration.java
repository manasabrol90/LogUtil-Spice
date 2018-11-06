package com.id.spice.logutil.network;

import com.android.volley.Request;
import com.id.spice.logutil.ApiConstants;
import com.id.spice.logutil.model.CardListResponseModel;

/**
 * Created by Manas on 09-01-2018.
 */

public enum ApiEnumeration implements ApiInterface {

    RU_LIST_CARD(ApiConstants.CORE_HOST_URL + "webapi/paypro/v1/", Request.Method.GET, CardListResponseModel.class, true);

    private String url;
    private int apiType;
    private Class<? extends BaseParserInterface> referenceClass;
    private boolean isEditURL;
    private String postFixURL;

    ApiEnumeration(String url, int apiType, Class<? extends BaseParserInterface> referenceClass) {
        this.url = url;
        this.apiType = apiType;
        this.referenceClass = referenceClass;
    }

    ApiEnumeration(String url, int apiType, Class<? extends BaseParserInterface> referenceClass, boolean isEditURL) {
        this.url = url;
        this.apiType = apiType;
        this.referenceClass = referenceClass;
        this.isEditURL = isEditURL;
    }

    public String getApiServiceName() {
        if (isEditURL && postFixURL != null) {
            return url + postFixURL;
        } else {
            return url;
        }
    }

    public int getApiType() {
        return apiType;
    }

    public Class<? extends BaseParserInterface> getReferenceClass() {
        return referenceClass;
    }

    public void setPostFixURL(String postFixURL) {
        this.postFixURL = postFixURL;
    }

}
