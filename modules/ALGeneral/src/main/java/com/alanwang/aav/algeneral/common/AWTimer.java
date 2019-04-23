/**
 * Copyright (c) 2019-present, AlanWang4523 (alanwang4523@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
