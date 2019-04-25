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
package com.alanwang.aavlib.libeglcore.render;

import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.view.Surface;
import com.alanwang.aavlib.libeglcore.common.AWCoordinateUtil;
import com.alanwang.aavlib.libeglcore.common.AWFrameAvailableListener;
import com.alanwang.aavlib.libeglcore.common.AWFrameBuffer;
import com.alanwang.aavlib.libeglcore.common.AWMessage;
import com.alanwang.aavlib.libeglcore.common.AWSurfaceTexture;
import com.alanwang.aavlib.libeglcore.common.GLLog;
import com.alanwang.aavlib.libeglcore.common.Type;
import com.alanwang.aavlib.libeglcore.engine.AWMainGLEngine;
import com.alanwang.aavlib.libeglcore.engine.IGLEngineCallback;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 输入、输出 Surface 的代理器，
 * 如：MediaPlayer --> AWIOSurfaceProxy --> SurfaceView，可以在播放器解码完的数据加滤镜后再显示
 * 如：Camera --> AWIOSurfaceProxy --> SurfaceView，可以在相机出来的数据加滤镜后再显示
 * 如：VideoDecoder --> AWIOSurfaceProxy --> VideoEncoder，可以在视频解码器出来的数据加滤镜后再给到视频编码器
 *
 * Author: AlanWang4523.
 * Date: 19/3/26 20:49.
 * Mail: alanwang4523@gmail.com
 */
public class AWIOSurfaceProxy {

    public interface OnInputSurfaceListener {
        /**
         * 输入的 surface ready，可以在此回调将 surface 绑定到播放器或解码器
         * @param surface
         */
        void onInputSurfaceCreated(Surface surface);
        /**
         * 输入的 surface 被销毁
         */
        void onInputSurfaceDestroyed();
    }

    public interface OnOutputSurfaceListener {
        /**
         * 输出的 surface 已发生改变
         * @param surface
         * @param w
         * @param h
         */
        void onOutputSurfaceUpdated(Surface surface, int w, int h);
        /**
         * 输出的 surface 被销毁
         */
        void onOutputSurfaceDestroyed();
    }

    public interface OnPassFilterListener {
        /**
         * 过滤镜回调
         * @param textureId
         * @param width
         * @param height
         * @return 返回过完滤镜后的 textureId
         */
        int onPassFilter(int textureId, int width, int height);
    }

    /**
     * 消息处理回调
     */
    public interface OnMessageListener {
        /**
         * 处理消息
         * @param msg
         */
        void onHandleMessage(AWMessage msg);
    }

    private AWMainGLEngine mMainGLEngine;
    private AWSurfaceTexture mInputSurfaceTexture;
    private AWFrameBuffer mInputFrameBuffer;
    private AWSurfaceRender mOutputSurfaceRender;
    private OnInputSurfaceListener mOnInputSurfaceListener;
    private OnOutputSurfaceListener mOnOutputSurfaceListener;
    private OnPassFilterListener mOnPassFilterListener;
    private OnMessageListener mOnMessageListener;
    private CountDownLatch mCountDownLatch;

    private @Type.ScaleType int scaleType = Type.ScaleType.FIT_XY;
    private volatile boolean mIsNeedUpdateTextureCoordinates = false;
    private volatile boolean mIsSurfaceReady = false;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mViewportWidth;
    private int mViewportHeight;

    public AWIOSurfaceProxy() {
        mCountDownLatch = new CountDownLatch(1);
        mMainGLEngine = new AWMainGLEngine(mIGLEngineCallback);
        mMainGLEngine.start();
    }

    /**
     * 设置回调, 异步返回 Surface，同步获取见 {@link #getInputSurface()}
     * @param onInputSurfaceListener
     */
    public void setOnInputSurfaceListener(OnInputSurfaceListener onInputSurfaceListener) {
        // 避免在 setCallback 之前就已经 ready
        if (onInputSurfaceListener != null && mInputSurfaceTexture != null) {
            onInputSurfaceListener.onInputSurfaceCreated(mInputSurfaceTexture.getSurface());
        }
        this.mOnInputSurfaceListener = onInputSurfaceListener;
    }

