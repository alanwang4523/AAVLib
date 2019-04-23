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
package com.alanwang.aavlib.libvideo.core;

import android.opengl.EGLContext;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import com.alanwang.aavlib.libeglcore.common.AWCoordinateUtil;
import com.alanwang.aavlib.libeglcore.egl.AWEGLCoreWrapper;
import com.alanwang.aavlib.libeglcore.render.AWSurfaceRender;
import com.alanwang.aavlib.libmediacore.encoder.AWVideoRecordEncoder;
import com.alanwang.aavlib.libvideo.common.DefaultEncodeTimeProvider;
import com.alanwang.aavlib.libvideo.common.IEncodeTimeProvider;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Author: AlanWang4523.
 * Date: 19/4/9 00:27.
 * Mail: alanwang4523@gmail.com
 */
public class AWVideoFileRecorder {

    private static final String TAG = AWVideoFileRecorder.class.getSimpleName();

    private static final int MSG_QUIT = 0;
    private static final int MSG_PREPARE_ENCODER = 1;
    private static final int MSG_START_RECORDING = 2;
    private static final int MSG_STOP_RECORDING = 3;
    private static final int MSG_FINISH_RECORDING = 4;
    private static final int MSG_FRAME_AVAILABLE = 5;

    private final Handler mHandler;
    private final HandlerThread mHandlerThread;
    private final AWEGLCoreWrapper mEGLCoreWrapper;
    private AWSurfaceRender mSurfaceRender;
    private AWVideoRecordEncoder mVideoEncoderCore;
    private IEncodeTimeProvider mEncodeTimeProvider;
    private DefaultEncodeTimeProvider mDefaultEncodeTimeProvider;

    private volatile boolean isEnableEncode = false;
    private int mVideoWidth = -1;
    private int mVideoHeight = -1;
    private int mBitrate;

    public AWVideoFileRecorder(EGLContext shareContext) {
        mEGLCoreWrapper = new AWEGLCoreWrapper(shareContext);
        this.mHandlerThread = new HandlerThread(TAG);
        this.mHandlerThread.start();
        this.mHandler = new EncodeHandler(this, this.mHandlerThread.getLooper());
    }

    /**
     * 设置编码时间提供器
     * @param encodeTimeProvider
     */
    public void setEncodeTimeProvider(IEncodeTimeProvider encodeTimeProvider) {
        this.mEncodeTimeProvider = encodeTimeProvider;
    }

