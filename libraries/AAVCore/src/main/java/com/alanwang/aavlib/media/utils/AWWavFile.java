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
package com.alanwang.aavlib.media.utils;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Wav File 封装类，用于生成带 44 字节 Wav header 的 wav 文件
 * Author: AlanWang4523.
 * Date: 19/3/13 00:51.
 * Mail: alanwang4523@gmail.com
 */
public class AWWavFile {

    RandomAccessFile randomAccessFile;
    private int sampleRate;
    private int channels;
    private int bitsPerSample;
    private long audioLen;
    private boolean isRelease;

    public AWWavFile(String filePath, int sampleRate, int channels) throws IOException {
        this(filePath, sampleRate, channels, 16);// 默认为 short 型
    }

    public AWWavFile(String filePath, int sampleRate, int channels, int bitsPerSample) throws IOException {
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.bitsPerSample = bitsPerSample;

        audioLen = 0;
        isRelease = false;
        randomAccessFile = new RandomAccessFile(filePath, "rw");
        randomAccessFile.seek(44);
    }

    /**
     * 写 PCM 数据
     * @param data
     * @param len
     */
    public void writeData(byte[] data, int len) throws IOException {
        if (isRelease || data == null || len <= 0) {
            return;
        }
        randomAccessFile.write(data, 0, len);
        audioLen += len;
    }

    /**
     * 写入 wav 文件头，并关闭文件
     * @throws IOException
     */
    public void release() throws IOException {
        isRelease = true;
        byte[] wavHeader = AWWavFileHelper.generateWavHeader(
                sampleRate, channels, bitsPerSample, audioLen);
        randomAccessFile.seek(0);
        randomAccessFile.write(wavHeader);
        randomAccessFile.close();
    }
}
