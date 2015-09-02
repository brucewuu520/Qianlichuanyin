package com.brucewuu.android.qlcy.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

/**
 * 网络状态工具类
 *
 * @version 2.0 2014.02.12
 */
public final class NetworkUtils {

    public static final String WIFI = "wifi";
    public static final String MOBILE = "mobile";
    public static final String MOBILE_CTWAP = "ctwap";
    public static final String MOBILE_CMWAP = "cmwap";
    public static final String MOBILE_3GWAP = "3gwap";
    public static final String MOBILE_UNIWAP = "uniwap";
    public static final String NONE = "none";
    public static final String OTHER = "other";

    private NetworkUtils() {
    }

    /**
     * 获取当前网络类型
     *
     * @return 返回网络类型
     */
    public static String getNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || !info.isConnectedOrConnecting()) {
            return NONE;
        }
        int type = info.getType();
        if (ConnectivityManager.TYPE_WIFI == type) {
            return WIFI;
        } else if (ConnectivityManager.TYPE_MOBILE == type) {
            return MOBILE;
        } else {
            return OTHER;
        }
    }

    /**
     * 网络连接是否断开
     *
     * @param context Context
     * @return 是否断开s
     */
    public static boolean isNotConnected(Context context) {
        return !isConnected(context);
    }

    /**
     * 是否有网络连接
     *
     * @param context Context
     * @return 是否连接
     */
    public static boolean isConnected(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    /**
     * 当前是否是WIFI网络
     *
     * @param context Context
     * @return 是否WIFI
     */
    public static boolean isWifi(Context context) {
        return WIFI.equals(getNetworkType(context));
    }

    /**
     * 当前是否移动网络
     *
     * @param context Context
     * @return 是否移动网络
     */
    public static boolean isMobile(Context context) {
        return MOBILE.equals(getNetworkType(context));
    }

    /**
     * 根据当前网络状态获取代理
     *
     * @param context Context
     * @return 代理
     */
    public static Proxy getProxyChina(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null
                || networkInfo.getType() != ConnectivityManager.TYPE_MOBILE
                || networkInfo.getExtraInfo() == null) {
            return null;
        }
        String typeName = networkInfo.getExtraInfo();
        if (MOBILE_CTWAP.equalsIgnoreCase(typeName)) {
            InetSocketAddress address = new InetSocketAddress("10.0.0.200", 80);
            return new Proxy(Type.HTTP, address);
        } else if (MOBILE_CMWAP.equalsIgnoreCase(typeName)
                || MOBILE_UNIWAP.equalsIgnoreCase(typeName)
                || MOBILE_3GWAP.equalsIgnoreCase(typeName)) {
            InetSocketAddress address = new InetSocketAddress("10.0.0.172", 80);
            return new Proxy(Type.HTTP, address);
        } else {
            return null;
        }
    }

    /**
     * 获取WiFi链接的ip地址
     * @param context
     * @return
     */
    public static String getWifiIpAddress(@NonNull Context context) {
        WifiManager e = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiinfo = e.getConnectionInfo();
        if (wifiinfo != null)
            return convertIntToIp(wifiinfo.getIpAddress());

        return "";
    }

    /**
     * int 转 ip
     * @param i
     * @return
     */
    private static String convertIntToIp(int i) {
        return (i & 255) + "." + (i >> 8 & 255) + "." + (i >> 16 & 255) + "." + (i >> 24 & 255);
    }
}
