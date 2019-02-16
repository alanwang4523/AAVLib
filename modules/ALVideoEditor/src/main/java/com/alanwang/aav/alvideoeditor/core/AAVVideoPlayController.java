package com.alanwang.aav.alvideoeditor.core;

import android.view.Surface;

import com.alanwang.aavlib.libeglcore.common.AAVFrameAvailableListener;
import com.alanwang.aavlib.libeglcore.common.AAVMessage;
import com.alanwang.aavlib.libeglcore.common.AAVSurfaceTexture;
import com.alanwang.aavlib.libeglcore.engine.AAVMainGLEngine;
import com.alanwang.aavlib.libeglcore.engine.IGLEngineCallback;
import com.alanwang.aavlib.libvideo.player.AAVVideoPlayer;
import com.alanwang.aavlib.libvideo.player.IVideoPlayer;

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
         */
        void onVideoSizeChanged(int width, int height);
    }

    private IVideoPlayer mVideoPlayer;
    private AAVSurfaceTexture mAAVSurface;
    private AAVMainGLEngine mMainGLEngine;
    private IControllerCallback iControllerCallback;


    public AAVVideoPlayController() {
        mVideoPlayer = new AAVVideoPlayer();
        mVideoPlayer.setOnPlayReadyListener(this);
        mMainGLEngine = new AAVMainGLEngine(this);
        mMainGLEngine.start();
    }

    public void setiControllerCallback(IControllerCallback iControllerCallback) {
        this.iControllerCallback = iControllerCallback;
    }

    public void setVideoPath(String videoPath) {
        if (mVideoPlayer.isPlaying()) {
            mVideoPlayer.stop();
        }
        mVideoPlayer.preparePlayer(videoPath);
    }

    public void updateSurface(Surface surface, int w, int h) {
        mMainGLEngine.updateSurface(surface, w, h);
    }

    public void destroySurface() {
        mMainGLEngine.destroySurface();
    }

    @Override
    public void onPlayReady(int width, int height) {
        if (iControllerCallback != null) {
            iControllerCallback.onVideoSizeChanged(width, height);
        }
    }

    @Override
    public void onEngineStart() {
        mAAVSurface = new AAVSurfaceTexture();
        mAAVSurface.setFrameAvailableListener(this);
        mVideoPlayer.setSurface(mAAVSurface.getSurface());
    }

    @Override
    public void onSurfaceUpdate(Surface surface, int width, int height) {

    }

    @Override
    public void onRender(AAVMessage msg) {

    }

    @Override
    public void onSurfaceDestroy() {

    }

    @Override
    public void onEngineRelease() {

    }

    @Override
    public void onFrameAvailable(AAVSurfaceTexture surfaceTexture) {

    }
}
