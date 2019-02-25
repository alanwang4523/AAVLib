package com.alanwang.aavlib.libeglcore.common;

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
