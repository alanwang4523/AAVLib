package com.alanwang.aavlib.libmediacore.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Wav File 工具类（注意：只支持文件头为 44 字节的 Wav 文件），支持：
 * 1. 生成 44 字节的 Wav 文件头
 * 2. 获取 Wav 文件头
 * 3. 更新 Wav 文件头信息
 *
 * Author: AlanWang4523.
 * Date: 19/2/25 00:16.
 * Mail: alanwang4523@gmail.com
 */
public class AWWavFileHelper {

    /**
     * 生成 Wav 文件头
     * @param sampleRate 采样率，如44100
     * @param channels 通道数，如立体声为2
     * @param bitsPerSample 采样精度，即每个采样所占数据位数，如16，表示每个采样16bit数据，即2个字节
     * @return wavHeader
     */
    public static byte[] generateWavHeader(int sampleRate, int channels, int bitsPerSample) {
        if (bitsPerSample != 16 || bitsPerSample != 32) {
            throw new IllegalArgumentException("The bitsPerSample is not 16 or 32!");
        }
        byte[] wavHeader = new byte[44];

        // 这个长度不包括"RIFF"标志(4字节)和文件长度本身所占字节(4字节),即该长度等于整个 Wav文件长度(包含44字节头) - 8
        long ckTotalSize = 36;

        // 生成文件头默认纯音频数据长度为 0
        long audioDataLen = 0;

        // 音频数据传送速率, 单位是字节。其值为采样率×每次采样大小。播放软件利用此值可以估计缓冲区的大小。
        // bytePerSecond = sampleRate * (bitsPerSample / 8) * channels
        int bytePerSecond = sampleRate * (bitsPerSample / 8) * channels;

        //ckid：4字节 RIFF 标志，大写
        wavHeader[0]  = 'R';
        wavHeader[1]  = 'I';
        wavHeader[2]  = 'F';
        wavHeader[3]  = 'F';

        //cksize：4字节文件长度，这个长度不包括"RIFF"标志(4字节)和文件长度本身所占字节(4字节),即该长度等于整个文件长度 - 8
        wavHeader[4]  = (byte)(ckTotalSize & 0xff);
        wavHeader[5]  = (byte)((ckTotalSize >> 8) & 0xff);
        wavHeader[6]  = (byte)((ckTotalSize >> 16) & 0xff);
        wavHeader[7]  = (byte)((ckTotalSize >> 24) & 0xff);

        //fcc type：4字节 "WAVE" 类型块标识, 大写
        wavHeader[8]  = 'W';
        wavHeader[9]  = 'A';
        wavHeader[10] = 'V';
        wavHeader[11] = 'E';

        //ckid：4字节 表示"fmt" chunk的开始,此块中包括文件内部格式信息，小写, 最后一个字符是空格
        wavHeader[12] = 'f';
        wavHeader[13] = 'm';
        wavHeader[14] = 't';
        wavHeader[15] = ' ';

        //cksize：4字节，文件内部格式信息数据的大小，过滤字节（一般为00000010H）
        wavHeader[16] = 0x10;
        wavHeader[17] = 0;
        wavHeader[18] = 0;
        wavHeader[19] = 0;

        //FormatTag：2字节，音频数据的编码方式，1：表示是PCM 编码
        wavHeader[20] = 1;
        wavHeader[21] = 0;

        //Channels：2字节，声道数，单声道为1，双声道为2
        wavHeader[22] = (byte) channels;
        wavHeader[23] = 0;

        //SamplesPerSec：4字节，采样率，如44100
        wavHeader[24] = (byte)(sampleRate & 0xff);
        wavHeader[25] = (byte)((sampleRate >> 8) & 0xff);
        wavHeader[26] = (byte)((sampleRate >> 16) & 0xff);
        wavHeader[27] = (byte)((sampleRate >> 24) & 0xff);

        //BytesPerSec：4字节，音频数据传送速率, 单位是字节。其值为采样率×每次采样大小。播放软件利用此值可以估计缓冲区的大小；
        //bytePerSecond = sampleRate * (bitsPerSample / 8) * channels
        wavHeader[28] = (byte)(bytePerSecond & 0xff);
        wavHeader[29] = (byte)((bytePerSecond >> 8) & 0xff);
        wavHeader[30] = (byte)((bytePerSecond >> 16) & 0xff);
        wavHeader[31] = (byte)((bytePerSecond >> 24) & 0xff);

        //BlockAlign：2字节，每次采样的大小 = 采样精度*声道数/8(单位是字节); 这也是字节对齐的最小单位, 譬如 16bit 立体声在这里的值是 4 字节。
        //播放软件需要一次处理多个该值大小的字节数据，以便将其值用于缓冲区的调整
        wavHeader[32] = (byte)(bitsPerSample * channels / 8);
        wavHeader[33] = 0;

        //BitsPerSample：2字节，每个声道的采样精度; 譬如 16bit 在这里的值就是16。如果有多个声道，则每个声道的采样精度大小都一样的；
        wavHeader[34] = (byte) bitsPerSample;
        wavHeader[35] = 0;

        //ckid：4字节，数据标志符（data），表示 "data" chunk的开始。此块中包含音频数据，小写；
        wavHeader[36] = 'd';
        wavHeader[37] = 'a';
        wavHeader[38] = 't';
        wavHeader[39] = 'a';

        //cksize：音频数据的长度，4字节，audioDataLen = ckSize - 36 = fileLenIncludeHeader - 44
        wavHeader[40] = (byte)(audioDataLen & 0xff);
        wavHeader[41] = (byte)((audioDataLen >> 8) & 0xff);
        wavHeader[42] = (byte)((audioDataLen >> 16) & 0xff);
        wavHeader[43] = (byte)((audioDataLen >> 24) & 0xff);
        return wavHeader;
    }

