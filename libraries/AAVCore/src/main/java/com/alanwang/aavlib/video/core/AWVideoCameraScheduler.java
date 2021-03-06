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
package com.alanwang.aavlib.video.core;

import android.hardware.Camera;
import android.opengl.GLES20;
import android.view.Surface;
import com.alanwang.aavlib.image.filters.common.FilterCategory;
import com.alanwang.aavlib.image.filters.common.FilterType;
import com.alanwang.aavlib.image.processors.AWFilterProcessor;
import com.alanwang.aavlib.opengl.common.AWMessage;
import com.alanwang.aavlib.opengl.render.AWIOSurfaceProxy;
import com.alanwang.aavlib.video.camera.AWCamera;
import com.alanwang.aavlib.video.camera.AWCameraException;
import com.alanwang.aavlib.video.common.AWVideoSize;
import com.alanwang.aavlib.video.common.IEncodeTimeProvider;

/**
 * Author: AlanWang4523.
 * Date: 19/4/2 08:25.
 * Mail: alanwang4523@gmail.com
 */
public class AWVideoCameraScheduler {

    private AWCamera mCamera;
    private AWIOSurfaceProxy mIOSurfaceProxy;
    private AWVideoFileRecorder mVideoFileRecorder;
    private AWFilterProcessor mFilterProcessor;
    
    private boolean mIsCameraOpen = false;

    public AWVideoCameraScheduler() {
        mCamera = new AWCamera();
        mFilterProcessor = new AWFilterProcessor(new int[]{FilterCategory.FC_STYLE});
        
        mIOSurfaceProxy = new AWIOSurfaceProxy();
        mIOSurfaceProxy.setOnInputSurfaceListener(mOnInputSurfaceListener);
        mIOSurfaceProxy.setOnOutputSurfaceListener(mOnOutputSurfaceListener);
        mIOSurfaceProxy.setOnPassFilterListener(mOnPassFilterListener);
        mIOSurfaceProxy.setOnMessageListener(mOnMessageListener);
    }

    /**
     * 切换摄像头
     * @param isFrontCamera
     */
    public void switchCamera(boolean isFrontCamera) {
        mIOSurfaceProxy.postMessage(new AWMessage(AWMessage.MSG_CAMERA_SWITCH,
                isFrontCamera ? Camera.CameraInfo.CAMERA_FACING_FRONT :
                        Camera.CameraInfo.CAMERA_FACING_BACK));
    }

    /**
     * 开关闪光灯
     * @param isOpenFlashlight
     */
    public void toggleFlashlight(boolean isOpenFlashlight) {
        mIOSurfaceProxy.postMessage(new AWMessage(AWMessage.MSG_CAMERA_TOGGLE_FLASH_LIGHT,
                isOpenFlashlight ? 1 : 0));
    }

    /**
     * 更新输出的 surface
     * @param surface
     * @param w
     * @param h
     */
    public void updateSurface(Surface surface, int w, int h) {
        mIOSurfaceProxy.updateSurface(surface, w, h);
    }

    /**
     * 销毁输出的 surface
     */
    public void destroySurface() {
        mIOSurfaceProxy.destroySurface();
    }

    /**
     * 设置滤镜参数
     * @param filterType
     * @param level
     */
    public void setFilter(@FilterType int filterType, float level) {
        mFilterProcessor.setFilter(filterType, level);
    }

    /**
     * 跟录制相关的功能必须在此方法之后调用
     * @param width
     * @param height
     * @param bitRate
     */
    public void setupRecord(int width, int height, int bitRate) {
        mVideoFileRecorder = new AWVideoFileRecorder(mIOSurfaceProxy.getSharedEGLContext());
        mVideoFileRecorder.setupRecord(width, height, bitRate);
    }

    /**
     * 设置编码时间提供器
     * @param encodeTimeProvider
     */
    public void setEncodeTimeProvider(IEncodeTimeProvider encodeTimeProvider) {
        if (mVideoFileRecorder != null) {
            mVideoFileRecorder.setEncodeTimeProvider(encodeTimeProvider);
        } else {
            throw new IllegalStateException("Could not be called before setupRecord!");
        }
    }

