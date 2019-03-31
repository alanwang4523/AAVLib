package com.alanwang.aavlib.libvideo.camera;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import com.alanwang.aavlib.libutils.ALog;
import com.alanwang.aavlib.libvideo.common.AWVideoSize;
import java.util.List;

/**
 * Author: AlanWang4523.
 * Date: 19/3/31 22:04.
 * Mail: alanwang4523@gmail.com
 */
public class AWCamera {
    private Camera mCamera;
    private boolean mIsFlashlightOn = false;
    private @AWVideoSize.Ratio int mVideoRatio;

    /**
     * 配置相机
     * @param facingId
     * @param videoRatio
     * @return
     */
    public AWCameraInfo config(int facingId, @AWVideoSize.Ratio int videoRatio) throws AWCameraException {
        if (mCamera != null) {
            release();
        }
        mVideoRatio = videoRatio;
        return configCamera(facingId);
    }

    /**
     * 设置相机曝光度
     * @param exposure
     */
    public void setExposure(int exposure) throws AWCameraException {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setExposureCompensation(exposure);
            try {
                mCamera.setParameters(parameters);
            } catch (Exception e) {
                throw new AWCameraException(AWCameraException.ERROR_CAMERA_SET_EXPOSURE_FAILED,
                        "Camera set exposure failed!", e);
            }
        }
    }

    /**
     * 开启闪光灯（不管当前状态）
     */
    public void openFlashLight() throws AWCameraException {
        try {
            if (mCamera == null) {
                throw new AWCameraException(AWCameraException.ERROR_CAMERA_SETTING_FAILED,
                        "Open flash light failed because null camera!");
            }
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(parameters);
            mIsFlashlightOn = true;
        } catch (Exception e) {
            throw new AWCameraException(AWCameraException.ERROR_CAMERA_SET_FLASH_LIGHT_FAILED,
                    "Open flash light failed!", e);
        }

    }

    /**
     * 关闭闪光灯（不管当前状态）
     */
    public void closeFlashlight() throws AWCameraException {
        try {
            if (mCamera == null) {
                return;
            }
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
            mIsFlashlightOn = false;
        } catch (Exception e) {
            throw new AWCameraException(AWCameraException.ERROR_CAMERA_SET_FLASH_LIGHT_FAILED,
                    "Close flash light failed!", e);
        }
    }

    /**
     * 闪光灯是否打开
     * @return
     */
    public boolean isFlashlightOn() {
        return mIsFlashlightOn;
    }

    /**
     * 释放相机
     */
    public void release() {
        try {
            if (mCamera == null) {
                mCamera.setPreviewCallback(null);
                mCamera.release();
                mCamera = null;
                mIsFlashlightOn = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开并配置相机
     * @param facingId
     * @return
     * @throws AWCameraException
     */
    private AWCameraInfo configCamera(int facingId) throws AWCameraException {
        try {
            // 1、开启Camera
            try {
                mCamera = getCameraInstance(facingId);
                ALog.d("getCameraInstance success...id = " + facingId);
            } catch (AWCameraException e) {
                throw e;
            }

            Camera.Parameters parameters = mCamera.getParameters();

            // 2、设置预览照片的图像格式
            List<Integer> supportedPreviewFormats = parameters.getSupportedPreviewFormats();
            if (supportedPreviewFormats.contains(ImageFormat.NV21)) {
                parameters.setPreviewFormat(ImageFormat.NV21);
            } else {
                throw new AWCameraException(AWCameraException.ERROR_CAMERA_SETTING_FAILED, "Set ImageFormat error!");
            }

            // 3、设置预览照片的尺寸, 最接近的尺寸
            AWVideoSize targetSize = AWVideoSize.getExpectCameraSize(mVideoRatio);
            AWVideoSize previewSize = AWCameraUtils.getMostSuitableSize(parameters, targetSize.width, targetSize.height);
            if (previewSize == null) {
                previewSize = AWVideoSize.getDefaultCameraSize(mVideoRatio);
            }
            parameters.setPreviewSize(previewSize.width, previewSize.height);

            //下面这行设置 有可能导致 返回的图像尺寸和预期不一致
//			parameters.setRecordingHint(true);

            // 4、设置视频记录的连续自动对焦模式
            if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }

            // 5、设置闪光灯默认关闭状态
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

            try {
                mCamera.setParameters(parameters);
            } catch (Exception e) {
                throw new AWCameraException(AWCameraException.ERROR_CAMERA_SETTING_FAILED, "Set camera parameters error!");
            }

            int degree = getCameraDisplayOrientation(facingId);
            mCamera.setDisplayOrientation(degree);
            return new AWCameraInfo(facingId, previewSize.width, previewSize.height, degree);
        } catch (Exception e) {
            throw new AWCameraException(AWCameraException.ERROR_CAMERA_UNKNOWN_ERROR, "Unknown error!", e);
        }
    }

    /**
     * 根据 facing id 获取相机实例
     * @param id
     * @return
     * @throws AWCameraException
     */
    private Camera getCameraInstance(final int id) throws AWCameraException {
        Camera camera;
        try {
            camera = Camera.open(id);
        } catch (Exception e) {
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append("Open the ")
                    .append((id == Camera.CameraInfo.CAMERA_FACING_FRONT) ? "front camera " : "back camera ")
                    .append("failed!");
            throw new AWCameraException(AWCameraException.ERROR_CAMERA_OPEN_FAILED, strBuilder.toString(), e);
        }
        return camera;
    }

    /**
     * 获取相机显示方向
     * @param cameraId
     * @return
     */
    private static int getCameraDisplayOrientation(int cameraId) {
        int result;
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation) % 360;
            result = (360 - result) % 360;
        } else { // back-facing
            result = (info.orientation + 360) % 360;
        }
        return result;
    }
}
