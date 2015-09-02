package com.brucewuu.android.qlcy;

import android.app.Application;

import com.yzxIM.IMManager;
import com.yzxtcp.UCSManager;

/**
 * Created by brucewuu on 15/9/1.
 */
public class AppContext extends Application {

    private static AppContext instance;

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
        UCSManager.init(this);//初始化核心服务
        IMManager.getInstance(this);//必须要加上

        if (!BuildConfig.LOG_DEBUG)
            Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());

    }

    public static AppContext getInstance() {
        return instance;
    }
}
