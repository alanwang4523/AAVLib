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
package com.alanwang.aavlib.libeglcore.engine;

import android.opengl.EGLContext;
import android.os.Process;
import android.view.Surface;

import com.alanwang.aavlib.libeglcore.common.AWHandlerThread;
import com.alanwang.aavlib.libeglcore.egl.AWEGLCoreWrapper;

/**
 * Author: AlanWang4523.
 * Date: 19/1/27 02:02.
 * Mail: alanwang4523@gmail.com
 */

public class AWSubGLEngine {
    private static final String TAG = AWMainGLEngine.class.getSimpleName();

    private final AWEGLCoreWrapper mEGLCoreWrapper;
    private final AWHandlerThread mHandlerThread;

    public AWSubGLEngine(EGLContext eglContext) {
        mEGLCoreWrapper = new AWEGLCoreWrapper(eglContext);
        mHandlerThread = new AWHandlerThread(TAG, Process.THREAD_PRIORITY_FOREGROUND);
    }

    /**
     * 启动 Engine
     */
    public void start() {
        mHandlerThread.start(null);
    }

    /**
     * 提交异步任务到 GL Thread
     * @param runnable
     */
    public void postRunnable(Runnable runnable) {
        mHandlerThread.postTask(runnable);
    }

    /**
     * 更新 Surface
     * @param surface
     */
    public void updateSurface(final Surface surface) {
        mHandlerThread.postTask(new Runnable() {
            @Override
            public void run() {
                mEGLCoreWrapper.createSurface(surface);
                mEGLCoreWrapper.makeCurrent();
            }
        });
    }

    /**
     * 释放资源
     */
    public void release() {
        mHandlerThread.postTask(new Runnable() {
            @Override
            public void run() {
                mEGLCoreWrapper.release();
            }
        });
        mHandlerThread.stop();
    }
}
