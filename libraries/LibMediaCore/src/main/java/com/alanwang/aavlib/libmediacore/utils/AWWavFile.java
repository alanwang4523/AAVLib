package com.alanwang.aavlib.libmediacore.utils;

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
