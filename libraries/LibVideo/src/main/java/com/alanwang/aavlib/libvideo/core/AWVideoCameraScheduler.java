package com.alanwang.aavlib.libvideo.core;

import android.hardware.Camera;
import android.view.Surface;
import com.alanwang.aavlib.libeglcore.common.AWMessage;
import com.alanwang.aavlib.libeglcore.render.AWIOSurfaceProxy;
import com.alanwang.aavlib.libvideo.camera.AWCamera;
import com.alanwang.aavlib.libvideo.camera.AWCameraException;
import com.alanwang.aavlib.libvideo.common.AWVideoSize;
import com.alanwang.aavlib.libvideo.common.IEncodeTimeProvider;
import com.alanwang.aavlib.libvideoeffect.processors.AWCameraPreviewVEProcessor;

/**
 * Author: AlanWang4523.
 * Date: 19/4/2 08:25.
 * Mail: alanwang4523@gmail.com
 */
public class AWVideoCameraScheduler {

    private AWCamera mCamera;
    private AWIOSurfaceProxy mIOSurfaceProxy;
    private AWCameraPreviewVEProcessor mVEProcessor;
    private AWVideoFileRecorder mVideoFileRecorder;
    
    private boolean mIsCameraOpen = false;
    private boolean mTestEnableEffect = false;// 测试是否使用滤镜

    public AWVideoCameraScheduler() {
        mCamera = new AWCamera();
        mVEProcessor = new AWCameraPreviewVEProcessor();
        
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

        }

        @Override
        public void onInputSurfaceDestroyed() {

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
            mVEProcessor.release();
            mIsCameraOpen = false;
        }
    };

    private AWIOSurfaceProxy.OnPassFilterListener mOnPassFilterListener = new AWIOSurfaceProxy.OnPassFilterListener() {
        @Override
        public int onPassFilter(int textureId, int width, int height) {
            int outputTexture = textureId;
            if (mTestEnableEffect) {
                outputTexture = mVEProcessor.processFrame(textureId, width, height);
            }
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
