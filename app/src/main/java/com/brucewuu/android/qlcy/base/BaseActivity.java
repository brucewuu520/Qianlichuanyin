package com.brucewuu.android.qlcy.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.brucewuu.android.qlcy.AppManager;
import com.brucewuu.android.qlcy.widget.LProgressDialog;

import butterknife.ButterKnife;


/**
 * Created by Brucewuu on 15/4/10.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected abstract @LayoutRes int getLayoutId();

    protected abstract void afterViews();

    protected LProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);

        afterViews();
        AppManager.getInstance().addActivity(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onHomeClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * TRIM_MEMORY_UI_HIDDEN 回调只有当我们程序中的所有UI组件全部不可见的时候才会触发，
     * 这和onStop()方法还是有很大区别的，
     * 因为onStop()方法只是当一个Activity完全不可见的时候就会调用，
     * 比如说用户打开了我们程序中的另一个Activity。
     * 因此，我们可以在onStop()方法中去释放一些Activity相关的资源，
     * 比如说取消网络连接或者注销广播接收器等，
     * 但是像UI相关的资源应该一直要等到onTrimMemory(TRIM_MEMORY_UI_HIDDEN)这个回调之后
     * 才去释放，这样可以保证如果用户只是从我们程序的一个Activity回到了另外一个Activity，
     * 界面相关的资源都不需要重新加载，从而提升响应速度。
     * @param level
     */
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {

        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().finishActivity(this);
    }

    protected void onHomeClick() {
        if (isFinishing())
            return;
        onBackPressed();
    }

    protected void showProgressDialog(String msg) {
        if (this.mProgressDialog == null)
            this.mProgressDialog = new LProgressDialog(this, msg, true);

        if (!TextUtils.isEmpty(msg))
            this.mProgressDialog.setMessage(msg);
        this.mProgressDialog.show();
    }

    protected void dismissProgressDialog() {
        if (this.mProgressDialog != null)
            this.mProgressDialog.dismiss();
    }

    protected void redirectTo(Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        startActivity(intent);
    }

    protected void redirectTo(Class<?> cls, Bundle extras) {
        Intent intent = new Intent();
        intent.putExtras(extras);
        intent.setClass(this, cls);
        startActivity(intent);
    }
}
