package com.alanwang.aavlib.libmediacore.clipper;

import com.alanwang.aavlib.libmediacore.muxer.AWAVAndroidMuxer;
import java.io.IOException;

/**
 * 裁剪音频和视频, 可继承 AWAVAndroidMuxer，相当于要 mux 的音频和视频是同一个文件
 * Author: AlanWang4523.
 * Date: 19/2/20 01:48.
 * Mail: alanwang4523@gmail.com
 */
public class AWAVClipper extends AWAVAndroidMuxer {

    /**
     * 设置资源文件
     * @param srcMediaPath
     * @param dstSavePath
     * @throws IOException
     */
    public void setDataSource(String srcMediaPath, String dstSavePath) throws IOException {
        super.setDataSource(srcMediaPath, srcMediaPath, dstSavePath);
    }


}
