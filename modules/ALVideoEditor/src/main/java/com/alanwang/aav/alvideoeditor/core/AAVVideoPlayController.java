package com.alanwang.aav.alvideoeditor.core;

import android.opengl.GLES20;
import android.view.Surface;

import com.alanwang.aavlib.libeglcore.common.AAVFrameAvailableListener;
import com.alanwang.aavlib.libeglcore.common.AAVFrameBufferObject;
import com.alanwang.aavlib.libeglcore.common.AAVMessage;
import com.alanwang.aavlib.libeglcore.common.AAVSurfaceTexture;
import com.alanwang.aavlib.libeglcore.engine.AAVMainGLEngine;
import com.alanwang.aavlib.libeglcore.engine.IGLEngineCallback;
import com.alanwang.aavlib.libeglcore.render.AAVSurfaceRender;
import com.alanwang.aavlib.libvideo.player.AAVVideoPlayer;
import com.alanwang.aavlib.libvideo.player.IVideoPlayer;
import com.alanwang.aavlib.libvideoeffect.effects.AAVGrayEffect;

/**
 * Author: AlanWang4523.
 * Date: 19/2/16 21:15.
 * Mail: alanwang4523@gmail.com
 */

public class AAVVideoPlayController implements
        IVideoPlayer.OnPlayReadyListener,
        IGLEngineCallback,
        AAVFrameAvailableListener {

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
    private AAVSurfaceTexture mAAVSurface;
    private AAVFrameBufferObject mSrcFrameBuffer;
    private AAVFrameBufferObject mEffectFrameBuffer;
    private AAVMainGLEngine mMainGLEngine;
    private IControllerCallback iControllerCallback;
    private AAVGrayEffect mTestEffect;
    private AAVSurfaceRender mVideoRender;

    private volatile boolean mIsPlayerReady = false;
    private volatile boolean mIsSurfaceReady = false;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mViewportWidth;
    private int mViewportHeight;


    public AAVVideoPlayController() {
        mVideoPlayer = new AAVVideoPlayer();
        mVideoPlayer.setOnPlayReadyListener(this);
        mMainGLEngine = new AAVMainGLEngine(this);
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

    //***************** OnPlayReadyListener start ******************
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
    //***************** OnPlayReadyListener end ******************

    //***************** IGLEngineCallback start ******************
    @Override
    public void onEngineStart() {
        mSrcFrameBuffer = new AAVFrameBufferObject();
        mEffectFrameBuffer = new AAVFrameBufferObject();
        mAAVSurface = new AAVSurfaceTexture();
        mAAVSurface.setFrameAvailableListener(this);
        mVideoPlayer.setSurface(mAAVSurface.getSurface());
        mTestEffect = new AAVGrayEffect();
        mVideoRender = new AAVSurfaceRender();
    }

    @Override
    public void onSurfaceUpdate(Surface surface, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        mViewportWidth = width;
        mViewportHeight = height;
        mIsSurfaceReady = true;
        tryToStartPlay();
    }

    @Override
    public void onRender(AAVMessage msg) {
        mEffectFrameBuffer.checkInit(mVideoWidth, mVideoHeight);
        mEffectFrameBuffer.bindFrameBuffer();
        mTestEffect.drawFrame(mSrcFrameBuffer.getOutputTextureId());
        mEffectFrameBuffer.unbindFrameBuffer();

        mVideoRender.drawFrame(mEffectFrameBuffer.getOutputTextureId(), mViewportWidth, mViewportHeight);
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
        mTestEffect.release();
        mVideoRender.release();

        mVideoPlayer.stop();
        mVideoPlayer.release();
        mSrcFrameBuffer.release();
        mEffectFrameBuffer.release();
    }
    //***************** OnPlayReadyListener end ******************

    //***************** AAVFrameAvailableListener start ******************
    @Override
    public void onFrameAvailable(AAVSurfaceTexture surfaceTexture) {
        surfaceTexture.drawFrame(mSrcFrameBuffer, mVideoWidth, mVideoHeight);
        mMainGLEngine.postRenderMessage(new AAVMessage(MSG_DRAW));
    }
    //***************** AAVFrameAvailableListener end ******************

    private boolean isAllReady() {
        return mIsPlayerReady && mIsSurfaceReady;
    }

    private void tryToStartPlay() {
        if (isAllReady() && !mVideoPlayer.isPlaying()) {
            mVideoPlayer.start();
        }
    }
}
