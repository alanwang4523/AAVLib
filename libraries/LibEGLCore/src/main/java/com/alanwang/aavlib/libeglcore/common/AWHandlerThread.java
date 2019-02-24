package com.alanwang.aavlib.libeglcore.common;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

/**
 * Author: AlanWang4523.
 * Date: 19/1/26 23:23.
 * Mail: alanwang4523@gmail.com
 */

public class AWHandlerThread {
    /**
     * 消息回调接口
     */
    public interface IMsgCallback {
        /**
         * 处理消息
         * @param msg 消息体
         */
        void handleMsg(Message msg);
    }

    private static final int MSG_QUIT = 402181;
    private final HandlerThread mHandlerThread;
    private Handler mHandler;

    public AWHandlerThread(String name) {
        this(name, Process.THREAD_PRIORITY_DEFAULT);
    }

    public AWHandlerThread(String name, int priority) {
        mHandlerThread = new HandlerThread(name, priority);
    }

    /**
     * 启动线程
     * @param callback
     */
    public void start(IMsgCallback callback) {
        if (mHandler == null) {
            mHandlerThread.start();
            mHandler = new InternalHandler(callback, mHandlerThread.getLooper());
        }
    }

    /**
     * 停止线程
     */
    public void stop() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(MSG_QUIT);
        }
    }

    /**
     * 发送一个只含 what 的消息
     * @param what
     */
    public void postMessage(int what) {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(what);
        }
    }

    /**
     * 发送一个含 what 和 object 的消息
     * @param what
     * @param object
     */
    public void postMessage(int what, Object object) {
        if (mHandler != null) {
            mHandler.sendMessage(mHandler.obtainMessage(what, object));
        }
    }

    /**
     * 发送一个任务到子线程执行
     * @param runnable
     */
    public void postTask(Runnable runnable) {
        if (mHandler != null) {
            if (Looper.myLooper() == mHandler.getLooper()) {
                runnable.run();
                return;
            }
            mHandler.post(runnable);
        }
    }

    private static class InternalHandler extends Handler {
        private IMsgCallback mMsgCallback;

        public InternalHandler(IMsgCallback callback, Looper looper) {
            super(looper);
            this.mMsgCallback = callback;
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_QUIT) {
                Looper looper = Looper.myLooper();
                if (looper != null) {
                    looper.quit();
                }
                return;
            }
            if (mMsgCallback != null) {
                mMsgCallback.handleMsg(msg);
            }
        }
    }
}
