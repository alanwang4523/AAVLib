package com.alanwang.aavlib.libeglcore.render;

import android.opengl.GLES20;
import android.view.Surface;

import com.alanwang.aavlib.libeglcore.common.AWCoordinateUtil;
import com.alanwang.aavlib.libeglcore.common.AWFrameAvailableListener;
import com.alanwang.aavlib.libeglcore.common.AWFrameBufferObject;
import com.alanwang.aavlib.libeglcore.common.AWMessage;
import com.alanwang.aavlib.libeglcore.common.AWSurfaceTexture;
import com.alanwang.aavlib.libeglcore.common.Type;
import com.alanwang.aavlib.libeglcore.engine.AWMainGLEngine;
import com.alanwang.aavlib.libeglcore.engine.IGLEngineCallback;

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

    public interface OnInputSurfaceReadyListener {
        /**
         * 输入的 surface ready，可以在此回调将 surface 绑定到播放器或解码器
         * @param surface
         */
        void onInputSurfaceReady(Surface surface);
    }

    private AWSurfaceTexture mAAVSurfaceTexture;
    private AWFrameBufferObject mSrcFrameBuffer;
    private AWMainGLEngine mMainGLEngine;
    private AWSurfaceRender mSurfaceRender;
    private OnInputSurfaceReadyListener mOnInputSurfaceReadyListener;

    private @Type.ScaleType int scaleType = Type.ScaleType.FIT_XY;
    private volatile boolean mIsNeedUpdateTextureCoordinates = false;
    private volatile boolean mIsSurfaceReady = false;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mViewportWidth;
    private int mViewportHeight;

    public AWIOSurfaceProxy() {
        mMainGLEngine = new AWMainGLEngine(mIGLEngineCallback);
        mMainGLEngine.start();
    }

    /**
     * 设置回调
     * @param onInputSurfaceReadyListener
     */
    public void setOnInputSurfaceReadyListener(OnInputSurfaceReadyListener onInputSurfaceReadyListener) {
        // 避免在 setCallback 之前就已经 ready
        if (onInputSurfaceReadyListener != null && mAAVSurfaceTexture != null) {
            onInputSurfaceReadyListener.onInputSurfaceReady(mAAVSurfaceTexture.getSurface());
        }
        this.mOnInputSurfaceReadyListener = onInputSurfaceReadyListener;
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
            if (!mIsSurfaceReady || mSrcFrameBuffer == null || mVideoWidth == 0 || mVideoHeight == 0) {
                surfaceTexture.updateTexImage();
                return;
            }
            surfaceTexture.drawFrame(mSrcFrameBuffer, mVideoWidth, mVideoHeight);
            mMainGLEngine.postRenderMessage(new AWMessage(AWMessage.MSG_DRAW));
        }
    };

    private IGLEngineCallback mIGLEngineCallback = new IGLEngineCallback() {
        @Override
        public void onEngineStart() {
            mSrcFrameBuffer = new AWFrameBufferObject();
            mAAVSurfaceTexture = new AWSurfaceTexture();
            mAAVSurfaceTexture.setFrameAvailableListener(mFrameAvailableListener);
            mSurfaceRender = new AWSurfaceRender();

            if (mOnInputSurfaceReadyListener != null) {
                mOnInputSurfaceReadyListener.onInputSurfaceReady(mAAVSurfaceTexture.getSurface());
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
            mIsSurfaceReady = true;
        }

        @Override
        public void onRender(AWMessage msg) {
            if (mIsNeedUpdateTextureCoordinates) {
                if (scaleType == Type.ScaleType.CENTER_CROP) {
                    mSurfaceRender.updateTextureCoord(AWCoordinateUtil.getCenterCropTextureCoords(
                            mVideoWidth, mVideoHeight, mViewportWidth, mViewportHeight));
                } else {
                    mSurfaceRender.updateTextureCoord(AWCoordinateUtil.DEFAULT_TEXTURE_COORDS);
                }
                mIsNeedUpdateTextureCoordinates = false;
            }
            mSurfaceRender.drawFrame(mSrcFrameBuffer.getOutputTextureId(), mViewportWidth, mViewportHeight);
        }

        @Override
        public void onSurfaceDestroy() {
            mIsSurfaceReady = false;
        }

        @Override
        public void onEngineRelease() {
            if (mAAVSurfaceTexture != null) {
                mAAVSurfaceTexture.release();
                mAAVSurfaceTexture = null;
            }
            if (mSrcFrameBuffer != null) {
                mSrcFrameBuffer.release();
                mSrcFrameBuffer = null;
            }
            if (mSurfaceRender != null) {
                mSurfaceRender.release();
                mSurfaceRender = null;
            }
        }
    };
}
