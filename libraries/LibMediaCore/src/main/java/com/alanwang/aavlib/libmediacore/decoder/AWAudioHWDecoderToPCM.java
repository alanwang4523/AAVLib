package com.alanwang.aavlib.libmediacore.decoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Author: AlanWang4523.
 * Date: 19/3/11 00:15.
 * Mail: alanwang4523@gmail.com
 */
public class AWAudioHWDecoderToPCM extends AWAudioHWDecoder {

    private FileOutputStream mOutPutFile;

    public void setOutputFile(String filePath) throws FileNotFoundException {
        mOutPutFile = new FileOutputStream(new File(filePath));
    }

    @Override
    protected void onDecodedAvailable(byte[] data, int offset, int len) {
        try {
            mOutPutFile.write(data, 0, len);
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
