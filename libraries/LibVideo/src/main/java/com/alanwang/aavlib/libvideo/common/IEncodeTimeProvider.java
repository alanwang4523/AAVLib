package com.alanwang.aavlib.libvideo.common;

/**
 * 编码时间提供器，可以由 SDK 外部提供编码时间戳，
 * 如用音频的时间戳作为视频的编码时间戳以便于做音画同步
 *
 * Author: wangjianjun.
 * Date: 19/1/3 15:41.
 * Mail: alanwang6584@gmail.com
 */

public interface IEncodeTimeProvider {
    /**
     * 获取编码时间戳，单位 ms
     * @return
     */
    long getTimeStampMS();
}
