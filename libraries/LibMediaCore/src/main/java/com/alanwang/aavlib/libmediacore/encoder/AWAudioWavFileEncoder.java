package com.alanwang.aavlib.libmediacore.encoder;

import android.media.MediaCodec;
import android.media.MediaFormat;
import java.nio.ByteBuffer;

/**
 * Author: AlanWang4523.
 * Date: 19/2/25 00:52.
 * Mail: alanwang4523@gmail.com
 */
public class AWAudioWavFileEncoder extends AWAudioHWEncoderCore {

    @Override
    protected void onOutputFormatChanged(MediaFormat newFormat) {

    }

    @Override
    protected void handleEncodedData(ByteBuffer encodedData, MediaCodec.BufferInfo bufferInfo) {

    }
}
