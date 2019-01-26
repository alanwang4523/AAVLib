package com.alanwang.aavlib.libeglcore.common;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

/**
 * Author: AlanWang4523.
 * Date: 19/1/26 23:23.
 * Mail: alanwang4523@gmail.com
 */

public class AAVHandlerThread {
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

    private static final int MSG_QUIT = -402181;
    private Handler mHandler;
    private final HandlerThread mHandlerThread;

    public AAVHandlerThread(String name) {
        mHandlerThread = new HandlerThread(name);
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
        mHandler.sendEmptyMessage(MSG_QUIT);
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
