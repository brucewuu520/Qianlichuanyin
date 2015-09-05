package com.brucewuu.android.qlcy.widget.drawstatusbar;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Created by brucewuu on 15/6/15.
 */
@TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
class DrawStatusLayoutInsetsHelperLollipop implements DrawStatusLayouyInsetsHelper {

    DrawStatusLayoutInsetsHelperLollipop() {
    }

    @Override
    public void setupForWindowInsets(View view, OnApplyWindowInsetsListener listener) {
        if (ViewCompat.getFitsSystemWindows(view)) {
            ViewCompat.setOnApplyWindowInsetsListener(view, listener);
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
}
