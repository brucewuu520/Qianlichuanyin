package com.brucewuu.android.qlcy.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.brucewuu.android.qlcy.util.io.IOUtils;
import com.yzxtcp.tools.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.List;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * User: brucewuu
 * Date: 14-7-21
 * Time: 上午10:06
 */
public final class AndroidUtils {
    /**
     * app SDCard文件根目录
     */
    private static final String APP_DIR = "Boluome";
    /**
     * app缓存目录
     */
    private static final String CACHE_DIR = "cache";
    /**
     * 图片缓存目录
     */
    private static final String INDIVIDUAL_DIR_NAME = "uil-images";
    /**
     * 用户图像存储目录
     */
    private static final String FACE_DIR = "face";
    /**
     * 应用日志保存目录
     */
    private static final String LOG_DIR = "log";
    /**
     * web缓存目录
     */
    private static final String WEB_CACHE_DIR = "web";
    private static final String FILENAME_NOMEDIA = ".nomedia";

    private static String versionName;


    private AndroidUtils() {
    }

    /**
     * 创建（获取）图片缓存目录../cache/uil-images
     *
     * @param context
     * @return
     */
    public static File getImageCacheDirectory(Context context) {
        File cacheDir = getCacheDirectory(context);
        File imageCacheDir = new File(cacheDir, INDIVIDUAL_DIR_NAME);
        if (!imageCacheDir.exists()) {
            if (!imageCacheDir.mkdir()) {
                imageCacheDir = cacheDir;
            }
        }

        return imageCacheDir;
    }

    /**
     * 创建（获取）应用的web缓存目录../cache/web
     *
     * @param context
     * @return
     */
    public static File getWebCacheDirectory(Context context) {
        File cacheDir = getCacheDirectory(context);
        File webCacheDir = new File(cacheDir, WEB_CACHE_DIR);
        if (!webCacheDir.exists()) {
            if (!webCacheDir.mkdir()) {
                webCacheDir = cacheDir;
            }
        }

        return webCacheDir;
    }

    /**
     * 存放用户头像文件夹 ../face
     */
    public static File getFaceDirectory(Context context) {
        File faceDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        }
        if (MEDIA_MOUNTED.equals(externalStorageState)) {
            faceDir = getExternalFaceDir();
        }
        if (faceDir == null) {
            faceDir = context.getCacheDir();
        }
        if (faceDir == null) {
            String faceDirPath = new StringBuilder("/data/data/").append(context.getPackageName()).append("/face/").toString();
            faceDir = new File(faceDirPath);
        }

