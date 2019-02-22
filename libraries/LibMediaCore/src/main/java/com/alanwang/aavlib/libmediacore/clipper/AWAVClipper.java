package com.alanwang.aavlib.libmediacore.clipper;

import com.alanwang.aavlib.libmediacore.listener.AWProcessListener;
import com.alanwang.aavlib.libmediacore.muxer.AWAVAndroidMuxer;
import java.io.IOException;

/**
 * 裁剪音频和视频, 实际是使用 AWAVAndroidMuxer，相当于要 mux 的音频和视频是同一个文件
 * Author: AlanWang4523.
 * Date: 19/2/20 01:48.
 * Mail: alanwang4523@gmail.com
 */
public class AWAVClipper {

    private AWAVAndroidMuxer mAVAndroidMuxer;

    public AWAVClipper() {
        mAVAndroidMuxer = new AWAVAndroidMuxer();
    }

    /**
     * 设置资源文件
     * @param srcMediaPath
     * @param dstSavePath
     * @throws IOException
     */
    public void setDataSource(String srcMediaPath, String dstSavePath) throws IOException {
        mAVAndroidMuxer.setDataSource(srcMediaPath, srcMediaPath, dstSavePath);
    }

    /**
     * 设置需要抽取的起止时间，必须在 {@link #setDataSource} 之后调用
     * @param startTimeMs 单位：毫秒
     * @param endTimeMs 单位：毫秒
     */
    public void setExtractTime(long startTimeMs, long endTimeMs) {
        mAVAndroidMuxer.setExtractTime(startTimeMs, endTimeMs);
    }

    /**
     * 设置监听器
     * @param extractorListener
     */
    public void setProcessListener(AWProcessListener extractorListener) {
        mAVAndroidMuxer.setProcessListener(extractorListener);
    }

    /**
     * 开始裁剪
     */
    public void start() {
        mAVAndroidMuxer.start();
    }

    /**
     * 停止/取消裁剪
     */
    public void stop() {
        mAVAndroidMuxer.stop();
    }
}
