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
package com.alanwang.aavlib.libmediacore.common;

import android.media.MediaCodec;
import java.nio.ByteBuffer;

/**
 * Author: AlanWang4523.
 * Date: 19/2/19 00:56.
 * Mail: alanwang4523@gmail.com
 */
public class AWCodecFrame {
    // buffer
    private ByteBuffer mBuffer;
    // bufferInfo
    private MediaCodec.BufferInfo mBufferInfo;

    public AWCodecFrame(ByteBuffer buffer, MediaCodec.BufferInfo bufferInfo) {
        this.mBuffer = buffer;
        this.mBufferInfo = bufferInfo;
    }

    public ByteBuffer getBuffer() {
        return mBuffer;
    }

    public void setBuffer(ByteBuffer mBuffer) {
        this.mBuffer = mBuffer;
    }

    public MediaCodec.BufferInfo getBufferInfo() {
        return mBufferInfo;
    }

    public void setBufferInfo(MediaCodec.BufferInfo mBufferInfo) {
        this.mBufferInfo = mBufferInfo;
    }
}
