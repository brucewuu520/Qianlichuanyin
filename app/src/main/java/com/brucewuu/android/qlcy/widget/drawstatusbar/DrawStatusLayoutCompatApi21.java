package com.brucewuu.android.qlcy.widget.drawstatusbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.WindowInsets;

/**
 * Created by brucewuu on 15/6/15.
 * Provides functionality unique to API 21
 */
@TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
class DrawStatusLayoutCompatApi21 {

    private static final int[] THEME_ATTRS = {
            android.R.attr.colorPrimaryDark
    };

    public static Drawable getDefaultStatusBarBackground(Context context) {
        final TypedArray a = context.obtainStyledAttributes(THEME_ATTRS);
        try {
            return a.getDrawable(0);
        } finally {
            a.recycle();
        }
    }

    public static int getTopInset(Object insets) {
        return insets != null ? ((WindowInsets) insets).getSystemWindowInsetTop() : 0;
    }
}
