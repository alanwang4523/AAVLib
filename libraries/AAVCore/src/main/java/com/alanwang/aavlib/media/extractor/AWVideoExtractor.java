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
package com.alanwang.aavlib.media.extractor;

import android.media.MediaCodec;
import android.text.TextUtils;
import com.alanwang.aavlib.media.listener.AWExtractorListener;

import java.nio.ByteBuffer;

/**
 * Author: AlanWang4523.
 * Date: 19/2/20 00:42.
 * Mail: alanwang4523@gmail.com
 */
public class AWVideoExtractor extends AWMediaExtractor {

    private AWExtractorListener mAWExtractorListener;

    /**
     * 设置抽取数据的监听器
     * @param extractorListener
     */
    public void setExtractorListener(AWExtractorListener extractorListener) {
        this.mAWExtractorListener = extractorListener;
    }

    @Override
    protected boolean isTheInterestedTrack(String keyMimeString) {
        if (!TextUtils.isEmpty(keyMimeString) && keyMimeString.startsWith("video")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDataAvailable(ByteBuffer extractBuffer, MediaCodec.BufferInfo bufferInfo) {
        if (mAWExtractorListener != null) {
            mAWExtractorListener.onDataAvailable(extractBuffer, bufferInfo);
        }
    }
}
