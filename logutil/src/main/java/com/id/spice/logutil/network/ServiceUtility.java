package com.id.spice.logutil.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.id.spice.logutil.ApiConstants;
import com.id.spice.logutil.R;
import com.id.spice.logutil.utilities.GenericUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Manas on 10-01-2018.
 */

public abstract class ServiceUtility {

    private int mInitialTimeoutMS = 120 * 1000;

    protected boolean isNetworkAvailable(Context context, ApiEnumeration apiRequestCode, VolleyResponse listener) {
        if (GenericUtils.haveNetworkConnection(context)) {
            return true;
        } else {
            if (listener != null) {
                String message = context.getString(R.string.internet_check);
                listener.onFailure(message, apiRequestCode);
            }
            return false;
        }
    }

    protected boolean isNetworkNotOnProxy(Context context, ApiEnumeration apiEnumeration, VolleyResponse listener) {
        if (GenericUtils.checkIfRunningOnProxy(URI.create(apiEnumeration.getApiServiceName()))) {
            if (listener != null) {
                String message = context.getString(R.string.proxy_check_message);
                listener.onFailure(message, apiEnumeration);
            }
            return false;
        } else {
            return true;
        }
    }

    protected <T> void sendRequest(Context context, VolleyResponse listener, ApiEnumeration apiEnumeration,
                                   ApiMarkerInterface commonServiceRequestModel,
                                   boolean isProgressShow, String message) throws JSONException {
        if (isNetworkAvailable(context, apiEnumeration, listener)) {
            if (isProgressShow) {
                GenericUtils.displayProgressDialog(context);
            }
            try {
                switch (apiEnumeration.getApiType()) {
                    case Request.Method.POST:
                        sendPostRequest(context, listener, commonServiceRequestModel, apiEnumeration, isProgressShow, message);
                        break;
                    case Request.Method.GET:
                        sendGetRequest(context, listener, apiEnumeration, isProgressShow, message);
                        break;
                }
            } catch (Exception e) {
                Log.e("", e.getMessage());
            }
        }
    }

