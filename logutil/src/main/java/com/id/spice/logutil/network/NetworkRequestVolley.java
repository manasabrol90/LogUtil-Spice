package com.id.spice.logutil.network;

import android.content.Context;

import org.json.JSONException;

/**
 * Created by Manas Abrol
 */

public class NetworkRequestVolley extends ServiceUtility {

    private Context mContext;
    private VolleyResponse mListener;

    public NetworkRequestVolley(VolleyResponse resultContext, Context context) {
        mContext = context;
        mListener = resultContext;
    }

    public <T> void sendRequest(ApiEnumeration apiEnumeration, ApiMarkerInterface commonServiceRequestModel,
                                boolean isProgressShow, String message) throws JSONException {
        sendRequest(mContext, mListener, apiEnumeration, commonServiceRequestModel, isProgressShow, message);
    }

    @Override
    protected void handleHttpSuccessDataFromServer(BaseParserInterface parsedObject, String response, ApiMarkerInterface commonServiceRequestModel,
                                                   VolleyResponse listener, ApiEnumeration apiEnumeration,
                                                   Boolean isShowDialog, String loaderMessage) throws JSONException {
        if (listener != null) {
            handleDefaultScenario(parsedObject, commonServiceRequestModel, listener, apiEnumeration, isShowDialog, loaderMessage);
        }
    }
}