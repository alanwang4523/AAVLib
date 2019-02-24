package com.alanwang.aavlib.libvideo.core;

import android.opengl.GLES20;
import android.view.Surface;

import com.alanwang.aavlib.libeglcore.common.AWFrameAvailableListener;
import com.alanwang.aavlib.libeglcore.common.AWFrameBufferObject;
import com.alanwang.aavlib.libeglcore.common.AWMessage;
import com.alanwang.aavlib.libeglcore.common.AWSurfaceTexture;
import com.alanwang.aavlib.libeglcore.engine.AWMainGLEngine;
import com.alanwang.aavlib.libeglcore.engine.IGLEngineCallback;
import com.alanwang.aavlib.libvideo.player.AWVideoPlayer;
import com.alanwang.aavlib.libvideo.player.IVideoPlayer;

/**
 * Author: AlanWang4523.
 * Date: 19/2/16 21:15.
 * Mail: alanwang4523@gmail.com
 */

public class AWVideoPlayController {

    public interface IControllerCallback {
        /**
         * 视频大小确定回调
         * @param width
         * @param height
         * @param duration
         */
        void onPlayReady(int width, int height, long duration);
    }

    private final static int MSG_DRAW = 0x0101;
    private IVideoPlayer mVideoPlayer;
    private AWSurfaceTexture mAAVSurface;
    private AWFrameBufferObject mSrcFrameBuffer;
    private AWMainGLEngine mMainGLEngine;
    private IControllerCallback iControllerCallback;
    private AWVideoPreviewRender mPreviewRender;

    private volatile boolean mIsPlayerReady = false;
    private volatile boolean mIsSurfaceReady = false;
    private int mVideoWidth;
    private int mVideoHeight;

    public AWVideoPlayController() {
        mVideoPlayer = new AWVideoPlayer();
        mVideoPlayer.setOnPlayReadyListener(mOnPlayReadyListener);
        mMainGLEngine = new AWMainGLEngine(mIGLEngineCallback);
        mMainGLEngine.start();
    }

    /**
     * 设置回调
     * @param iControllerCallback
     */
    public void setControllerCallback(IControllerCallback iControllerCallback) {
        this.iControllerCallback = iControllerCallback;
    }

    /**
     * 设置视频路径
     * @param videoPath
     */
    public void setVideoPath(String videoPath) {
        if (mVideoPlayer.isPlaying()) {
            mVideoPlayer.stop();
        }
        mIsPlayerReady = false;
        mVideoPlayer.preparePlayer(videoPath);
    }

    /**
     * 开始、恢复播放
     */
    public void startPlay() {
        mVideoPlayer.resume();
    }

    /**
     * 停止、暂停播放
     */
    public void stopPlay() {
        mVideoPlayer.pause();
    }

    /**
     * 是否正在播放
     * @return
     */
    public boolean isPlaying() {
        return mVideoPlayer.isPlaying();
    }

    /**
     * 获取当前播放进度
     * @return
     */
    public long getCurrentPostion() {
        return mVideoPlayer.getCurrentPosition();
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

    public void release() {
        mMainGLEngine.release();
    }

    private boolean isAllReady() {
        return mIsPlayerReady && mIsSurfaceReady;
    }

    private void tryToStartPlay() {
        if (isAllReady() && !mVideoPlayer.isPlaying()) {
            mVideoPlayer.start();
        }
    }

    private AWFrameAvailableListener mFrameAvailableListener = new AWFrameAvailableListener() {
        @Override
        public void onFrameAvailable(AWSurfaceTexture surfaceTexture) {
            surfaceTexture.drawFrame(mSrcFrameBuffer, mVideoWidth, mVideoHeight);
            mMainGLEngine.postRenderMessage(new AWMessage(MSG_DRAW));
        }
    };

    private IGLEngineCallback mIGLEngineCallback = new IGLEngineCallback() {
        @Override
        public void onEngineStart() {
            mSrcFrameBuffer = new AWFrameBufferObject();
            mAAVSurface = new AWSurfaceTexture();
            mAAVSurface.setFrameAvailableListener(mFrameAvailableListener);
            mVideoPlayer.setSurface(mAAVSurface.getSurface());
            mPreviewRender = new AWVideoPreviewRender();
        }

        @Override
        public void onSurfaceUpdate(Surface surface, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            mPreviewRender.updatePreviewSize(width, height);
            mIsSurfaceReady = true;
            tryToStartPlay();
        }

        @Override
        public void onRender(AWMessage msg) {
            mPreviewRender.draw(mSrcFrameBuffer.getOutputTextureId(), mVideoWidth, mVideoHeight);
        }

        @Override
        public void onSurfaceDestroy() {
            mIsSurfaceReady = false;
        }

        @Override
        public void onEngineRelease() {
            if (mAAVSurface != null) {
                mAAVSurface.release();
            }

            mVideoPlayer.stop();
            mVideoPlayer.release();
            mSrcFrameBuffer.release();
            mPreviewRender.release();
        }
    };

    private IVideoPlayer.OnPlayReadyListener mOnPlayReadyListener = new IVideoPlayer.OnPlayReadyListener() {
        @Override
        public void onPlayReady(int width, int height) {
            mIsPlayerReady = true;
            if (iControllerCallback != null) {
                iControllerCallback.onPlayReady(width, height, mVideoPlayer.getDuration());
            }
            mVideoWidth = width;
            mVideoHeight = height;
            tryToStartPlay();
        }
    };
}
