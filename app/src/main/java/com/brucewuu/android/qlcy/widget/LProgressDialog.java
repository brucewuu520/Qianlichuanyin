package com.brucewuu.android.qlcy.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.widget.TextView;

import com.brucewuu.android.qlcy.R;
import com.pnikosis.materialishprogress.ProgressWheel;

/**
 * Created by brucewuu on 2015/3/30.
 */
public class LProgressDialog extends AppCompatDialog {

    private TextView tvMessage;
    private CharSequence message;
    private int progressColor = -1;


    public LProgressDialog(@NonNull Context context) {
        this(context, null);
    }

    public LProgressDialog(@NonNull Context context, CharSequence message) {
        this(context, message, -1, false);
    }

    public LProgressDialog(@NonNull Context context, CharSequence message, boolean cancelable) {
        this(context, message, -1, cancelable);
    }

    public LProgressDialog(@NonNull Context context, CharSequence message, int progressColor, boolean cancelable) {
        super(context, R.style.MyDialog);
        this.message = message;
        this.progressColor = progressColor;

        setCanceledOnTouchOutside(false);
        setCancelable(cancelable);

        super.setContentView(R.layout.l_progress_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvMessage = (TextView) findViewById(R.id.progress_dialog_message);
        tvMessage.setText(message);
        if (progressColor != -1) {
            ProgressWheel mProgressWheel = (ProgressWheel) findViewById(R.id.progress_dialog_wheel);
            mProgressWheel.setBarColor(progressColor);
        }
    }

    public void setMessage(CharSequence message) {
        this.message = message;
    }

    @Override
    public void show() {
        super.show();
        if (!TextUtils.isEmpty(message))
            tvMessage.setText(message);
    }
}