    protected <T> void sendPostRequest(final Context mContext, final VolleyResponse listener,
                                       final ApiMarkerInterface commonServiceRequestModel,
                                       final ApiEnumeration apiEnumeration, final Boolean isShowDialog,
                                       final String loaderMessage) throws JSONException {
        try {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(apiEnumeration.getApiType(),
                    apiEnumeration.getApiServiceName(), getJsonObjectFromPojo(commonServiceRequestModel),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (listener != null) {
                                String receivedResponse = response.toString();
                                Log.d("ApiDetails", fetchApiDetails(response, apiEnumeration, commonServiceRequestModel, mContext));
                                onResponseReceived(mContext, ResponseType.SUCCESS, receivedResponse, null,
                                        commonServiceRequestModel, listener, apiEnumeration, isShowDialog, loaderMessage);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String locationUrl = null;
                    if (listener != null) {
                        if (error.networkResponse != null && error.networkResponse.statusCode == 302) {
                            Map<String, String> result = error.networkResponse.headers;
                            for (Map.Entry<String, String> entry : result.entrySet()) {
                                if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("Location")) {
                                    locationUrl = entry.getValue();
                                }
                            }
                        }

                        String apiDetails = "Requested Url: \n" + apiEnumeration.getApiServiceName()
                                + "\n\n" + "Requested Body: \n" + getJsonObjectFromPojo(commonServiceRequestModel)
                                + "\n\n" + "Headers: \n" + getRequestHeaders(mContext, apiEnumeration)
                                + "\n\n" + "Response Received: \n" + error.toString();
                        Log.d("ApiDetails", apiDetails);

                        if (!TextUtils.isEmpty(locationUrl)) {
                            Log.d("ApiDetails", "Location URL:\n" + locationUrl);
                            listener.onHttpResultHandler(locationUrl, apiEnumeration, error.networkResponse.statusCode);
                        } else {
                            onResponseReceived(mContext, ResponseType.FAILED, null, error, commonServiceRequestModel, listener,
                                    apiEnumeration, isShowDialog, loaderMessage);
                        }
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return getRequestHeaders(mContext, apiEnumeration);
                }
            };
            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(mInitialTimeoutMS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            jsObjRequest.setShouldCache(false);

            addRequestQueue(mContext, jsObjRequest);
        } catch (Exception e) {
            Log.e("", e.getMessage());
        }
    }

    @NonNull
    private String fetchApiDetails(JSONObject response, ApiEnumeration apiEnumeration, ApiMarkerInterface commonServiceRequestModel, Context mContext) {
        return "Requested Url: \n" + apiEnumeration.getApiServiceName()
                + "\n\n" + "Requested Body: \n" + getJsonObjectFromPojo(commonServiceRequestModel)
                + "\n\n" + "Headers: \n" + getRequestHeaders(mContext, apiEnumeration)
                + "\n\n" + "Response Received: \n" + response.toString();
    }

    private void addRequestQueue(Context pContext, Request jsObjRequest) {
        RequestQueue requestQueue = null;

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
//                && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
        HttpStack stack = null;
        try {
            stack = new HurlStack(null, new TLSSocketFactory()) {
                @Override
                protected HttpURLConnection createConnection(URL url) throws IOException {
                    HttpURLConnection connection = super.createConnection(url);
                    connection.setInstanceFollowRedirects(false);
                    return connection;
                }
            };
        } catch (KeyManagementException e) {
            Log.d("Your Wrapper Class", "Could not create new stack for TLS v1.2");
            stack = new HurlStack() {
                @Override
                protected HttpURLConnection createConnection(URL url) throws IOException {
                    HttpURLConnection connection = super.createConnection(url);
                    connection.setInstanceFollowRedirects(false);
                    return connection;
                }
            };
        } catch (NoSuchAlgorithmException e) {
            Log.d("Your Wrapper Class", "Could not create new stack for TLS v1.2");
            stack = new HurlStack() {
                @Override
                protected HttpURLConnection createConnection(URL url) throws IOException {
                    HttpURLConnection connection = super.createConnection(url);
                    connection.setInstanceFollowRedirects(false);
                    return connection;
                }
            };
        }
        requestQueue = Volley.newRequestQueue(pContext, stack);
//        } else {
//            requestQueue = Volley.newRequestQueue(pContext);
//        }
        if (requestQueue != null) {
            requestQueue.add(jsObjRequest);
        }
    }

    protected void sendGetRequest(final Context mContext, final VolleyResponse listener,
                                  final ApiEnumeration apiEnumeration, final Boolean isShowDialog, final String loaderMessage) throws JSONException {
        StringRequest jsObjRequest = new StringRequest(apiEnumeration.getApiServiceName(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (listener != null) {
                            String apiDetails = "Requested Url: \n" + apiEnumeration.getApiServiceName()
                                    + "\n\n" + "Requested Body: \n" + ""
                                    + "\n\n" + "Headers: \n" + getRequestHeaders(mContext, apiEnumeration)
                                    + "\n\n" + "Response Received: \n" + response.toString();
                            Log.d("ApiDetails", apiDetails);
                            onResponseReceived(mContext, ResponseType.SUCCESS, response, null,
                                    null, listener, apiEnumeration, isShowDialog, loaderMessage);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (listener != null) {
                    String apiDetails = "Requested Url: \n" + apiEnumeration.getApiServiceName()
                            + "\n\n" + "Requested Body: \n" + ""
                            + "\n\n" + "Headers: \n" + getRequestHeaders(mContext, apiEnumeration)
                            + "\n\n" + "Response Received: \n" + error.toString();
                    Log.d("ApiDetails", apiDetails);
                    onResponseReceived(mContext, ResponseType.FAILED, null, null, null,
                            listener, apiEnumeration, isShowDialog, loaderMessage);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getRequestHeaders(mContext, apiEnumeration);
            }
        };
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(mInitialTimeoutMS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsObjRequest.setShouldCache(false);

        addRequestQueue(mContext, jsObjRequest);
    }

    protected void onResponseReceived(Context context, ResponseType successOrFailure, String response, Object volleyError,
                                      ApiMarkerInterface commonServiceRequestModel, VolleyResponse listener,
                                      ApiEnumeration apiEnumeration, Boolean isShowDialog, String loaderMessage) {
        try {
            GenericUtils.hideProgressDialog();
            switch (successOrFailure) {
                case SUCCESS:
                    if (!TextUtils.isEmpty(response)) {

                        BaseParserInterface parsedObject = new Gson().fromJson(response, (Type) apiEnumeration.getReferenceClass());

                        if (parsedObject != null) {
                            handleHttpSuccessDataFromServer(parsedObject, response, commonServiceRequestModel, listener,
                                    apiEnumeration, isShowDialog, loaderMessage);
                        } else {
                            listener.onFailure(context.getString(R.string.no_response), apiEnumeration);
                        }
                    } else {
                        listener.onFailure(context.getString(R.string.no_response), apiEnumeration);
                    }
                    break;
                case FAILED:
                    String message = setFailureMessage(context, volleyError, "");
                    listener.onFailure(message, apiEnumeration);
                    break;
            }
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
    }

    protected String setFailureMessage(Context context, Object volleyError, String message) {
        if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError) {
            message = context.getString(R.string.error_message_time_out);
        } else if (volleyError instanceof AuthFailureError) {
            message = context.getString(R.string.error_message_auth_fail);
        } else if (volleyError instanceof ServerError) {
            message = context.getString(R.string.error_message_server_error);
        } else if (volleyError instanceof NetworkError) {
            message = context.getString(R.string.error_message_network_error);
        } else if (volleyError instanceof ParseError) {
            message = context.getString(R.string.error_message_parse_error);
        }
        return message;
    }

    protected void handleHttpSuccessDataFromServer(BaseParserInterface parsedObject, String response,
                                                   ApiMarkerInterface commonServiceRequestModel,
                                                   VolleyResponse listener, ApiEnumeration apiEnumeration,
                                                   Boolean isShowDialog, String loaderMessage) throws JSONException {
    }

    protected boolean checkIfTokenIsNotValidOrExpired(BaseParserInterface parsedObject,
                                                      ApiMarkerInterface commonServiceRequestModel, VolleyResponse listener,
                                                      ApiEnumeration apiEnumeration,
                                                      Boolean isShowDialog, String loaderMessage) {
        return true;
    }

    protected void handleDefaultScenario(BaseParserInterface parsedObject, ApiMarkerInterface
            commonServiceRequestModel, VolleyResponse listener, ApiEnumeration apiEnumeration,
                                         Boolean isShowDialog, String loaderMessage) {
        boolean isSuccessResponse;
        isSuccessResponse = checkIfTokenIsNotValidOrExpired(parsedObject,
                commonServiceRequestModel, listener, apiEnumeration, isShowDialog, loaderMessage);
        if (isSuccessResponse) {
            try {
                listener.onResult(parsedObject, apiEnumeration);
            } catch (Exception e) {
//                listener.onFailure(e.getMessage(), apiEnumeration);
                Log.e("", e.getMessage());
            }
        }
    }


    protected Map<String, String> getRequestHeaders(Context context, ApiEnumeration apiEnumeration) {
        HashMap<String, String> header = new HashMap();
        header.put(ApiConstants.HK_CONTENT_TYPE, "application/json; charset=UTF-8");
        header.put(ApiConstants.HK_APP_VERSION, "" + GenericUtils.getPackageVersion(context));
        header.put(ApiConstants.HK_PLATFORM, "Android");
        header.put(ApiConstants.HK_RESOLUTION, GenericUtils.getScreenDensity(context));
        header.put(ApiConstants.HK_AUTHORIZATION, GenericUtils.getAuthorization());
        header.put(ApiConstants.HK_DEVICE_IP, GenericUtils.getIpAddress(context));
        header.put(ApiConstants.HK_DEVICE_ID, GenericUtils.getDeviceId());
        return header;
    }

    public <T> JSONObject getJsonObjectFromPojo(ApiMarkerInterface requestModel) {
        JSONObject jsonObject = null;
//        Type type = new TypeToken<T>() {
//        }.getType();
//        String json = new Gson().toJson(requestModel, type);
//        try {
//            jsonObject = new JSONObject(json);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        String json = new Gson().toJson(requestModel);
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
