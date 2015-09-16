package com.brucewuu.android.qlcy.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatDialog;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import java.lang.ref.WeakReference;

/**
 * 放微信录音按钮
 */
public class RecordButton extends Button {

    private static final int MIN_INTERVAL_TIME = 1000; // 录音最短时间
    private static final int MAX_INTERVAL_TIME = 60000; // 录音最长时间

    private AppCompatDialog mRecordDialog;
    private Handler mVolumeHandler; // 用于更新录音音量大小的图片
    private CountDownTimer mTimer;

    public RecordButton(Context context) {
        this(context, null);
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mVolumeHandler = new ShowVolumeHandler(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    /** 初始化 dialog和录音器 */
    private void initlization() {
        if (mRecordDialog == null) {
            mRecordDialog = new AppCompatDialog(getContext());
            mRecordDialog.setOnDismissListener(onDismiss);
        }
        mRecordDialog.show();
        startRecording();
    }

    /** 录音完成（达到最长时间或用户决定录音完成） */
    private void finishRecord() {
        stopRecording();
        mRecordDialog.dismiss();
//        long intervalTime = System.currentTimeMillis() - mStartTime;
//        if (intervalTime < MIN_INTERVAL_TIME) {
//            AppContext.showToastShort(R.string.record_sound_short);
//            File file = new File(mAudioFile);
//            file.delete();
//            return;
//        }
//        if (mFinishedListerer != null) {
//            mFinishedListerer.onFinishedRecord(mAudioFile,
//                    (int) ((System.currentTimeMillis() - mStartTime) / 1000));
//        }
    }
    // 用户手动取消录音
    private void cancelRecord() {
        stopRecording();
        mRecordDialog.dismiss();
//        File file = new File(mAudioFile);
//        file.delete();
//        if (mFinishedListerer != null) {
//            mFinishedListerer.onCancleRecord();
//        }
    }

    // 开始录音
    private void startRecording() {
//        mAudioUtil.setAudioPath(mAudioFile);
//        mAudioUtil.recordAudio();
//        mThread = new ObtainDecibelThread();
//        mThread.start();
    }
    // 停止录音
    private void stopRecording() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

    }

    private final DialogInterface.OnDismissListener onDismiss = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            stopRecording();
        }
    };

    static class ShowVolumeHandler extends Handler {

        private final WeakReference<RecordButton> mOuterInstance;
        public ShowVolumeHandler(RecordButton outer) {
            mOuterInstance = new WeakReference<>(outer);
        }

        @Override
        public void handleMessage(Message msg) {
            RecordButton outerButton = mOuterInstance.get();
            if (msg.what != -1) {
                // 大于0时 表示当前录音的音量
//                if (outerButton.mVolumeListener != null) {
//                    outerButton.mVolumeListener.onVolumeChange(mRecordDialog, msg.what);
//                }
            } else {
                // -1 时表示录音超时
                outerButton.finishRecord();
            }
        }
    }

    /**
     * 音量改变的监听器
     */
    public interface OnVolumeChangeListener {
        void onVolumeChange(Dialog dialog, int volume);
    }

    public interface OnFinishedRecordListener {
        /**
         * 用户手动取消
         */
        void onCancleRecord();
        /**
         * 录音完成
         */
        void onFinishedRecord(String audioPath, int recordTime);
    }
}
