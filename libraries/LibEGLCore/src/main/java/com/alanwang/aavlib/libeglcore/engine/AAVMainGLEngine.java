package com.alanwang.aavlib.libeglcore.engine;

import android.media.ImageReader;
import android.opengl.EGLContext;
import android.os.Process;
import android.view.Surface;
import com.alanwang.aavlib.libeglcore.common.AAVHandlerThread;
import com.alanwang.aavlib.libeglcore.egl.AAVEGLCoreWrapper;

/**
 * Author: AlanWang4523.
 * Date: 19/1/27 00:11.
 * Mail: alanwang4523@gmail.com
 */

public class AAVMainGLEngine {
    private static final String TAG = AAVMainGLEngine.class.getSimpleName();

    private final AAVEGLCoreWrapper mEGLCoreWrapper;
    private final AAVHandlerThread mHandlerThread;
    private final ImageReader mImageReader;

    public AAVMainGLEngine() {
        mEGLCoreWrapper = new AAVEGLCoreWrapper(null);
        mHandlerThread = new AAVHandlerThread(TAG, Process.THREAD_PRIORITY_FOREGROUND);
        mImageReader = ImageReader.newInstance(1, 1, 1, 1);
    }

    /**
     * 启动 Engine
     */
    public void start() {
        mHandlerThread.start(null);
        mHandlerThread.postTask(new Runnable() {
            @Override
            public void run() {
                mEGLCoreWrapper.createSurface(mImageReader.getSurface());
                mEGLCoreWrapper.makeCurrent();
            }
        });
    }

    /**
     * 获取当前的 EGLContext，其他 EGL 线程可通过该方法来共享 EGLContext
     * @return
     */
    public EGLContext getEGLContext() {
        return mEGLCoreWrapper.getCurEGLContext();
    }

    /**
     * 更新 Surface
     * @param surface
     * @param width
     * @param height
     */
    public void updateSurface(final Surface surface, int width, int height) {
        mHandlerThread.postTask(new Runnable() {
            @Override
            public void run() {
                mEGLCoreWrapper.createSurface(surface);
                mEGLCoreWrapper.makeCurrent();
            }
        });
    }

    /**
     * 销毁 Surface
     */
    public void destroySurface() {
        mHandlerThread.postTask(new Runnable() {
            @Override
            public void run() {
                mEGLCoreWrapper.destroyWindowSurface();
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
                mImageReader.close();
            }
        });
    }

}
