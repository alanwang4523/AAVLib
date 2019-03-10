package com.alanwang.aavlib.libmediacore.decoder;

import android.media.MediaCodec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Author: AlanWang4523.
 * Date: 19/3/11 00:15.
 * Mail: alanwang4523@gmail.com
 */
public class AWAudioHWDecoderForPCM extends AWAudioHWDecoder {

    private FileOutputStream mOutPutFile;
    private byte[] dataArr = new byte[10 * 1024];

    public void setOutputFile(String filePath) throws FileNotFoundException {
        mOutPutFile = new FileOutputStream(new File(filePath));
    }


    @Override
    protected void onDecodedAvailable(ByteBuffer extractBuffer, MediaCodec.BufferInfo bufferInfo) {
        try {
            extractBuffer.get(dataArr, 0, bufferInfo.size);
            mOutPutFile.write(dataArr, 0, bufferInfo.size);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        if (mOutPutFile != null) {
            try {
                mOutPutFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
