/**
 * Copyright (c) 2019-present, AlanWang4523 (alanwang4523@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alanwang.aavlib.libmediacore.clipper;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import com.alanwang.aavlib.libmediacore.extractor.AWMediaExtractor;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * 媒体文件裁剪器，可设置起始时间裁剪到指定的输出文件，具体裁视频还是裁音频由子类实现
 * Author: AlanWang4523.
 * Date: 19/2/20 01:25.
 * Mail: alanwang4523@gmail.com
 */
public abstract class AWAbstractAVClipper extends AWMediaExtractor {

    private MediaMuxer mMediaMuxer = null;
    private String mOutputPath;
    private int mAddedTrackIndex;

    public AWAbstractAVClipper(String outputPath) {
        this.mOutputPath = outputPath;
    }

    @Override
    protected void onMediaFormatConfirmed(MediaFormat mediaFormat) throws IllegalArgumentException, IOException {
        super.onMediaFormatConfirmed(mediaFormat);
        mMediaMuxer = new MediaMuxer(mOutputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        mAddedTrackIndex = mMediaMuxer.addTrack(mediaFormat);
        mMediaMuxer.start();
    }

    @Override
    protected void onDataAvailable(ByteBuffer extractBuffer, MediaCodec.BufferInfo bufferInfo) {
        mMediaMuxer.writeSampleData(mAddedTrackIndex, extractBuffer, bufferInfo);
    }

    @Override
    protected void release() {
        mMediaMuxer.stop();
        mMediaMuxer.release();
        super.release();
    }
}
