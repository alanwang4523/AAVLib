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
package com.alanwang.aavlib.libeglcore.common;

/**
 * Author: AlanWang4523.
 * Date: 19/1/27 01:55.
 * Mail: alanwang4523@gmail.com
 */

public class AWWaitingTask implements Runnable {

    private final Runnable runnable;
    private volatile boolean mIsNeedWait;

    public AWWaitingTask(Runnable runnable) {
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