        return faceDir;
    }

    /**
     * 存放错误日志的文件夹 ../log
     *
     * @return
     */
    public static File getLogDirectory(Context context) {
        File cacheDir = getCacheDirectory(context);
        File logDir = new File(cacheDir, LOG_DIR);
        if (!logDir.exists()) {
            if (!logDir.mkdir()) {
                logDir = cacheDir;
            }
        }

        return logDir;
    }

    /**
     * 创建（获取）应用的缓存目录../cache
     *
     * @param context
     * @return
     */
    public static File getCacheDirectory(Context context) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        }
        if (MEDIA_MOUNTED.equals(externalStorageState)) {
            appCacheDir = getExternalCacheDir();
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = new StringBuilder("/data/data/").append(context.getPackageName()).append("/cache/").toString();
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    private static File getExternalFaceDir() {
        File faceDir = new File(new File(Environment.getExternalStorageDirectory(), APP_DIR), FACE_DIR);
        if (!faceDir.exists()) {
            if (!faceDir.mkdirs()) {
                return null;
            }
            try {
                new File(faceDir, FILENAME_NOMEDIA).createNewFile();
            } catch (IOException e) {
            }
        }

        return faceDir;
    }

    private static File getExternalCacheDir() {
        File appCacheDir = new File(new File(Environment.getExternalStorageDirectory(), APP_DIR), CACHE_DIR);
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                return null;
            }
            try {
                new File(appCacheDir, FILENAME_NOMEDIA).createNewFile();
            } catch (IOException e) {
            }
        }

        return appCacheDir;
    }

    /**
     * 判断是否有内存卡
     *
     * @return
     */
    public static boolean hasSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取SD卡的根目录
     *
     * @return
     */
    public static String getSDRoot() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 隐藏输入法键盘
     *
     * @param context
     * @param editText
     */
    public static void hideSoftKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    /**
     * Checks whether the recording service is currently running.
     *
     * @param ctx the current context
     * @return true if the service is running, false otherwise
     */
    public static boolean isServiceRunning(Context ctx, Class<?> cls) {
        ActivityManager activityManager = (ActivityManager) ctx
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager
                .getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo serviceInfo : services) {
            ComponentName componentName = serviceInfo.service;
            String serviceName = componentName.getClassName();
            if (serviceName.equals(cls.getName())) {
                return true;
            }
        }
        return false;
    }

    public static String getDeviceId(Context context) {
        String imei = getPhoneIMEI(context);
        if (!TextUtils.isEmpty(imei))
            return imei;
        return getAndroidID(context);
    }

    /**
     * 获取设备的ANDROID_ID
     * @param context
     * @return
     */
    public static String getAndroidID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取设备号
     *
     * @param context
     * @return
     */
    public static String getPhoneIMEI(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    /**
     * 获取App安装包信息
     *
     * @return
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            //e.printStackTrace(System.err);
        }
        if (info == null) info = new PackageInfo();
        return info;
    }

    public static String getVersionName(Context context) {
        if (StringUtils.isEmpty(versionName)) {
            versionName = getPackageInfo(context).versionName;
        }

        return versionName;
    }

    /**
     * 获取ApiKey
     *
     * @param context
     * @param metaKey
     * @return
     */
    public static String getMetaValue(Context context, String metaKey) {
        String apiKey = "NameNotFoundException";
        if (context == null || metaKey == null) {
            return apiKey;
        }
        try {
            Bundle metaData = null;
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {
        }

        return apiKey;
    }

    /**
     * 获取系统版本号
     *
     * @param propName
     * @return
     */
    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException e) {
            return "";
        } finally {
            IOUtils.closeQuietly(input);
        }

        return line;
    }

    /**
     * @param context
     * @param intent
     * @return
     */
    public static boolean isIntentExist(Context context, Intent intent) {
        List localList = context.getPackageManager().queryIntentActivities(intent, 1);
        if (ListUtils.isEmpty(localList))
            return false;

        return true;
    }

//    /**
//     * 创建快捷方式
//     *
//     * @param cxt   Context
//     * @param icon  快捷方式图标
//     * @param title 快捷方式标题
//     * @param cls   要启动的类
//     */
//    public void createDeskShortCut(Context cxt, int icon,
//                                   String title, Class<?> cls) {
//        // 创建快捷方式的Intent
//        Intent shortcutIntent = new Intent(
//                "com.android.launcher.action.INSTALL_SHORTCUT");
//        // 不允许重复创建
//        shortcutIntent.putExtra("duplicate", false);
//        // 需要现实的名称
//        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
//        // 快捷图片
//        Parcelable ico = Intent.ShortcutIconResource.fromContext(
//                cxt.getApplicationContext(), icon);
//        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
//                ico);
//        Intent intent = new Intent(cxt, cls);
//        // 下面两个属性是为了当应用程序卸载时桌面上的快捷方式会删除
//        intent.setAction("android.intent.action.MAIN");
//        intent.addCategory("android.intent.category.LAUNCHER");
//        // 点击快捷图片，运行的程序主入口
//        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
//        // 发送广播。OK
//        cxt.sendBroadcast(shortcutIntent);
//    }

    /**
     * 判断某应用是否已安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean checkInstall(Context context, String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            if (packageInfo != null)
                return true;
        } catch (NameNotFoundException e) {
        }

        return false;
    }

    /**
     * SHA-1 加密算法
     *
     * @param data
     * @return
     */
    public static String SHA_1(String data) {
        StringBuilder sb = new StringBuilder("");
        try {
            // Get an hmac_sha1 key from the raw key bytes
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(data.getBytes("UTF-8"));
            byte[] result = md.digest();

            for (byte b : result) {
                int i = b & 0xff;
                if (i < 0xf) {
                    sb.append(0);
                }
                sb.append(Integer.toHexString(i));
            }
        } catch (Exception e) {
        }

        return sb.toString();
    }

    public static boolean isAndroidL() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
