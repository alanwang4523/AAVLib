package com.alanwang.aavlib.libmediacore.video;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.Surface;

/**
 * Author: AlanWang4523.
 * Date: 19/1/25 00:59.
 * Mail: alanwang4523@gmail.com
 */

public class AAVVideoPlayer implements IVideoPlayer {

    private MediaPlayer mMediaPlayer;

    private boolean mIsPlayerReady = false;
    private boolean mIsVideoSizeKnown = false;
    private boolean mIsHaveNotifyReady = false;
    private OnPlayReadyListener mPlayReadyListener;
    private OnSeekCompleteListener mOnSeekCompleteListener;

    public AAVVideoPlayer() {
        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public void preparePlayer(String videoPath) {
        mMediaPlayer.reset();
        mMediaPlayer.setScreenOnWhilePlaying(true);

        mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                if (mOnSeekCompleteListener != null) {
                    mOnSeekCompleteListener.onSeekComplete();
                }
            }
        });

        mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                if (width == 0 || height == 0) {
                    return;
                }
                mIsVideoSizeKnown = true;
                if (isNeedNotifyReady()) {
                    mPlayReadyListener.onPlayReady(width, height);
                    mIsHaveNotifyReady = true;
                }
            }
        });

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mIsPlayerReady = true;
                if (isNeedNotifyReady()) {
                    mPlayReadyListener.onPlayReady(mp.getVideoWidth(), mp.getVideoHeight());
                    mIsHaveNotifyReady = true;
                }
            }
        });

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(videoPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSurface(Surface surface) {
        mMediaPlayer.setSurface(surface);
    }

    @Override
    public void start() {
        mMediaPlayer.start();
    }

    @Override
    public void seekTo(long posTime) {
        mMediaPlayer.seekTo((int) posTime);
    }

    @Override
    public void pause() {
        mMediaPlayer.pause();
    }

    @Override
    public void resume() {
        mMediaPlayer.start();
    }

    @Override
    public void stop() {
        mMediaPlayer.stop();
    }

    @Override
    public void release() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

    @Override
    public long getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public void setOnSeekCompleteListener(OnSeekCompleteListener onSeekCompleteListener) {
        mOnSeekCompleteListener = onSeekCompleteListener;
    }

    @Override
    public void setOnPlayReadyListener(OnPlayReadyListener onPlayReadyListener) {
        mPlayReadyListener = onPlayReadyListener;
    }

    private boolean isNeedNotifyReady() {
        return (!mIsHaveNotifyReady) && mIsPlayerReady && mIsVideoSizeKnown && (mPlayReadyListener != null);
    }
}
