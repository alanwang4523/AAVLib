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
package com.alanwang.aavlib.media.decoder;

import android.media.MediaFormat;
import com.alanwang.aavlib.media.utils.AWWavFileHelper;
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
