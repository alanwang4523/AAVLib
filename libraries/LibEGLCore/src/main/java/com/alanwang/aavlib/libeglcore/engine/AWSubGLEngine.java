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
