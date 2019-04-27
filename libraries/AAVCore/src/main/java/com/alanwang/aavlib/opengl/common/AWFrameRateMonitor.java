/*
 * Copyright (c) 2019-present AlanWang4523 <alanwang4523@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alanwang.aavlib.opengl.common;

/**
 * Author: AlanWang4523.
 * Date: 19/2/25 23:52.
 * Mail: alanwang4523@gmail.com
 */
public class AWFrameRateMonitor {
    private final static long INTERVAL_TIME_MS = 1000;

    private long mLastTimeMS = 0;
    private int mFrameCount = 0;
    private float mCurrentFPS = 0.0f;

    /**
     * 增加一帧计数
     */
    public void frameIncrease() {
        mFrameCount++;
        long currentTime = System.currentTimeMillis();
        if ((currentTime - mLastTimeMS) > INTERVAL_TIME_MS) {
            mCurrentFPS = mFrameCount / ((currentTime - mLastTimeMS) / 1000.f);
            mCurrentFPS = (float)(Math.round(mCurrentFPS * 100)) / 100;//保留两位小数
            mFrameCount = 0;
            mLastTimeMS = currentTime;
        }
    }

    /**
     * 获取当前帧率
     * @return
     */
    public float getFPS() {
        return mCurrentFPS;
    }

    /**
     * 重置
     */
    public void reset() {
        mLastTimeMS = 0;
        mFrameCount = 0;
        mCurrentFPS = 0.0f;
    }
}
