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

    private Handler mHandler;
    private final HandlerThread mHandlerThread;

    public AAVHandlerThread(String name) {
        mHandlerThread = new HandlerThread(name);
    }

    public void start(IMsgCallback callback) {
        if (mHandler == null) {
            mHandlerThread.start();
            mHandler = new InternalHandler(callback, mHandlerThread.getLooper());
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
            if (mMsgCallback != null) {
                mMsgCallback.handleMsg(msg);
            }
        }
    }
}
