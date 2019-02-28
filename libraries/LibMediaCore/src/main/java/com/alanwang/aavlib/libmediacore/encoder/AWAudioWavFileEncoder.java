package com.alanwang.aavlib.libmediacore.encoder;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import com.alanwang.aavlib.libmediacore.utils.AWWavFileHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Author: AlanWang4523.
 * Date: 19/2/25 00:52.
 * Mail: alanwang4523@gmail.com
 */
public class AWAudioWavFileEncoder extends AWAudioHWEncoderCore {

    private FileInputStream mWavInputStream = null;
    private int mSampleRate;
    private int mChannelCount;
    private int mAudioDataLen;

    private MediaMuxer mMediaMuxer;
    private int mAudioTrackIdx = 0;

    /**
     * 设置资源文件
     * @param wavFilePath
     * @param outputFilePath
     * @throws IOException
     */
    public void setDataSource(String wavFilePath, String outputFilePath) throws IOException {
        File wavFile = new File(wavFilePath);
        if (!wavFile.exists()) {
            throw new FileNotFoundException("Wav file is not found!");
        }

        // 获取音频信息
        AWWavFileHelper.WavHeaderInfo headerInfo = AWWavFileHelper.getWavHeaderInfo(wavFile);
        mSampleRate = headerInfo.sampleRate;
        mChannelCount = headerInfo.channelCount;
        mAudioDataLen = headerInfo.audioDataLen;

        mMediaMuxer = new MediaMuxer(outputFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        mWavInputStream = new FileInputStream(wavFile);
    }

    /**
     * 必须在 {@link #setDataSource} 之后调
     * @param bitRate
     * @throws IOException
     * @throws InterruptedException
     */
    public void setup(int bitRate) throws IOException, InterruptedException {
        super.setup(mSampleRate, mChannelCount, bitRate);
    }

    @Override
    protected void onOutputFormatChanged(MediaFormat newFormat) {
        mAudioTrackIdx = mMediaMuxer.addTrack(newFormat);
        mMediaMuxer.start();
    }

    @Override
    protected void handleEncodedData(ByteBuffer encodedData, MediaCodec.BufferInfo bufferInfo) {
        mMediaMuxer.writeSampleData(mAudioTrackIdx, encodedData, bufferInfo);
    }
}