    /**
     * 设置录制参数
     * @param width
     * @param height
     * @param bitRate
     */
    public void setupRecord(int width, int height, int bitRate) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_PREPARE_ENCODER,
                new EncoderArg(width, height, bitRate)));
    }

    /**
     * 处理录制参数，与 {@link #finishRecord()} 为一组配套使用
     * @param width
     * @param height
     * @param bitRate
     */
    private void handleSetupRecord(int width, int height, int bitRate) {

        mSurfaceRender = new AWSurfaceRender();
        mSurfaceRender.updateTextureCoord(AWCoordinateUtil.DEFAULT_TEXTURE_COORDS);

        mVideoWidth = width;
        mVideoHeight = height;
        mBitrate = bitRate;
    }

    /**
     * 开始录制，新建一个录制文件，与 {@link #stopRecord()} 对应使用
     * @param filePath
     */
    public void startRecord(String filePath) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_START_RECORDING, filePath));
    }

    /**
     * 处理开始录制
     * @param filePath
     */
    private void handleStartRecord(String filePath) {
        // 视频编码器
        mVideoEncoderCore = new AWVideoRecordEncoder();
        try {
            mVideoEncoderCore.setup(mVideoWidth, mVideoHeight, mBitrate);
            mVideoEncoderCore.startRecord(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mEGLCoreWrapper.createSurface(mVideoEncoderCore.getInputSurface());
        mEGLCoreWrapper.makeCurrent();

        // 如果外部没有设置编码时间提供器，则使用默认的
        if (mEncodeTimeProvider == null && mDefaultEncodeTimeProvider == null) {
            mDefaultEncodeTimeProvider = new DefaultEncodeTimeProvider();
        }
        if (mDefaultEncodeTimeProvider != null) {
            mDefaultEncodeTimeProvider.reset();
        }

        isEnableEncode = true;
    }

    /**
     * 停止录制，结束一个录制文件，与 {@link #startRecord} 对应
     */
    public void stopRecord() {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_STOP_RECORDING));
    }

    /**
     * 处理停止录制
     */
    private void handleStopRecord() {
        isEnableEncode = false;
        if (mVideoEncoderCore != null) {// 避免外部不调用 start 就直接调 stop
            mVideoEncoderCore.stopRecord();
            mVideoEncoderCore.release();
            mVideoEncoderCore = null;
        }
    }

    /**
     * 暂停录制，暂停往录制文件写入数据
     */
    public void pauseRecord() {
        if (mDefaultEncodeTimeProvider != null) {
            mDefaultEncodeTimeProvider.pauseRecord();
        }
        isEnableEncode = false;
    }

    /**
     * 恢复录制，继续向当前的录制文件写入数据
     */
    public void resumeRecord() {
        if (mDefaultEncodeTimeProvider != null) {
            mDefaultEncodeTimeProvider.resumeRecord();
        }
        isEnableEncode = true;
    }

    /**
     * 结束录制，与 {@link #setupRecord} 对应
     */
    public void finishRecord() {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_FINISH_RECORDING));
    }

    /**
     * 处理结束录制
     */
    public void handleFinishRecord() {

        isEnableEncode = false;
        if (mSurfaceRender != null) {
            mSurfaceRender.release();
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_QUIT));
    }

    /**
     * 处理释放资源
     */
    private void handleRelease() {
        mEGLCoreWrapper.release();
    }

    /**
     * 编码数据
     * @param textureId
     */
    public void encodeFrame(int textureId) {
        if (isEnableEncode) {
            long encodeTimestamp = getEncodeTimeStampMs();
            mHandler.sendMessage(mHandler.obtainMessage(MSG_FRAME_AVAILABLE, new FrameArg(textureId, encodeTimestamp)));
        }
    }

    /**
     * 返回编码时间戳，单位 ：纳秒
     * @return
     */
    private long getEncodeTimeStampMs() {
        long encodeTimestampMs;
        if (mEncodeTimeProvider != null) {
            encodeTimestampMs = mEncodeTimeProvider.getTimeStampMS();
        } else {
            encodeTimestampMs = mDefaultEncodeTimeProvider.getTimeStampMS();
        }

        return encodeTimestampMs;
    }

    /**
     * 真正的处理编码数据
     * @param textureId
     * @param encodeTimestamp
     */
    private void handleEncodeAFrame(int textureId, long encodeTimestamp) {
        if (isEnableEncode) {
            mSurfaceRender.drawFrame(textureId, mVideoWidth, mVideoHeight);

            mEGLCoreWrapper.setPresentationTime(encodeTimestamp * 1000 * 1000);
            mEGLCoreWrapper.swapBuffers();
            mVideoEncoderCore.drainEncoder(false);
        }
    }

    private static class EncoderArg {
        public int width;
        public int height;
        public int bitRate;

        public EncoderArg(int width, int height, int bitRate) {
            this.width = width;
            this.height = height;
            this.bitRate = bitRate;
        }
    }

    private static class FrameArg {
        public int textureId;
        public long encodeTimestamp;

        public FrameArg(int textureId, long encodeTimestamp) {
            this.textureId = textureId;
            this.encodeTimestamp = encodeTimestamp;
        }
    }

    private static class EncodeHandler extends Handler {

        private WeakReference<AWVideoFileRecorder> mWeakRef;
        public EncodeHandler(AWVideoFileRecorder videoFileRecorder, Looper looper) {
            super(looper);
            mWeakRef = new WeakReference<>(videoFileRecorder);
        }

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            Object object = msg.obj;

            AWVideoFileRecorder videoFileRecorder = mWeakRef.get();
            if (videoFileRecorder == null) {
                return;
            }

            switch (what) {
                case MSG_PREPARE_ENCODER:
                    EncoderArg config = (EncoderArg) object;
                    videoFileRecorder.handleSetupRecord(config.width, config. height, config.bitRate);
                    break;
                case MSG_START_RECORDING:
                    String filePath = (String) object;
                    videoFileRecorder.handleStartRecord(filePath);
                    break;
                case MSG_STOP_RECORDING:
                    videoFileRecorder.handleStopRecord();
                    break;
                case MSG_FINISH_RECORDING:
                    videoFileRecorder.handleFinishRecord();
                    break;
                case MSG_FRAME_AVAILABLE:
                    FrameArg frameArg = (FrameArg) object;
                    videoFileRecorder.handleEncodeAFrame(frameArg.textureId, frameArg.encodeTimestamp);
                    break;
                case MSG_QUIT:
                    videoFileRecorder.handleRelease();
                    Looper looper = Looper.myLooper();
                    if (looper != null) {
                        looper.quit();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