    /**
     * 更新 wav 文件头，主要是更新文件长度
     */
    public static void updateWavHeader(File wavFile) throws IOException {
        updateWavHeader(wavFile, wavFile.length());
    }

    /**
     * 更新 wav 文件头，主要是更新文件长度
     * @param totalFileLenIncludeHeader 包含 44 字节 wav 头及所有 PCM 数据总和
     */
    public static void updateWavHeader(File wavFile, long totalFileLenIncludeHeader) throws IOException {
        if (wavFile == null || !wavFile.exists()) {
            throw new IllegalArgumentException("The wavFile is null or not exist!");
        }
        RandomAccessFile randomAccessFile = new RandomAccessFile(wavFile, "rw");
        //更新wav文件头04H— 08H的数据长度：该长度 = 文件总长 - 8
        randomAccessFile.seek(4);
        randomAccessFile.write(int2ByteArray((int) (totalFileLenIncludeHeader - 8)));

        //更新wav文件头28H— 2CH,实际PCM采样数据长度
        randomAccessFile.seek(40);
        randomAccessFile.write(int2ByteArray((int) (totalFileLenIncludeHeader - 44)));
        randomAccessFile.close();
    }

    /**
     * 获取wav文件头信息
     * @param wavFile
     * @return
     */
    public static WavHeaderInfo getWavHeaderInfo(File wavFile) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(wavFile, "rw");

        //读取channelCount，第22~23位
        randomAccessFile.seek(22);
        byte[] channelCountArray = new byte[2];
        randomAccessFile.read(channelCountArray);
        int channelCount = byteArray2Short(channelCountArray);

        //读取sampleRate，第24~27位
        randomAccessFile.seek(24);
        byte[] sampleRateArray = new byte[4];
        randomAccessFile.read(sampleRateArray);
        int sampleRate = byteArray2Int(sampleRateArray);

        //读取BitsPerSample，第34~35位
        randomAccessFile.seek(34);
        byte[] bitsPerSampleArray = new byte[2];
        randomAccessFile.read(bitsPerSampleArray);
        int bytePerSample = byteArray2Short(bitsPerSampleArray) / 8;

        //读取音频数据长度，第40~43位
        randomAccessFile.seek(40);
        byte[] audioDataLenArray = new byte[4];
        randomAccessFile.read(audioDataLenArray);
        int audioDataLen = byteArray2Int(audioDataLenArray);

        randomAccessFile.close();

        return new WavHeaderInfo(sampleRate, channelCount, bytePerSample, audioDataLen);
    }

    /**
     * 将整型转成byte数组
     * @param data
     * @return
     */
    private static byte[] int2ByteArray(int data) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(data).array();
    }

    /**
     * 将short转成byte数组
     * @param data
     * @return
     */
    private static byte[] short2ByteArray(short data) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(data).array();
    }

    /**
     * 将byte数组转成short
     * @param b
     * @return
     */
    private static short byteArray2Short(byte[] b) {
        return ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    /**
     * 将byte数组转成整型
     * @param b
     * @return
     */
    private static int byteArray2Int(byte[] b) {
        return ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public static class WavHeaderInfo {
        /**
         * 采样率
         */
        public int sampleRate;

        /**
         * 通道数
         */
        public int channelCount;

        /**
         * 每个采样点的大小，
         * 如：short 型 PCM 是 2 字节
         * 如：float 型 PCM 是 4 字节
         * 单位：字节
         */
        public int bytePerSample;

        /**
         * 音频数据长度，单位：字节
         */
        public int audioDataLen;

        public WavHeaderInfo(int sampleRate, int channelCount, int bytePerSample, int audioDataLen) {
            this.sampleRate = sampleRate;
            this.channelCount = channelCount;
            this.bytePerSample = bytePerSample;
            this.audioDataLen = audioDataLen;
        }

        @Override
        public String toString() {
            StringBuilder sttBuilder = new StringBuilder(WavHeaderInfo.class.getSimpleName());
            sttBuilder.append("::")
                    .append("sampleRate = ").append(sampleRate)
                    .append(", channelCount = ").append(channelCount)
                    .append(", bytePerSample = ").append(bytePerSample)
                    .append(", audioDataLen = ").append(audioDataLen).append("\n");
            return sttBuilder.toString();
        }
    }
}
