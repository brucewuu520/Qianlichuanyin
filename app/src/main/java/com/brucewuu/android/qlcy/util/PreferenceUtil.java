package com.brucewuu.android.qlcy.util;

import android.preference.PreferenceManager;

import com.brucewuu.android.qlcy.AppContext;
import com.brucewuu.android.qlcy.model.User;

/**
 * Created by brucewuu on 15/9/1.
 */
public final class PreferenceUtil {

    private PreferenceUtil() {}

    public static void setUserInfo(User user) {
        PreferenceManager.getDefaultSharedPreferences(AppContext.getInstance()).edit().putString("user_info", User.toJson(user)).apply();
    }

    public static String getUserInfo() {
        return PreferenceManager.getDefaultSharedPreferences(AppContext.getInstance()).getString("user_info", null);
    }

    public static void clear() {
        PreferenceManager.getDefaultSharedPreferences(AppContext.getInstance()).edit().clear().apply();
    }
}
