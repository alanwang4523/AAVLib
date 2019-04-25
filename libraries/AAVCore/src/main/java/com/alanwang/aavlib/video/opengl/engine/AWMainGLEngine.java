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
package com.alanwang.aavlib.video.opengl.engine;

import android.graphics.PixelFormat;
import android.media.ImageReader;
import android.opengl.EGLContext;
import android.os.Process;
import android.support.annotation.NonNull;
import android.view.Surface;
import com.alanwang.aavlib.video.opengl.common.AWHandlerThread;
import com.alanwang.aavlib.video.opengl.common.AWMessage;
import com.alanwang.aavlib.video.opengl.egl.AWEGLCoreWrapper;
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
    private final Deque<AWMessage> mMessageDeque;
    private final MessageTask mMessageTask;
    private IGLEngineCallback mEngineCallback;
    private volatile boolean mIsEngineValid = false;

    public AWMainGLEngine(@NonNull IGLEngineCallback callback) {
        mEGLCoreWrapper = new AWEGLCoreWrapper(null);
        mHandlerThread = new AWHandlerThread(TAG, Process.THREAD_PRIORITY_FOREGROUND);
        mImageReader = ImageReader.newInstance(1, 1, PixelFormat.RGBA_8888, 1);
        mMessageDeque = new ArrayDeque<>();
        mMessageTask = new MessageTask(this);
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
    public void postMessage(AWMessage msg) {
        if (mIsEngineValid) {
            synchronized (mMessageDeque) {
                if (mMessageDeque.size() > 100) {
                    mMessageDeque.clear();
                }
                mMessageDeque.add(msg);
            }
            mHandlerThread.postTask(mMessageTask);
        }
    }

    /**
     * 真正的处理渲染消息
     * @param msg
     */
    private void handleMsg(AWMessage msg) {
        if (msg.msgWhat == AWMessage.MSG_DRAW) {
            if (mEGLCoreWrapper.isEGLContextValid()) {
                mEGLCoreWrapper.makeCurrent();
                mEngineCallback.onRender(msg);
                mEGLCoreWrapper.swapBuffers();
            }
        } else {
            mEngineCallback.onHandleMsg(msg);
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
    private static class MessageTask implements  Runnable {
        private final AWMainGLEngine mMainGLEngine;

        public MessageTask(AWMainGLEngine mainGLEngine) {
            this.mMainGLEngine = mainGLEngine;
        }

        @Override
        public void run() {
            AWMessage msg;
            synchronized (mMainGLEngine.mMessageDeque) {
                msg = mMainGLEngine.mMessageDeque.poll();
            }
            if (msg != null) {
                mMainGLEngine.handleMsg(msg);
            }
        }
    }

}
