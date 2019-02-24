package com.alanwang.aavlib.libmediacore.utils;

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
}
