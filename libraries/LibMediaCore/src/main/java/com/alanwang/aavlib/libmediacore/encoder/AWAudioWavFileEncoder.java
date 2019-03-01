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
    private static final String TAG = AWAudioWavFileEncoder.class.getSimpleName();
    private FileInputStream mWavInputStream = null;
    private AWProcessListener mProcessListener;
    private MediaMuxer mMediaMuxer;
    private int mAudioTrackIdx = 0;
    private int mSampleRate;
    private int mChannelCount;
    private int mBytePerSample;

    private long mNeedEncodeLen;
    private long mPresentationTimeUs;
    private long mTotalLenReaded = 0;
    private boolean mIsReady = false;
    private volatile boolean mIsRunning = false;

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
    public void setEncodeTime(long startTimeMs, long endTimeMs) throws IOException {
        checkIsReady();
        long needSkipLen = getLenByTime(mSampleRate, mChannelCount, mBytePerSample, startTimeMs);
        long needEncodeLen = getLenByTime(mSampleRate, mChannelCount, mBytePerSample, (endTimeMs - startTimeMs));
        mNeedEncodeLen = Math.min(needEncodeLen, mNeedEncodeLen);
        mWavInputStream.skip(needSkipLen);
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
        if (!mIsRunning) {
            StringBuilder strBuilder = new StringBuilder(TAG);
            strBuilder.append("-").append(System.currentTimeMillis());

            Thread thread = new Thread(null, workRunnable, strBuilder.toString());
            thread.start();
            mIsRunning = true;
        }
    }

    /**
     * 停止/取消裁剪
     */
    public void cancel() {
        mIsRunning = false;
    }

    @Override
    public void release() {
        super.release();
        if (mMediaMuxer != null) {
            try {
                mMediaMuxer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mMediaMuxer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final Runnable workRunnable = new Runnable() {
        @Override
        public void run() {
            boolean isSuccess;
            do {
                isSuccess = fillingRawData();
                if (!isSuccess) {
                    break;
                }

                do {
                    isSuccess = extractEncodedData();
                } while (mIsRunning && isSuccess);
                if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                    break;
                }
            } while (mIsRunning);

            release();
        }
    };

    /**
     * 向编码器中填充裸数据
     * @return
     */
    private boolean fillingRawData() {
        int inputBufIndex = 0;
        int bytesRead = 0;
        while (mIsRunning) {
            inputBufIndex = mMediaEncoder.dequeueInputBuffer(CODEC_TIMEOUT_IN_US);
            if (inputBufIndex < 0) {
                break;
            }
            ByteBuffer inputBuffer = mEncoderInputBuffers[inputBufIndex];
            inputBuffer.clear();

            try {
                bytesRead = mWavInputStream.read(inputBuffer.array(), 0, inputBuffer.limit());
            } catch (IOException e) {
                return false;
            }

            if (bytesRead == -1) {//读到文件尾，这里应该走不到才对
                mMediaEncoder.queueInputBuffer(inputBufIndex, 0, 0,
                        mPresentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                break;
            } else {
                int flags = 0;
                if (mNeedEncodeLen - mTotalLenReaded < bytesRead) {
                    bytesRead = (int)(mNeedEncodeLen - mTotalLenReaded);
                    flags = MediaCodec.BUFFER_FLAG_END_OF_STREAM;
                }
                mTotalLenReaded += bytesRead;
                inputBuffer.limit(bytesRead);
                mMediaEncoder.queueInputBuffer(inputBufIndex, 0, bytesRead, mPresentationTimeUs, flags);
                mPresentationTimeUs = (1.0 * mTotalLenReaded / mBytePerSample / mSampleRate / mChannelCount * 1000 * 1000l;
                if (MediaCodec.BUFFER_FLAG_END_OF_STREAM == flags) {
                    break;
                }
            }
        }
        return true;
    }

    @Override
    protected void onOutputFormatChanged(MediaFormat newFormat) {
        mAudioTrackIdx = mMediaMuxer.addTrack(newFormat);
        mMediaMuxer.start();
    }

    @Override
    protected void onEncodedDataAvailable(ByteBuffer encodedData, MediaCodec.BufferInfo bufferInfo) {
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
