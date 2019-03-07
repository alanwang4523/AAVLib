package com.alanwang.aavlib.libmediacore.decoder;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.text.TextUtils;
import com.alanwang.aavlib.libmediacore.extractor.AWMediaExtractor;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Author: AlanWang4523.
 * Date: 19/3/8 00:16.
 * Mail: alanwang4523@gmail.com
 */
public abstract class AWBaseHWDecoder {

    /**
     * 是否是解码音频
     * @return
     */
    protected abstract boolean isDecodeAudio();

    protected AWMediaExtractor mMediaExtractor = new AWMediaExtractor() {
        @Override
        protected boolean isTheInterestedTrack(String keyMimeString) {
            if (TextUtils.isEmpty(keyMimeString)) {
                return false;
            }
            if (isDecodeAudio() && keyMimeString.startsWith("audio")) {
                return true;
            }

            if (!isDecodeAudio() && keyMimeString.startsWith("video")) {
                return true;
            }
            return false;
        }

        @Override
        protected void onDataAvailable(ByteBuffer extractBuffer, MediaCodec.BufferInfo bufferInfo) {

        }

        @Override
        protected void onMediaFormatConfirmed(MediaFormat mediaFormat) throws IllegalArgumentException, IOException {
            super.onMediaFormatConfirmed(mediaFormat);
        }
    };


}
