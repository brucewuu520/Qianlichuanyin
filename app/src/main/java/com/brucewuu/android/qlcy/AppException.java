package com.brucewuu.android.qlcy;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;

import com.brucewuu.android.qlcy.util.AndroidUtils;
import com.brucewuu.android.qlcy.util.DateUtil;
import com.brucewuu.android.qlcy.util.UIHelper;
import com.brucewuu.android.qlcy.util.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;

/**
 * 应用程序异常类：用于捕获异常和提示错误信息
 */
public class AppException extends Exception implements UncaughtExceptionHandler {

    /**
     * 系统默认的UncaughtException处理类
     */
    private UncaughtExceptionHandler mDefaultHandler;

    private AppException() {
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    /**
     * 获取APP异常崩溃处理对象
     *
     * @return
     */
    public static AppException getAppExceptionHandler() {
        return new AppException();
    }

    private AppException(Exception ex, boolean isLoad) {
        super(ex);
        this.saveErrorLog(ex);
        if (isLoad)
            this.handleException(ex);
    }

    /**
     * 保存异常日志
     *
     * @param te
     */
    private void saveErrorLog(Throwable te) {
        if (te == null)
            return;

        try {
            // 判断是否挂载了SD卡
            if (AndroidUtils.hasSDCard()) {
                Context context = AppContext.getInstance();
                File logFile = new File(AndroidUtils.getLogDirectory(context), "errorlog.txt");
                if (!logFile.exists()) {
                    logFile.createNewFile();
                }
                String crashReport = getCrashReport(te, context);
                if (!TextUtils.isEmpty(crashReport)) {
                    IOUtils.writeString(logFile, crashReport, true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        this.saveErrorLog(ex);
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    /**
     * 自定义异常处理:收集错误信息&发送错误报告
     *
     * @param ex
     * @return true:处理了该异常信息;否则返回false
     */
    private boolean handleException(final Throwable ex) {
        final Activity activity = AppManager.getInstance().currentActivity();

        if (activity == null)
            return false;

        // 显示异常信息&发送报告
        new Thread() {
            public void run() {
                Looper.prepare();
                // 上传错误信息到友盟的后台
                UIHelper.sendAppCrashReport(activity);
                Looper.loop();
            }

        }.start();

        return true;
    }

    /**
     * 获取APP崩溃异常报告
     *
     * @param ex
     * @return
     */
    private String getCrashReport(Throwable ex, Context context) {
        PackageInfo pinfo = AndroidUtils.getPackageInfo(context);
        StringBuilder sb = new StringBuilder("---------");
        sb.append(DateUtil.ParseDateToString(new Date()))
                .append("---------\n")
                .append("APP版本: ")
                .append(pinfo.versionName)
                .append("(")
                .append(pinfo.versionCode)
                .append(")\n")
                .append("系统版本: ")
                .append(Build.VERSION.RELEASE)
                .append("(")
                .append(Build.VERSION.SDK_INT)
                .append(")\n")
                .append("生产厂商: ")
                .append(Build.MANUFACTURER)
                .append("\n")
                .append("手机型号: ")
                .append(Build.MODEL)
                .append("\n")
                .append("设备: ")
                .append(Build.DEVICE)
                .append("\n")
                .append("异常信息: " + ex.getMessage() + "\n");
        StackTraceElement[] elements = ex.getStackTrace();
        for (int i = 0, lenth = elements.length; i < lenth; i++) {
            sb.append(elements[i].toString() + "\n");
        }
        return sb.toString();
    }

    public static AppException lock(Exception e) {
        return new AppException(e, true);
    }

    public static AppException http(Exception e) {
        return new AppException(e, false);
    }

    public static AppException encrypt(Exception e) {
        return new AppException(e, false);
    }

    public static AppException db(Exception e) {
        return new AppException(e, false);
    }


}
