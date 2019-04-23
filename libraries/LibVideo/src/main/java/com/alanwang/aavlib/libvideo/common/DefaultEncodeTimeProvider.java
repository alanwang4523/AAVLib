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
package com.alanwang.aavlib.libvideo.common;

/**
 * Author: AlanWang4523.
 * Date: 19/1/3 20:31.
 * Mail: alanwang4523@gmail.com
 */

public class DefaultEncodeTimeProvider implements IEncodeTimeProvider {

    private boolean isFirstTimeStampGot = false;
    private long mStartTime = 0L;
    private long mLastTimestamp = 0L;
    private long mLastPauseTimeMs = 0;

    /**
     * 暂停录制
     */
    public void pauseRecord() {
        mLastPauseTimeMs = System.currentTimeMillis();
    }

    /**
     * 恢复录制
     */
    public void resumeRecord() {
        mStartTime += (System.currentTimeMillis() - mLastPauseTimeMs);
    }

    /**
     * 重置
     */
    public void reset() {
        isFirstTimeStampGot = false;
        mStartTime = 0L;
        mLastTimestamp = 0L;
    }

    @Override
    public long getTimeStampMS() {
        long timestamp;
        if (!isFirstTimeStampGot) {
            isFirstTimeStampGot = true;
            mStartTime = System.currentTimeMillis();
            timestamp = 1L;
        } else {
            timestamp = System.currentTimeMillis() - mStartTime;
        }

        // 保证时间戳递增
        if (timestamp <= mLastTimestamp) {
            timestamp = mLastTimestamp + 1L;
        }
        mLastTimestamp = timestamp;
        return timestamp;
    }
}
