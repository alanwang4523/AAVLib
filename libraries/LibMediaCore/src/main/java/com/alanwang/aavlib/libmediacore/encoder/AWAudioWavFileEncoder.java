package com.alanwang.aavlib.libmediacore.encoder;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import com.alanwang.aavlib.libmediacore.listener.AWProcessListener;
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
    private boolean mIsReady = false;
    private AWProcessListener mProcessListener;
    private MediaMuxer mMediaMuxer;
    private int mAudioTrackIdx = 0;
    private int mSampleRate;
    private int mChannelCount;
    private int mBytePerSample;
    private long mNeedEncodeLen;
    private long mNeedSkipLen;

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
        mBytePerSample = headerInfo.bytePerSample;
        mNeedEncodeLen = headerInfo.audioDataLen;

        mMediaMuxer = new MediaMuxer(outputFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        mWavInputStream = new FileInputStream(wavFile);
        mWavInputStream.skip(44);//跳过wav文件头，从00H~2BH

        mIsReady = true;
    }

    /**
     * 设置要编码的区间, 须在 {@link #setDataSource} 之后调
     * @param startTimeMs
     * @param endTimeMs
     */
    public void setEncodeTime(long startTimeMs, long endTimeMs) {
        checkIsReady();
        mNeedSkipLen = getLenByTime(mSampleRate, mChannelCount, mBytePerSample, startTimeMs);
        long needEncodeLen = getLenByTime(mSampleRate, mChannelCount, mBytePerSample, (endTimeMs - startTimeMs));
        mNeedEncodeLen = Math.min(needEncodeLen, mNeedEncodeLen);
    }

    /**
     * 必须在 {@link #setDataSource} 之后调
     * @param bitRate
     * @throws IOException
     * @throws InterruptedException
     */
    public void setup(int bitRate) throws IOException, InterruptedException {
        checkIsReady();
        super.setup(mSampleRate, mChannelCount, bitRate);
    }

    /**
     * 设置监听器
     * @param extractorListener
     */
    public void setProcessListener(AWProcessListener extractorListener) {
        this.mProcessListener = extractorListener;
    }

    /**
     * 开始裁剪
     */
    public void start() {
        checkIsReady();
        // TODO 开现线程开始处理
    }

    /**
     * 停止/取消裁剪
     */
    public void cancel() {

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

    /**
     * 根据时长获取对应长度wav文件长度
     * @param sampleRate
     * @param channelCount
     * @param bytePerSample 每个采样点的数据长度
     * @param timeMs
     * @return
     */
    private long getLenByTime(int sampleRate, int channelCount, int bytePerSample, long timeMs) {
        int samplesNum = (int) ((timeMs / 1000.0f) * sampleRate);
        return bytePerSample * channelCount * samplesNum;
    }

    /**
     * 检测是否已经 ready
     */
    private void checkIsReady() {
        if (!mIsReady) {
            throw new IllegalStateException("Data source is not ready or ready failed!");
        }
    }
}
