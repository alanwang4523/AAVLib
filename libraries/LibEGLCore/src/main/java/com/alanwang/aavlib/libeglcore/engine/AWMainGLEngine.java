package com.alanwang.aavlib.libeglcore.engine;

import android.media.ImageReader;
import android.opengl.EGLContext;
import android.os.Process;
import android.support.annotation.NonNull;
import android.view.Surface;
import com.alanwang.aavlib.libeglcore.common.AWHandlerThread;
import com.alanwang.aavlib.libeglcore.common.AWMessage;
import com.alanwang.aavlib.libeglcore.egl.AWEGLCoreWrapper;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Author: AlanWang4523.
 * Date: 19/1/27 00:11.
 * Mail: alanwang4523@gmail.com
 */

public class AWMainGLEngine {
    private static final String TAG = AWMainGLEngine.class.getSimpleName();

    private final AWEGLCoreWrapper mEGLCoreWrapper;
    private final AWHandlerThread mHandlerThread;
    private final ImageReader mImageReader;
    private final Deque<AWMessage> mRenderTaskDeque;
    private final RenderTask mRenderTask;
    private IGLEngineCallback mEngineCallback;
    private volatile boolean mIsEngineValid = false;

    public AWMainGLEngine(@NonNull IGLEngineCallback callback) {
        mEGLCoreWrapper = new AWEGLCoreWrapper(null);
        mHandlerThread = new AWHandlerThread(TAG, Process.THREAD_PRIORITY_FOREGROUND);
        mImageReader = ImageReader.newInstance(1, 1, 1, 1);
        mRenderTaskDeque = new ArrayDeque<>();
        mRenderTask = new RenderTask(this);
        mEngineCallback = callback;
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
                mIsEngineValid = true;
                mEngineCallback.onEngineStart();
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
    public void updateSurface(final Surface surface, final int width, final int height) {
        mHandlerThread.postTask(new Runnable() {
            @Override
            public void run() {
                mEGLCoreWrapper.createSurface(surface);
                mEGLCoreWrapper.makeCurrent();
                mIsEngineValid = true;
                mEngineCallback.onSurfaceUpdate(surface, width, height);
            }
        });
    }

    /**
     * 发送一个渲染消息
     * @param msg
     */
    public void postRenderMessage(AWMessage msg) {
        if (mIsEngineValid) {
            synchronized (mRenderTaskDeque) {
                if (mRenderTaskDeque.size() > 100) {
                    mRenderTaskDeque.clear();
                }
                mRenderTaskDeque.add(msg);
            }
            mHandlerThread.postTask(mRenderTask);
        }
    }

    /**
     * 真正的处理渲染消息
     * @param msg
     */
    private void handleRenderMsg(AWMessage msg) {
        if (mEGLCoreWrapper.isEGLContextValid()) {
            mEGLCoreWrapper.makeCurrent();
            mEngineCallback.onRender(msg);
            mEGLCoreWrapper.swapBuffers();
        }
    }

    /**
     * 销毁 Surface
     */
    public void destroySurface() {
        mHandlerThread.postTask(new Runnable() {
            @Override
            public void run() {
                mIsEngineValid = false;
                mEGLCoreWrapper.destroyWindowSurface();
                mEngineCallback.onSurfaceDestroy();
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
                mIsEngineValid = false;
                mEngineCallback.onEngineRelease();
                mEGLCoreWrapper.release();
                mImageReader.close();
            }
        });
        mHandlerThread.stop();
    }

    /**
     * 渲染任务
     */
    private static class RenderTask implements  Runnable {
        private final AWMainGLEngine mMainGLEngine;

        public RenderTask(AWMainGLEngine mainGLEngine) {
            this.mMainGLEngine = mainGLEngine;
        }

        @Override
        public void run() {
            AWMessage msg;
            synchronized (mMainGLEngine.mRenderTaskDeque) {
                msg = mMainGLEngine.mRenderTaskDeque.poll();
            }
            if (msg != null) {
                mMainGLEngine.handleRenderMsg(msg);
            }
        }
    }

}
