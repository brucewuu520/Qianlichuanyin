package com.brucewuu.android.qlcy.widget.drawstatusbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.widget.Space;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.brucewuu.android.qlcy.util.AndroidUtils;
import com.brucewuu.android.qlcy.util.ViewUtils;


/**
 * Created by brucewuu on 15/6/15.
 */
public class DrawStatusLinearLayout extends LinearLayout {

    static final DrawStatusLayouyInsetsHelper INSETS_HELPER;

    private WindowInsetsCompat mLastInsets;
    private boolean mDrawStatusBarBackground;
    private Drawable mStatusBarBackground;

    static {
        if (AndroidUtils.isAndroidL())
            INSETS_HELPER = new DrawStatusLayoutInsetsHelperLollipop();
        else
            INSETS_HELPER = null;
    }


    public DrawStatusLinearLayout(Context context) {
        this(context, null);
    }

    public DrawStatusLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        if (INSETS_HELPER != null) {
            Space space = new Space(context);
            //ViewUtils.setDrawable(space, mStatusBarBackground);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params.height = ViewUtils.getStatusBarHeight(context);
            space.setLayoutParams(params);
            addView(space);
            this.mStatusBarBackground = DrawStatusLayoutCompatApi21.getDefaultStatusBarBackground(context);
            INSETS_HELPER.setupForWindowInsets(this, new ApplyInsetsListener());
        }
    }

    public void setStatusBarBackground(Drawable bg) {
        if (INSETS_HELPER == null)
            return;
        this.mStatusBarBackground = bg;
        this.invalidate();
    }

    public Drawable getStatusBarBackground() {
        return this.mStatusBarBackground;
    }

    public void setStatusBarBackgroundResource(@DrawableRes int resId) {
        this.setStatusBarBackground(resId != 0 ? ContextCompat.getDrawable(this.getContext(), resId) : null);
    }

    public void setStatusBarBackgroundColor(@ColorInt int color) {
        this.setStatusBarBackground(new ColorDrawable(color));
    }

    private void setWindowInsets(WindowInsetsCompat insets) {
        if (this.mLastInsets != insets) {
            this.mLastInsets = insets;
            this.mDrawStatusBarBackground = insets != null && insets.getSystemWindowInsetTop() > 0;
            this.setWillNotDraw(!this.mDrawStatusBarBackground && this.getBackground() == null);
            this.requestLayout();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mDrawStatusBarBackground && this.mStatusBarBackground != null) {
            int inset = this.mLastInsets != null ? this.mLastInsets.getSystemWindowInsetTop() : 0;
            if (inset > 0) {
                this.mStatusBarBackground.setBounds(0, 0, this.getWidth(), inset);
                this.mStatusBarBackground.draw(canvas);
            }
        }
    }

    final class ApplyInsetsListener implements android.support.v4.view.OnApplyWindowInsetsListener {
        ApplyInsetsListener() {
        }

        public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
            DrawStatusLinearLayout.this.setWindowInsets(insets);
            return insets.consumeSystemWindowInsets();
        }
    }
}