    /**
     * 开始录制，调用该方法会新产生一个视频文件，如果想录制到同一个文件，使用 {@link #pauseRecord()} {@link #resumeRecord()}
     * @param path
     */
    public void startRecord(String path) {
        if (mVideoFileRecorder != null) {
            mVideoFileRecorder.startRecord(path);
        }
    }

    /**
     * 暂停录制
     */
    public void pauseRecord() {
        if (mVideoFileRecorder != null) {
            mVideoFileRecorder.pauseRecord();
        }
    }

    /**
     * 恢复录制，继续向当前文件写入
     */
    public void resumeRecord() {
        if (mVideoFileRecorder != null) {
            mVideoFileRecorder.resumeRecord();
        }
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        if (mVideoFileRecorder != null) {
            mVideoFileRecorder.stopRecord();
        }
    }

    /**
     * 结束整个录制
     */
    public void finishRecord() {
        if (mVideoFileRecorder != null) {
            mVideoFileRecorder.finishRecord();
            mVideoFileRecorder.release();
            mVideoFileRecorder = null;
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        mIOSurfaceProxy.release();
    }

    private AWIOSurfaceProxy.OnInputSurfaceListener mOnInputSurfaceListener = new AWIOSurfaceProxy.OnInputSurfaceListener() {
        @Override
        public void onInputSurfaceCreated(Surface surface) {
            mFilterProcessor.initialize();
        }

        @Override
        public void onInputSurfaceDestroyed() {
            mFilterProcessor.release();
        }
    };

    private AWIOSurfaceProxy.OnOutputSurfaceListener mOnOutputSurfaceListener = new AWIOSurfaceProxy.OnOutputSurfaceListener() {
        @Override
        public void onOutputSurfaceUpdated(Surface surface, int w, int h) {
            if (!mIsCameraOpen) {
                try {
                    AWVideoSize textureSize = AWVideoSize.getTextureSize(AWVideoSize.Ratio.RATIO_16_9);
                    mIOSurfaceProxy.setTextureSize(textureSize.width, textureSize.height);

                    mCamera.config(Camera.CameraInfo.CAMERA_FACING_FRONT, AWVideoSize.Ratio.RATIO_16_9);
                    mCamera.setPreviewTexture(mIOSurfaceProxy.getInputSurfaceTexture());
                    mIsCameraOpen = true;
                } catch (AWCameraException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onOutputSurfaceDestroyed() {
            mCamera.release();
            mIsCameraOpen = false;
        }
    };

    private AWIOSurfaceProxy.OnPassFilterListener mOnPassFilterListener = new AWIOSurfaceProxy.OnPassFilterListener() {
        @Override
        public int onPassFilter(int textureId, int width, int height) {
            int outputTexture = mFilterProcessor.processFrame(textureId, width, height);
            GLES20.glFinish();
            if (mVideoFileRecorder != null) {
                mVideoFileRecorder.encodeFrame(outputTexture);
            }
            return outputTexture;
        }
    };

    private AWIOSurfaceProxy.OnMessageListener mOnMessageListener = new AWIOSurfaceProxy.OnMessageListener() {
        @Override
        public void onHandleMessage(AWMessage msg) {
            if (msg.msgWhat == AWMessage.MSG_CAMERA_SWITCH) {
                try {
                    mCamera.config(msg.msgArg1, AWVideoSize.Ratio.RATIO_16_9);
                    mCamera.setPreviewTexture(mIOSurfaceProxy.getInputSurfaceTexture());
                    mIsCameraOpen = true;
                } catch (AWCameraException e) {
                    e.printStackTrace();
                }
            } else if (msg.msgWhat == AWMessage.MSG_CAMERA_TOGGLE_FLASH_LIGHT) {
                try {
                    if (msg.msgArg1 == 1) {
                        mCamera.openFlashLight();
                    } else {
                        mCamera.closeFlashlight();
                    }
                } catch (AWCameraException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
