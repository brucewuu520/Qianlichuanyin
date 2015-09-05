package com.brucewuu.android.qlcy.util;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import java.io.File;

/**
 * ****** System Action_View************
 * <p/>
 * 如有 特殊 需求 请改 FLAG
 *
 * @author brucewuu
 * @date 2013-5-15
 */
public class SysIntentUtil {

    public static void install(Context context, File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    // uninstall a apk
    public static void uninstall(Context context, String pkg) {
        if (pkg == null) {
            return;
        }
        Uri uri = Uri.fromParts("package", pkg, null);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 直接拨打电话
     * @param context
     * @param number
     */
    public static void callPhone(Context context, String number) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        context.startActivity(intent);
    }

    /**
     * 进入拨号面板
     * @param context
     * @param number
     */
    public static void goPhone(Context context, String number) {
        Uri uri = Uri.parse("tel:" + number);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        context.startActivity(intent);
    }

    /**
     * 返回桌面
     *
     * @param context
     */
    public static void goHome(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(intent);
    }

    /**
     * 返回桌面
     *
     * @param context
     */
    public static void goTo(Context context, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 进入设置的管理app界面
     */
    public static void goSettingsManageApp(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.applications.ManageApplications");
        context.startActivity(intent);
    }

    /**
     * 进入设置界面
     */
    public static void goSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * 进入网络设置界面
     *
     * @param context
     */
    public static void gotoNetworkSetting(Context context) {
        Intent intent = new Intent();
        // 类名一定要包含包名
        intent.setClassName("com.android.settings", "com.android.settings.WirelessSettings");
        context.startActivity(intent);
    }

    /**
     * 打开图片查看界面
     *
     * @param context
     */
    public static void goImageView(Context context) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        context.startActivity(intent);
    }

    /**
     * 打开指定Uri
     *
     * @param context
     * @param url
     */
    public static void goWeb(Context context, String url) {
        try {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Intent shareMore(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        //intent.setType("text/plain"); // 模拟器
        intent.setType("message/rfc822"); // 真机
        intent.putExtra(Intent.EXTRA_SUBJECT, "好友分享");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /**
     * 去应用市场下载指定app
     *
     * @param context
     */
    public static void goMarket(Context context, String packageName) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
            context.startActivity(intent);
        } catch (Exception e) {
        }
    }

    /**
     * 进入系统锁屏设置面板
     */
    public static void openSysLockSetting(Context context) {
        try {
            Intent intent = new Intent();
            ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.ChooseLockGeneric");
            intent.setComponent(cm);
            context.startActivity(intent);
        } catch (Exception e) {
        }
    }

    /**
     * 通过包名启动应用
     *
     * @param context
     * @param packageName
     */
    public static void openApp(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
    }
}
