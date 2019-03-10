package com.alanwang.aavlib.libmediacore.decoder;

import android.media.MediaFormat;
import com.alanwang.aavlib.libmediacore.utils.AWWavFileHelper;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Author: AlanWang4523.
 * Date: 19/3/11 01:02.
 * Mail: alanwang4523@gmail.com
 */
public class AWAudioHWDecoderToWav extends AWAudioHWDecoder {

    private static final int BITS_PER_SAMPLE = 16;
    private int mSampleRate;
    private int mChannelCount;
    private long mAudioLen;
    private RandomAccessFile mOutPutWavFile;

    public void setOutputFile(String filePath) throws IOException {
        mOutPutWavFile = new RandomAccessFile(new File(filePath), "rw");
        mOutPutWavFile.seek(44);
    }

    @Override
    protected void onMediaFormatConfirmed(MediaFormat mediaFormat) {
        super.onMediaFormatConfirmed(mediaFormat);
        mSampleRate = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        mChannelCount = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
    }

    @Override
    protected void onDecodedAvailable(byte[] data, int offset, int len) {
        try {
            mOutPutWavFile.write(data, 0, len);
            mAudioLen += len;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        if (mOutPutWavFile != null) {
            try {
                byte[] headByte = AWWavFileHelper.generateWavHeader(
                        mSampleRate, mChannelCount, BITS_PER_SAMPLE, mAudioLen);
                mOutPutWavFile.seek(0);
                mOutPutWavFile.write(headByte);
                mOutPutWavFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
