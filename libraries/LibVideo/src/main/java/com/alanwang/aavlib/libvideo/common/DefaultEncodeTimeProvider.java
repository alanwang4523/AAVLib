package com.alanwang.aavlib.libvideo.common;

/**
 * Author: wangjianjun.
 * Date: 19/1/3 20:31.
 * Mail: alanwang6584@gmail.com
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
