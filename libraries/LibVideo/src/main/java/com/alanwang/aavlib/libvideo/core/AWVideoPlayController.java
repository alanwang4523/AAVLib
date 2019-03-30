package com.alanwang.aavlib.libvideo.core;

import android.view.Surface;
import com.alanwang.aavlib.libeglcore.common.AWFrameBufferObject;
import com.alanwang.aavlib.libeglcore.render.AWIOSurfaceProxy;
import com.alanwang.aavlib.libvideo.player.AWVideoPlayer;
import com.alanwang.aavlib.libvideo.player.IVideoPlayer;
import com.alanwang.aavlib.libvideoeffect.effects.AWGrayEffect;

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

    private IVideoPlayer mVideoPlayer;
    private IControllerCallback iControllerCallback;
    private AWIOSurfaceProxy mIOSurfaceProxy;
    private AWFrameBufferObject mEffectFrameBuffer;
    private AWGrayEffect mTestEffect;

    public AWVideoPlayController() {
        mVideoPlayer = new AWVideoPlayer();
        mIOSurfaceProxy = new AWIOSurfaceProxy();

        mIOSurfaceProxy.setOnInputSurfaceListener(new AWIOSurfaceProxy.OnInputSurfaceListener() {
            @Override
            public void onInputSurfaceCreated(Surface surface) {
                mEffectFrameBuffer = new AWFrameBufferObject();
                mTestEffect = new AWGrayEffect();
            }

            @Override
            public void onInputSurfaceDestroyed() {
                mEffectFrameBuffer.release();
                mTestEffect.release();
            }
        });
        mIOSurfaceProxy.setOnPassFilterListener(new AWIOSurfaceProxy.OnPassFilterListener() {
            @Override
            public int onPassFilter(int textureId, int width, int height) {
                mEffectFrameBuffer.checkInit(width, height);
                mEffectFrameBuffer.bindFrameBuffer();
                mTestEffect.drawFrame(textureId);
                mEffectFrameBuffer.unbindFrameBuffer();
                return mEffectFrameBuffer.getOutputTextureId();
            }
        });

        mVideoPlayer.setSurface(mIOSurfaceProxy.getInputSurface());
        mVideoPlayer.setOnPlayReadyListener(new IVideoPlayer.OnPlayReadyListener() {
            @Override
            public void onPlayReady(int width, int height) {
                mIOSurfaceProxy.setTextureSize(width, height);
                if (iControllerCallback != null) {
                    iControllerCallback.onPlayReady(width, height, mVideoPlayer.getDuration());
                }
            }
        });
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
        mIOSurfaceProxy.updateSurface(surface, w, h);
    }

    /**
     * 销毁 surface
     */
    public void destroySurface() {
        mIOSurfaceProxy.destroySurface();
    }

    public void release() {
        mIOSurfaceProxy.release();
    }
}
