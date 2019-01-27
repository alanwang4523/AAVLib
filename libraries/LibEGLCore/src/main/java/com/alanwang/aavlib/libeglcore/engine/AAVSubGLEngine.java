package com.alanwang.aavlib.libeglcore.engine;

import android.opengl.EGLContext;
import android.os.Process;
import android.view.Surface;

import com.alanwang.aavlib.libeglcore.common.AAVHandlerThread;
import com.alanwang.aavlib.libeglcore.egl.AAVEGLCoreWrapper;

/**
 * Author: AlanWang4523.
 * Date: 19/1/27 02:02.
 * Mail: alanwang4523@gmail.com
 */

public class AAVSubGLEngine {
    private static final String TAG = AAVMainGLEngine.class.getSimpleName();

    private final AAVEGLCoreWrapper mEGLCoreWrapper;
    private final AAVHandlerThread mHandlerThread;

    public AAVSubGLEngine(EGLContext eglContext) {
        mEGLCoreWrapper = new AAVEGLCoreWrapper(eglContext);
        mHandlerThread = new AAVHandlerThread(TAG, Process.THREAD_PRIORITY_FOREGROUND);
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
