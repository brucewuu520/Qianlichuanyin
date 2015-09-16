package com.brucewuu.android.qlcy.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.brucewuu.android.qlcy.listener.OnSoftKeyboardListener;

/**
 * Created by brucewuu on 15/9/9.
 */
public class KeyboardRelativeLayout extends RelativeLayout {

    private OnSoftKeyboardListener listener;

    public KeyboardRelativeLayout(Context context) {
        super(context);
    }

    public KeyboardRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (listener != null) {
            final int newSpec = MeasureSpec.getSize(heightMeasureSpec);
            final int oldSpec = getMeasuredHeight();
            if (oldSpec - newSpec > 100) {
                listener.onShow();
            } else {
                listener.onHidden();
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setOnSoftKeyboardListener(OnSoftKeyboardListener listener) {
        this.listener = listener;
    }
}