    /**
     * 设置输出 surface 回调
     * @param onOutputSurfaceListener
     */
    public void setOnOutputSurfaceListener(OnOutputSurfaceListener onOutputSurfaceListener) {
        this.mOnOutputSurfaceListener = onOutputSurfaceListener;
    }

    /**
     * 设置视频滤镜回调
     * @param onPassFilterListener
     */
    public void setOnPassFilterListener(OnPassFilterListener onPassFilterListener) {
        this.mOnPassFilterListener = onPassFilterListener;
    }

    /**
     * 设置消息回调
     * @param onMessageListener
     */
    public void setOnMessageListener(OnMessageListener onMessageListener) {
        this.mOnMessageListener = onMessageListener;
    }

    /**
     * 获取共享的 EGLContext
     * @return
     */
    public EGLContext getSharedEGLContext() {
        return mMainGLEngine.getEGLContext();
    }

    /**
     * 同步获取输入的 surface
     * @return
     */
    public Surface getInputSurface() {
        Surface surface = null;
        try {
            mCountDownLatch.await(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return surface;
        }
        if (mInputSurfaceTexture != null) {
            surface = mInputSurfaceTexture.getSurface();
        }
        return surface;
    }

    /**
     * 同步获取输入的 mInputSurfaceTexture
     * @return
     */
    public AWSurfaceTexture getInputSurfaceTexture() {
        try {
            mCountDownLatch.await(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        if (mInputSurfaceTexture != null) {
            return mInputSurfaceTexture;
        }
        return null;
    }

    /**
     * 设置缩放模式，目前支持：FIT_XY, CENTER_CROP
     * @param scaleType
     */
    public void setScaleType(@Type.ScaleType int scaleType) {
        if (this.scaleType != scaleType) {
            mIsNeedUpdateTextureCoordinates = true;
        }
        this.scaleType = scaleType;
    }

    /**
     * 设置视频源的大小
     * @param videoWidth
     * @param videoHeight
     */
    public void setTextureSize(int videoWidth, int videoHeight) {
        if (this.mVideoWidth != videoWidth || this.mVideoHeight != videoHeight) {
            mIsNeedUpdateTextureCoordinates = true;
        }
        this.mVideoWidth = videoWidth;
        this.mVideoHeight = videoHeight;
    }

    /**
     * 发送消息到 GL 线程
     * @param msg
     */
    public void postMessage(AWMessage msg) {
        mMainGLEngine.postMessage(msg);
    }

    /**
     * 更新 surface
     * @param surface
     * @param w
     * @param h
     */
    public void updateSurface(Surface surface, int w, int h) {
        mMainGLEngine.updateSurface(surface, w, h);
    }

    /**
     * 销毁 surface
     */
    public void destroySurface() {
        mMainGLEngine.destroySurface();
    }

    /**
     * 释放资源
     */
    public void release() {
        mMainGLEngine.release();
    }

    private AWFrameAvailableListener mFrameAvailableListener = new AWFrameAvailableListener() {
        @Override
        public void onFrameAvailable(AWSurfaceTexture surfaceTexture) {
            if (!mIsSurfaceReady || mInputFrameBuffer == null || mVideoWidth == 0 || mVideoHeight == 0) {
                StringBuilder strB = new StringBuilder();
                strB.append("onFrameAvailable()-->Illegal Arguments : ")
                        .append("mIsSurfaceReady = ").append(mIsSurfaceReady)
                        .append(",mVideoWidth = ").append(mVideoWidth)
                        .append(", mVideoHeight = ").append(mVideoHeight);
                GLLog.e(strB.toString());

                surfaceTexture.updateTexImage();
                return;
            }
            surfaceTexture.drawFrame(mInputFrameBuffer, mVideoWidth, mVideoHeight);
            mMainGLEngine.postMessage(new AWMessage(AWMessage.MSG_DRAW));
        }
    };

    private IGLEngineCallback mIGLEngineCallback = new IGLEngineCallback() {
        @Override
        public void onEngineStart() {
            GLLog.d("onEngineStart()--->>");

            mInputFrameBuffer = new AWFrameBuffer();
            mInputSurfaceTexture = new AWSurfaceTexture();
            mInputSurfaceTexture.setFrameAvailableListener(mFrameAvailableListener);
            mOutputSurfaceRender = new AWSurfaceRender();
            mCountDownLatch.countDown();

            if (mOnInputSurfaceListener != null) {
                mOnInputSurfaceListener.onInputSurfaceCreated(mInputSurfaceTexture.getSurface());
            }
        }

        @Override
        public void onSurfaceUpdate(Surface surface, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            if (mViewportWidth != width || mViewportHeight != height) {
                mIsNeedUpdateTextureCoordinates = true;
            }
            mViewportWidth = width;
            mViewportHeight = height;
            mOutputSurfaceRender.updateViewSize(width, height);

            if (mOnOutputSurfaceListener != null) {
                mOnOutputSurfaceListener.onOutputSurfaceUpdated(surface, width, height);
            }
            mIsSurfaceReady = true;

            StringBuilder strB = new StringBuilder();
            strB.append("onSurfaceUpdate()-->")
                    .append("width = ").append(width)
                    .append(", height = ").append(height);
            GLLog.d(strB.toString());

            // 确保在切换输入surface ready 后把最新的一帧数据更新到输出 surface
            if (mOutputSurfaceRender != null && mInputFrameBuffer != null
                    && mVideoWidth > 0 && mVideoHeight > 0) {
                mMainGLEngine.postMessage(new AWMessage(AWMessage.MSG_DRAW));
            }
        }

        @Override
        public void onRender(AWMessage msg) {
            if (mIsNeedUpdateTextureCoordinates) {
                if (scaleType == Type.ScaleType.CENTER_CROP) {
                    mOutputSurfaceRender.updateTextureCoordinates(AWCoordinateUtil.getCenterCropTextureCoordinates(
                            mVideoWidth, mVideoHeight, mViewportWidth, mViewportHeight));
                } else {
                    mOutputSurfaceRender.updateTextureCoordinates(AWCoordinateUtil.DEFAULT_TEXTURE_COORDS);
                }
                mIsNeedUpdateTextureCoordinates = false;
            }
            int outputTextureId = mInputFrameBuffer.getOutputTextureId();
            if (mOnPassFilterListener != null) {
                outputTextureId = mOnPassFilterListener.onPassFilter(
                        mInputFrameBuffer.getOutputTextureId(), mVideoWidth, mVideoHeight);
            }
            mOutputSurfaceRender.drawFrame(outputTextureId, mViewportWidth, mViewportHeight);
        }

        @Override
        public void onHandleMsg(AWMessage msg) {
            if (mOnMessageListener != null) {
                mOnMessageListener.onHandleMessage(msg);
            }
        }

        @Override
        public void onSurfaceDestroy() {
            GLLog.d("onSurfaceDestroy()--->>");
            mIsSurfaceReady = false;
            if (mOnOutputSurfaceListener != null) {
                mOnOutputSurfaceListener.onOutputSurfaceDestroyed();
            }
        }

        @Override
        public void onEngineRelease() {
            GLLog.d("onEngineRelease()--->>");
            if (mInputSurfaceTexture != null) {
                mInputSurfaceTexture.release();
                mInputSurfaceTexture = null;
            }
            if (mInputFrameBuffer != null) {
                mInputFrameBuffer.release();
                mInputFrameBuffer = null;
            }
            if (mOutputSurfaceRender != null) {
                mOutputSurfaceRender.release();
                mOutputSurfaceRender = null;
            }
            if (mOnInputSurfaceListener != null) {
                mOnInputSurfaceListener.onInputSurfaceDestroyed();
            }
        }
    };
}
