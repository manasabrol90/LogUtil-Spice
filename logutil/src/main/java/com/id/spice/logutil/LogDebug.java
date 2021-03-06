package com.id.spice.logutil;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

public class LogDebug {

    private static final String TAG = "SUPER_AWESOME_APP";

    public static void d(String message) {
        Log.d(TAG, message);
    }

    public static void startFlow(Activity activity, String userId) {
        Intent intent = new Intent(activity, MyCardActivity.class);
        intent.putExtra("userId", userId);
        activity.startActivity(new Intent(activity, MyCardActivity.class));
    }

}
