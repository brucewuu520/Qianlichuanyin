package com.brucewuu.android.qlcy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.util.PreferenceUtil;
import com.bumptech.glide.Glide;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView imageView = new ImageView(this);
        setContentView(imageView);
        Glide.with(this).load(R.mipmap.loading).centerCrop().into(imageView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (TextUtils.isEmpty(PreferenceUtil.getUserInfo())) {
            redirectTo(LoginActivity.class);
        } else {
            redirectTo(MainActivity.class);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void redirectTo(final Class<?> cls) {
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, cls));
                finish();
            }
        }, 2000L);
    }
}