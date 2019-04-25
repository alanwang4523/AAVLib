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
