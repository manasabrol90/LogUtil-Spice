package com.id.spice.logutil.utilities;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.id.spice.logutil.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by Manas Abrol
 */

public class GenericUtils {

    private static Dialog mOverlayDialog;

    public static boolean haveNetworkConnection(Context c) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static void hideProgressDialog() {
        try {
            mOverlayDialog.dismiss();
        } catch (Exception e) {
        }
    }

    public static void displayProgressDialog(Context mContext) {
        try {
            if (mOverlayDialog == null || !(mOverlayDialog.isShowing())) {

                mOverlayDialog = new Dialog(mContext);
                mOverlayDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mOverlayDialog.setCancelable(false);
                mOverlayDialog.setCanceledOnTouchOutside(false);

                mOverlayDialog.setContentView(R.layout.dialog_network);
                Window window = mOverlayDialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.CENTER);
                WindowManager.LayoutParams lp = window.getAttributes();

                lp.dimAmount = 0;
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ProgressBar mProgressBar = (ProgressBar) mOverlayDialog.findViewById(R.id.progressBar);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                    Drawable wrapDrawable = DrawableCompat.wrap(mProgressBar.getIndeterminateDrawable());
                    DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(mContext, R.color.colorPrimary));
                    mProgressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
                } else {
                    mProgressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.
                            getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                }
                mOverlayDialog.show();
            }
        } catch (Exception e) {
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String getFormatDateTimeLong(long millisecond) {
        String dateString = "";
        try {
            dateString = DateFormat.format("dd MMM, yyyy | HH:mm:ss", new Date(millisecond)).toString();
        } catch (Exception e) {
        }
        return dateString;
    }

    public static boolean isOperatingSystemMIUI() {
        String device = Build.MANUFACTURER;
        if (device.equals("Xiaomi")) {
            try {
                Properties prop = new Properties();
                prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
                return prop.getProperty("ro.miui.ui.version.code", null) != null
                        || prop.getProperty("ro.miui.ui.version.name", null) != null
                        || prop.getProperty("ro.miui.internal.storage", null) != null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static String getPackageVersion(Context context) {
        PackageInfo pInfo = null;
        String version = "";
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static String getIpAddress(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }

    public static String getDeviceId() {
        return Build.SERIAL;
    }

    public static String getAuthorization() {
        String text = "testclient";
        byte[] data = new byte[0];
        try {
            data = text.getBytes("UTF-8");
            String base64 = Base64.encodeToString(data, Base64.DEFAULT);
            text = "Bearer " + base64;
        } catch (UnsupportedEncodingException e) {
            text = "Bearer " + text;
            e.printStackTrace();
        }
        return text;
    }

    public static String getScreenDensity(Context context) {
        float den = 0;
        try {
            den = context.getResources().getDisplayMetrics().density;
            if (den == 0.75) {//return 0.75 if it's LDPI
                return "mdpi";
            } else if (den == 1.0) {// return 1.0 if it's MDPI
                return "mdpi";
            } else if (den == 1.5) {// return 1.5 if it's HDPI
                return "hdpi";
            } else if (den == 2.0) {// return 2.0 if it's XHDPI
                return "xhdpi";
            } else if (den == 3.0) {// return 3.0 if it's XXHDPI
                return "xxhdpi";
            } else if (den == 4.0) {// return 4.0 if it's XXXHDPI
                return "xxhdpi";
            } else {
                return "xxhdpi";
            }
        } catch (Exception e) {
            return "xxhdpi";
        }
    }

    public static boolean checkIfRunningOnProxy(URI uri) {
        boolean isProxyOn = false;
        ProxySelector defaultProxySelector = ProxySelector.getDefault();
        Proxy proxy = null;
        List<Proxy> proxyList = defaultProxySelector.select(uri);
        if (proxyList.size() > 0) {
            proxy = proxyList.get(0);
            if (proxy != null && proxy.address() != null) {
                Log.d("", "Current Proxy Configuration: " + proxy.toString());
                isProxyOn = true;
            }
        }
        return isProxyOn;
    }
}