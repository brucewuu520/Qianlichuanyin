package com.brucewuu.android.qlcy.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.Toast;

import com.brucewuu.android.qlcy.AppContext;
import com.brucewuu.android.qlcy.AppManager;
import com.brucewuu.android.qlcy.R;


public final class UIHelper {

    public static void showToast(String tip) {
        Toast.makeText(AppContext.getInstance(), tip, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(String tip) {
        Toast.makeText(AppContext.getInstance(), tip, Toast.LENGTH_LONG).show();
    }

    public static void centerToast(String tip) {
        Toast toast = Toast.makeText(AppContext.getInstance(), tip, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 30);
        toast.show();
    }

    /**
     * 检测网络是否可用
     *
     * @return
     */
    public static boolean checkNetWork() {
        Context context = AppContext.getInstance();
        if (NetworkUtils.isNotConnected(context)) {
            showToast(context.getString(R.string.net_error));
            return false;
        }

        return true;
    }

    /**
     * 发送App异常崩溃报告
     *
     * @param context
     */
    public static void sendAppCrashReport(@NonNull Context context) {
        new AlertDialog.Builder(context)
                .setTitle("灰常抱歉~~~(>_<)~~~")
                .setMessage(context.getString(R.string.app_error_message))
                .setPositiveButton("提交报告", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AppManager.getInstance().AppExit();
                    }
                }).show();
    }
}