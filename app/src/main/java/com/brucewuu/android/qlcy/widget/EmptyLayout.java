package com.brucewuu.android.qlcy.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.util.SysIntentUtil;
import com.pnikosis.materialishprogress.ProgressWheel;

import butterknife.ButterKnife;

/**
 * Created by brucewuu on 2015/3/26.
 */
public class EmptyLayout extends LinearLayout implements View.OnClickListener {

    public static final int DEFAULT = 0;
    public static final int ON_LOADING = 1;
    public static final int NO_DATA = 2;
    public static final int NET_ERROR = 3;
    public static final int LOAD_ERROR = 4;

    private boolean clickEnable = false;
    private OnClickListener listener;
    private int currentState = DEFAULT;

    private ProgressWheel mProgressWheel;
    private TextView tvEmpty;

    public EmptyLayout(Context context) {
        this(context, null);
    }

    public EmptyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        View.inflate(context, R.layout.view_empty, this);
        mProgressWheel = ButterKnife.findById(this, R.id.base_progress_wheel);
        tvEmpty = ButterKnife.findById(this, R.id.base_tv_empty);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (clickEnable) {
            if (currentState == NET_ERROR) {
                SysIntentUtil.gotoNetworkSetting(getContext());
            } else if (listener != null) {
                listener.onClick(v);
            }
        }
    }

    public void setEmptyClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public void setEmptyView(final int type) {
        switch (type) {
            case DEFAULT:
                clickEnable = false;
                tvEmpty.setVisibility(GONE);
                mProgressWheel.setVisibility(GONE);
                this.setVisibility(GONE);
                break;
            case ON_LOADING:
                clickEnable = false;
                this.setVisibility(VISIBLE);
                tvEmpty.setVisibility(VISIBLE);
                tvEmpty.setText(R.string.loading);
                mProgressWheel.setVisibility(VISIBLE);
                break;
            case NO_DATA:
                this.setVisibility(VISIBLE);
                mProgressWheel.setVisibility(GONE);
                tvEmpty.setVisibility(VISIBLE);
                tvEmpty.setText(R.string.no_data);
                clickEnable = true;
                break;
            case NET_ERROR:
                this.setVisibility(VISIBLE);
                mProgressWheel.setVisibility(GONE);
                tvEmpty.setVisibility(VISIBLE);
                tvEmpty.setText(R.string.net_error);
                clickEnable = true;
                break;
            case LOAD_ERROR:
                this.setVisibility(VISIBLE);
                mProgressWheel.setVisibility(GONE);
                tvEmpty.setVisibility(VISIBLE);
                tvEmpty.setText(R.string.load_error);
                clickEnable = true;
                break;
        }
        currentState = type;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
