/**
 * Copyright (c) 2019-present, AlanWang4523 (alanwang4523@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alanwang.aavlib.libmediacore.clipper;

import com.alanwang.aavlib.libmediacore.listener.AWVoidResultListener;
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
    public void setProcessListener(AWVoidResultListener extractorListener) {
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
    public void cancel() {
        mAVAndroidMuxer.cancel();
    }
}
