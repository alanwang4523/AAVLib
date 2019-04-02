package com.alanwang.aavlib.libvideo.core;

import android.hardware.Camera;
import android.view.Surface;
import com.alanwang.aavlib.libeglcore.common.AWMessage;
import com.alanwang.aavlib.libeglcore.render.AWIOSurfaceProxy;
import com.alanwang.aavlib.libvideo.camera.AWCamera;
import com.alanwang.aavlib.libvideo.camera.AWCameraException;
import com.alanwang.aavlib.libvideo.common.AWVideoSize;

/**
 * Author: AlanWang4523.
 * Date: 19/4/2 08:25.
 * Mail: alanwang4523@gmail.com
 */
public class AWCameraRecordController {

    private AWCamera mCamera;
    private AWIOSurfaceProxy mIOSurfaceProxy;
    private boolean mIsCameraOpen = false;

    public AWCameraRecordController() {
        mCamera = new AWCamera();
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
     * 更新 surface
     * @param surface
     * @param w
     * @param h
     */
    public void updateSurface(Surface surface, int w, int h) {
        mIOSurfaceProxy.updateSurface(surface, w, h);
    }

    /**
     * 销毁 surface
     */
    public void destroySurface() {
        mIOSurfaceProxy.destroySurface();
    }

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
            mIsCameraOpen = false;
        }
    };

    private AWIOSurfaceProxy.OnPassFilterListener mOnPassFilterListener = new AWIOSurfaceProxy.OnPassFilterListener() {
        @Override
        public int onPassFilter(int textureId, int width, int height) {
            return textureId;
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
