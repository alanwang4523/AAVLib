package com.alanwang.aavlib.libmediacore.muxer;

import android.media.MediaMuxer;

import com.alanwang.aavlib.libmediacore.extractor.AWAudioExtractor;
import com.alanwang.aavlib.libmediacore.extractor.AWVideoExtractor;
import com.alanwang.aavlib.libmediacore.listener.AWProcessListener;

/**
 * 音视频混合器，将音频文件和视频文件合成为一个视频文件
 * Author: AlanWang4523.
 * Date: 19/2/21 01:39.
 * Mail: alanwang4523@gmail.com
 */
public class AWAVAndroidMuxer {
    private AWAudioExtractor mAudioExtractor;
    private AWVideoExtractor mVideoExtractor;
    private MediaMuxer mMediaMuxer;
    private AWProcessListener mProcessListener;

    private String mOutputPath;

    public AWAVAndroidMuxer(String outputPath) {
        this.mOutputPath = outputPath;
    }
}
