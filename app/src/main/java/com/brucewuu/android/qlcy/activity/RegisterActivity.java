package com.brucewuu.android.qlcy.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.brucewuu.android.qlcy.AppManager;
import com.brucewuu.android.qlcy.R;
import com.brucewuu.android.qlcy.base.SwipeBackActivity;
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

/**
 * Created by brucewuu on 15/9/1.
 */
public class RegisterActivity extends SwipeBackActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.et_mobile)
    MaterialEditText etMobile;

    @Bind(R.id.et_nickname)
    MaterialEditText etNickname;

    @Override
    protected int getLayoutId() {
        return R.layout.act_register;
    }

    @Override
    protected void afterViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.btn_register_submit)
    void register() {
        String mobile = etMobile.getText().toString();
        String nickname = etNickname.getText().toString();
        if (!StringUtils.isPhone(mobile)) {
            UIHelper.showToast("请输入正确的手机号~");
            return;
        }
        if (!StringUtils.isNick(nickname)) {
            UIHelper.showToast("请输入昵称（不能是中文）~");
            return;
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("phone", mobile);
        builder.add("nickname", nickname);
        TaskQueue.getDefault().addSerially(getCallable(AppConfig.REGISTER_URL, builder), callback, this);
    }

    final TaskCallback<String> callback = new SimpleTaskCallback<String>() {
        @Override
        public void onTaskSuccess(String result, Bundle extras) {
            LogUtils.e("result:" + result);
            User user = User.parseJson(result);
            if (user != null && user.getResult().equals("0")) {
                PreferenceUtil.setUserInfo(user);
                redirectTo(MainActivity.class);
                finish();
                AppManager.getInstance().finishActivity(LoginActivity.class);
            } else if (user != null && user.getResult().equals("-1")) {
                UIHelper.showToast("该手机号已经注册~");
            } else {
                UIHelper.showToast("注册失败，请重试~");
            }
        }

        @Override
        public void onTaskFailure(Throwable ex, Bundle extras) {
            UIHelper.showToast("注册失败，请重试~");
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
