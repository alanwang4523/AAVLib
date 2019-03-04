package com.alanwang.aavlib.libmediacore.encoder;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 视频录制编码器，将编码数据写入文件，用于生成 mp4 视频文件
 * Author: AlanWang4523.
 * Date: 19/2/18 00:12.
 * Mail: alanwang4523@gmail.com
 */

public class AWVideoRecordEncoder extends AWVideoHWEncoderCore {

    private MediaMuxer mMediaMuxer;
    private int mTrackIndex = -1;

    public void setup(int width, int height, int bitRate) throws IOException, InterruptedException {
        super.setup(width, height, bitRate, FRAME_RATE, DEFAULT_I_FRAME_INTERVAL);
    }

    /**
     * 开始录制
     * @param filePath
     * @throws IOException
     */
    public void startRecord(String filePath) throws IOException {
        mMediaMuxer = new MediaMuxer(filePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        if (mMediaMuxer != null) {
            drainEncoder(true);
            if (mTrackIndex >= 0) {
                mMediaMuxer.stop();
            }
        }
    }

    @Override
    public void release() {
        super.release();
        if (mMediaMuxer != null) {
            mMediaMuxer.release();
            mMediaMuxer = null;
        }
    }

    @Override
    protected void onOutputFormatChanged(MediaFormat newFormat) {
        mTrackIndex = mMediaMuxer.addTrack(newFormat);
        mMediaMuxer.start();
    }

    @Override
    protected void onEncodedDataAvailable(ByteBuffer encodedData, MediaCodec.BufferInfo bufferInfo) {
        if (mMediaMuxer != null) {
            mMediaMuxer.writeSampleData(mTrackIndex, encodedData, bufferInfo);
        }
    }
}
