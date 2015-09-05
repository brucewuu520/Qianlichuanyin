package com.brucewuu.android.qlcy.activity;

import android.support.v7.widget.Toolbar;

import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.base.BaseActivity;

import butterknife.Bind;

/**
 * Created by 吴昭 on 2015-9-3.
 */
public class SettingActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected int getLayoutId() {
        return R.layout.test;
    }

    @Override
    protected void afterViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
