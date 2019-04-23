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
package com.alanwang.aav.alvideoeditor.core;

/**
 * Author: AlanWang4523.
 * Date: 19/4/16 00:17.
 * Mail: alanwang4523@gmail.com
 */
public class AWMediaConstants {

    /**
     * 视频录制目录
     */
    public final static String VIDEO_RECORD_DIR_PATH = "/sdcard/Alan/record/";

    /**
     * mp4 后缀
     */
    public final static String SUFFIX_MP4 = ".mp4";

    /**
     * 视频片段前缀
     */
    public final static String PREFIX_VIDEO_SEGMENT_NAME = "aw_video_";

    /**
     * 多片段拼接输出的视频名
     */
    public final static String MERGED_OUT_VIDEO_NAME = "aw_video_merged.mp4";


    public final static int VIDEO_ENCODE_WIDTH = 576;
    public final static int VIDEO_ENCODE_HEIGHT = 1024;
    public final static int VIDEO_ENCODE_BITRATE = 5 * 1024 * 1024;

}
