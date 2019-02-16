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

public class AAVVideoPlayController {

    private IVideoPlayer mVideoPlayer;
    private AAVSurfaceTexture mAAVSurface;
    private AAVMainGLEngine mMainGLEngine;


    public AAVVideoPlayController() {
        mVideoPlayer = new AAVVideoPlayer();
        mAAVSurface = new AAVSurfaceTexture();
        mAAVSurface.setFrameAvailableListener(mAAVFrameAvailableListener);
        mMainGLEngine = new AAVMainGLEngine(mGLEngineCallback);
        mMainGLEngine.start();
    }

    public void updateSurface(Surface surface, int w, int h) {
        mMainGLEngine.updateSurface(surface, w, h);
    }

    public void destroySurface() {
        mMainGLEngine.destroySurface();
    }

    private IGLEngineCallback mGLEngineCallback = new IGLEngineCallback() {
        @Override
        public void onEngineStart() {

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
    };

    private AAVFrameAvailableListener mAAVFrameAvailableListener = new AAVFrameAvailableListener() {
        @Override
        public void onFrameAvailable(AAVSurfaceTexture surfaceTexture) {

        }
    };
}
