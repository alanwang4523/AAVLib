package com.alanwang.aavlib.libmediacore.encoder;

import android.media.MediaCodec;
import android.media.MediaFormat;

import java.nio.ByteBuffer;

/**
 * Author: AlanWang4523.
 * Date: 19/2/25 00:53.
 * Mail: alanwang4523@gmail.com
 */
public class AWAudioPcmFileEncoder extends AWAudioHWEncoderCore {


    @Override
    protected void onOutputFormatChanged(MediaFormat newFormat) {

    }

    @Override
    protected void onEncodedDataAvailable(ByteBuffer encodedData, MediaCodec.BufferInfo bufferInfo) {

    }
}
