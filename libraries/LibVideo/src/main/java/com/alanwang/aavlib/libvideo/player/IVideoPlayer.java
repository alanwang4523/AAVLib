package com.alanwang.aavlib.libvideo.player;

import android.view.Surface;

/**
 * Author: AlanWang4523.
 * Date: 19/1/25 00:48.
 * Mail: alanwang4523@gmail.com
 */

public interface IVideoPlayer {

    /**
     * SeekCompleteListener
     */
    interface OnSeekCompleteListener {
        /**
         * seek 结束后回调
         */
        void onSeekComplete();
    }

    /**
     * PlayReadyListener
     */
    interface OnPlayReadyListener {
        /**
         * 视频资源准备好后回调
         * @param width
         * @param height
         */
        void onPlayReady(int width, int height);
    }

    /**
     * 准备Player
     */
    void preparePlayer(String videoPath);

    /**
     * 设置视频画面显示的Surface
     * @param surface
     */
    void setSurface(Surface surface);

    /**
     * 开始播放
     */
    void start();

    /**
     * 跳转某处播放
     * @param posTime
     */
    void seekTo(long posTime);

    /**
     * 按暂停播放
     */
    void pause();

    /**
     * 恢复播放
     */
    void resume();

    /**
     * 停止播放
     */
    void stop();

    /**
     * 释放Player
     */
    void release();

    /**
     * 获取当前进度位置
     * @return
     */
    long getCurrentPosition();

    /**
     * 获取文件时长
     * @return
     */
    long getDuration();

    /**
     * 是否正在播放
     * @return
     */
    boolean isPlaying();

    /**
     * 设置seek完成后监听
     * @param onSeekCompleteListener
     */
    void setOnSeekCompleteListener(OnSeekCompleteListener onSeekCompleteListener);

    /**
     *  设置可以开始播放回调
     * @param onPlayReadyListener
     */
    void setOnPlayReadyListener(OnPlayReadyListener onPlayReadyListener);
}
