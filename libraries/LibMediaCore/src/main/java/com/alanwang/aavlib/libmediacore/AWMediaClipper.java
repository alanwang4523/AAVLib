package com.alanwang.aavlib.libmediacore;

import android.media.MediaMuxer;

import com.alanwang.aavlib.libmediacore.audio.AWAudioExtractor;
import com.alanwang.aavlib.libmediacore.video.AWVideoExtractor;

import java.io.IOException;

/**
 * 裁剪音频和视频
 * Author: AlanWang4523.
 * Date: 19/2/20 01:48.
 * Mail: alanwang4523@gmail.com
 */
public class AWMediaClipper {
    private AWAudioExtractor mAudioExtractor;
    private AWVideoExtractor mVideoExtractor;
    private MediaMuxer mMediaMuxer;
    private boolean mIsHaveAudio = false;
    private boolean mIsHaveVideo = false;

    public AWMediaClipper() {
        mAudioExtractor = new AWAudioExtractor();
        mVideoExtractor = new AWVideoExtractor();
    }

    /**
     * 设置资源文件
     * @param srcMediaPath
     * @param dstSavePath
     * @throws IOException
     */
    public void setDataSource(String srcMediaPath, String dstSavePath) throws IOException {
        mMediaMuxer = new MediaMuxer(dstSavePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        try {
            mAudioExtractor.setDataSource(srcMediaPath);
            mIsHaveAudio = true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            mIsHaveAudio = false;
        }
        if (mIsHaveAudio) {
            mMediaMuxer.addTrack(mAudioExtractor.getMediaFormat());
        }

        try {
            mVideoExtractor.setDataSource(srcMediaPath);
            mIsHaveVideo = true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            mIsHaveVideo = false;
        }
        if (mIsHaveVideo) {
            mMediaMuxer.addTrack(mVideoExtractor.getMediaFormat());
        }

        if (!mIsHaveAudio && !mIsHaveVideo) {
            throw new IllegalArgumentException("There is neither audio nor video!");
        }

    }
}
