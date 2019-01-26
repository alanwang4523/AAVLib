package com.alanwang.aavlib.libeglcore.common;

/**
 * Author: AlanWang4523.
 * Date: 19/1/27 01:55.
 * Mail: alanwang4523@gmail.com
 */

public class AAVWaitingTask implements Runnable {

    private final Runnable runnable;
    private volatile boolean mIsNeedWait;

    public AAVWaitingTask(Runnable runnable) {
        this.runnable = runnable;
        mIsNeedWait = true;
    }

    @Override
    public void run() {
        runnable.run();
        synchronized (this) {
            mIsNeedWait = false;
            notifyAll();
        }
    }

    /**
     * 等待任务执行完成
     * @param waitMaxTimeMs
     */
    public void waitFor(long waitMaxTimeMs) {
        synchronized (this) {
            if (mIsNeedWait) {
                try {
                    wait(waitMaxTimeMs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
