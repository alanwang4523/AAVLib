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
package com.alanwang.aavlib.video.core;

import android.view.Surface;
import com.alanwang.aavlib.image.filters.common.FilterCategory;
import com.alanwang.aavlib.image.filters.common.FilterType;
import com.alanwang.aavlib.image.processors.AWFilterProcessor;
import com.alanwang.aavlib.opengl.render.AWIOSurfaceProxy;
import com.alanwang.aavlib.video.player.AWVideoPlayer;
import com.alanwang.aavlib.video.player.IVideoPlayer;

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
    private AWFilterProcessor mFilterProcessor;

    public AWVideoPlayController() {
        mVideoPlayer = new AWVideoPlayer();
        mIOSurfaceProxy = new AWIOSurfaceProxy();
        mFilterProcessor = new AWFilterProcessor(new int[]{FilterCategory.FC_STYLE});

        mIOSurfaceProxy.setOnInputSurfaceListener(new AWIOSurfaceProxy.OnInputSurfaceListener() {
            @Override
            public void onInputSurfaceCreated(Surface surface) {
                mFilterProcessor.initialize();
            }

            @Override
            public void onInputSurfaceDestroyed() {
                mFilterProcessor.release();
            }
        });
        mIOSurfaceProxy.setOnPassFilterListener(new AWIOSurfaceProxy.OnPassFilterListener() {
            @Override
            public int onPassFilter(int textureId, int width, int height) {
                return mFilterProcessor.processFrame(textureId, width, height);
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
     * 设置滤镜参数
     * @param filterType
     * @param level
     */
    public void setFilter(@FilterType int filterType, float level) {
        mFilterProcessor.setFilter(filterType, level);
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
