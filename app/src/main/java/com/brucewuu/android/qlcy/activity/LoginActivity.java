package com.brucewuu.android.qlcy.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;

import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.base.BaseActivity;
import com.brucewuu.android.qlcy.config.AppConfig;
import com.brucewuu.android.qlcy.model.User;
import com.brucewuu.android.qlcy.util.PreferenceUtil;
import com.brucewuu.android.qlcy.util.StringUtils;
import com.brucewuu.android.qlcy.util.UIHelper;
import com.brucewuu.android.qlcy.util.io.LogUtils;
import com.mcxiaoke.next.task.SimpleTaskCallback;
import com.mcxiaoke.next.task.TaskCallable;
import com.mcxiaoke.next.task.TaskCallback;
import com.mcxiaoke.next.task.TaskQueue;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.okhttp.FormEncodingBuilder;

import org.brucewuu.http.AppClient;

import java.util.concurrent.Callable;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by brucewuu on 15/9/1.
 */
public class LoginActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.et_mobile)
    MaterialEditText etMobile;

    @Bind(R.id.btn_login)
    AppCompatButton btnLogin;

    @Override
    protected int getLayoutId() {
        return R.layout.act_login;
    }

    @Override
    protected void afterViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.btn_login)
    void login() {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("phone", etMobile.getText().toString());
        TaskQueue.getDefault().addSerially(getCallable(AppConfig.LOGIN_URL, builder), callback, this);
    }

    @OnClick(R.id.btn_register)
    void register() {
        redirectTo(RegisterActivity.class);
    }

    @OnTextChanged(value = R.id.et_mobile, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void inputMobile(Editable editable) {
        if (StringUtils.isPhone(editable.toString())) {
            btnLogin.setEnabled(true);
        }
    }

    final TaskCallback<String> callback = new SimpleTaskCallback<String>() {
        @Override
        public void onTaskSuccess(String result, Bundle extras) {
            LogUtils.e("result:" + result);
            User user = User.parseJson(result);
            if (user != null && user.getResult().equals("0")) {
                user.setId(user.getPhone());
                PreferenceUtil.setUserInfo(user);
                redirectTo(MainActivity.class);
                finish();
            } else if (user != null && user.getResult().equals("-2")) {
                UIHelper.showToast("手机号未注册~");
            } else {
                UIHelper.showToast("登录失败，请重试~");
            }
        }

        @Override
        public void onTaskFailure(Throwable ex, Bundle extras) {
            UIHelper.showToast("登录失败，请重试~");
        }
    };

    private Callable<String> getCallable(final String url, final FormEncodingBuilder builder) {
        return new TaskCallable<String>() {
            @Override
            public String call() throws Exception {
                return AppClient.post(url, builder);
            }
        };
    }

    @Override
    protected void onDestroy() {
        TaskQueue.getDefault().cancelAll(this);
        super.onDestroy();
    }
}
