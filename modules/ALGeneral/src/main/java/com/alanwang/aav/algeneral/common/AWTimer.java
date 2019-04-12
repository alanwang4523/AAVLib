package com.alanwang.aav.algeneral.common;

import android.os.Handler;
import android.os.Message;

/**
 * 定时器
 * Author: AlanWang4523.
 * Date: 19/4/13 00:04.
 * Mail: alanwang4523@gmail.com
 */
public class AWTimer {

    public interface TimerListener {
        void onTimeUpdate();
    }

    private static final int MSG_UPDATE = 1;
    private static final int DEFAULT_TIME_INTERVAL = 50;

    private int mTimeInterval;// 通知间隔
    private Handler mHandler;
    private TimerListener mListener;

    public AWTimer() {
        this(DEFAULT_TIME_INTERVAL);
    }

    public AWTimer(int timeInterval) {
        mTimeInterval = timeInterval;
        mHandler = new Handler(mHandleCallback);
    }

    /**
     * 设置回调
     * @param listener
     */
    public void setTimerListener(TimerListener listener) {
        this.mListener = listener;
    }

    /**
     * 开始定时
     */
    public void start() {
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_UPDATE;
        mHandler.sendMessageDelayed(msg, mTimeInterval);
    }

    /**
     * 停止定时
     */
    public void stop() {
        mHandler.removeMessages(MSG_UPDATE);
    }

    private Handler.Callback mHandleCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_UPDATE) {
                if (mListener != null) {
                    mListener.onTimeUpdate();
                }
                mHandler.removeMessages(MSG_UPDATE);
                start();
            }
            return true;
        }
    };
}
