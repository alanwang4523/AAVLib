package com.alanwang.aav.alvideoeditor.core;

import android.view.Surface;

import com.alanwang.aavlib.libeglcore.common.AAVFrameAvailableListener;
import com.alanwang.aavlib.libeglcore.common.AAVFrameBufferObject;
import com.alanwang.aavlib.libeglcore.common.AAVMessage;
import com.alanwang.aavlib.libeglcore.common.AAVSurfaceTexture;
import com.alanwang.aavlib.libeglcore.engine.AAVMainGLEngine;
import com.alanwang.aavlib.libeglcore.engine.IGLEngineCallback;
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
    private AAVFrameBufferObject mFrameBuffer;
    private AAVMainGLEngine mMainGLEngine;
    private IControllerCallback iControllerCallback;
    private AAVGrayEffect mVideoEffect;

    private volatile boolean mIsPlayerReady = false;
    private volatile boolean mIsSurfaceReady = false;
    private int mVideoWidth;
    private int mVideoHeight;


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
        mFrameBuffer = new AAVFrameBufferObject();
        mAAVSurface = new AAVSurfaceTexture();
        mAAVSurface.setFrameAvailableListener(this);
        mVideoPlayer.setSurface(mAAVSurface.getSurface());
        mVideoEffect = new AAVGrayEffect();
    }

    @Override
    public void onSurfaceUpdate(Surface surface, int width, int height) {
        mIsSurfaceReady = true;
        tryToStartPlay();
    }

    @Override
    public void onRender(AAVMessage msg) {
        mVideoEffect.drawFrame(mFrameBuffer.getOutputTextureId());
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
    }
    //***************** OnPlayReadyListener end ******************

    //***************** AAVFrameAvailableListener start ******************
    @Override
    public void onFrameAvailable(AAVSurfaceTexture surfaceTexture) {
        mFrameBuffer.checkInit(mVideoWidth, mVideoHeight);
        surfaceTexture.drawFrame(mFrameBuffer, mVideoWidth, mVideoHeight);
        mMainGLEngine.postRenderMessage(new AAVMessage(MSG_DRAW));
    }
    //***************** AAVFrameAvailableListener end ******************

    private boolean isAllReady() {
        return mIsPlayerReady && mIsSurfaceReady;
    }

    private void tryToStartPlay() {
        if (isAllReady()) {
            mVideoPlayer.start();
        }
    }
}
