package com.alanwang.aavlib.libeglcore.common;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Surface;
import com.alanwang.aavlib.libeglcore.egl.GlUtil;
import com.alanwang.aavlib.libeglcore.render.AAVOESTextureRender;
import java.lang.ref.WeakReference;

/**
 * 自定义 SurfaceTexture ，注意其需要在 GL 线程初始化
 * Author: AlanWang4523.
 * Date: 19/1/23 23:41.
 * Mail: alanwang4523@gmail.com
 */

public class AAVSurfaceTexture {

    private int mTextureId;
    private final SurfaceTexture mSurfaceTexture;
    private Surface mSurface;
    private final AAVOESTextureRender mTextureRender;
    private AAVFrameAvailableListener mFrameAvailableListener;

    public AAVSurfaceTexture() {
        mTextureRender = new AAVOESTextureRender();
        mTextureId = GlUtil.createOESTexture();
        if (Build.VERSION.SDK_INT >= 19) {
            mSurfaceTexture = new SurfaceTexture(mTextureId, false);
        } else {
            mSurfaceTexture = new SurfaceTexture(mTextureId);
        }
        mSurfaceTexture.setOnFrameAvailableListener(new InternalFrameAvailableListener(this));
    }

    /**
     * 设置 AAVFrameAvailableListener
     * @param frameAvailableListener
     */
    public void setFrameAvailableListener(AAVFrameAvailableListener frameAvailableListener) {
        mFrameAvailableListener = frameAvailableListener;
    }

    /**
     * 获取 textureID
     * @return
     */
    public int getTextureId() {
        return mTextureId;
    }

    /**
     * 获取 mSurfaceTexture，如可以通过该方法将 mSurfaceTexture 绑定到相机，
     * 相机输出的数据可以通过 mTextureId 拿到，可以通过 mTextureId 对其过滤镜
     * @return
     */
    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    /**
     * 获取 surface，如视频播放器需要绑定 surface，可以通过该方法获取
     * 将其绑定到 Player 后其界面播放的数据就会更新到 mSurfaceTexture，
     * 也可以通过 mTextureId 对其过滤镜
     * @return
     */
    public Surface getSurface() {
        if (mSurface == null) {
            mSurface = new Surface(mSurfaceTexture);
        }
        return mSurface;
    }

    /**
     * 更新数据
     */
    public void updateTexImage() {
        updateTexImage(null);
    }

    /**
     * 更新数据
     * @param transformMatrix
     */
    public synchronized void updateTexImage(float[] transformMatrix) {
        try {
            mSurfaceTexture.updateTexImage();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        if (transformMatrix != null) {
            mSurfaceTexture.getTransformMatrix(transformMatrix);
        }
    }

    /**
     * 讲数据绘制到 frameBuffer
     * @param frameBuffer
     * @param width
     * @param height
     * @return
     */
    public int drawFrame(@NonNull AAVFrameBufferObject frameBuffer, int width, int height) {
        frameBuffer.checkInit(width, height);
        frameBuffer.bindFrameBuffer();
        GLES20.glViewport(0, 0, width, height);
        mTextureRender.drawFrame(this);
        frameBuffer.unbindFrameBuffer();

        return frameBuffer.getOutputTextureId();
    }

    /**
     * 获取最近调用 updateTexImage 关联的纹理的时间戳
     * @return
     */
    public long getTimestamp() {
        return mSurfaceTexture.getTimestamp();
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mTextureId != -1) {
            GLES20.glDeleteTextures(1, new int[]{mTextureId}, 0);
            mTextureId = -1;
        }
        mSurfaceTexture.setOnFrameAvailableListener(null);
        mSurfaceTexture.release();
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
    }


    private static class InternalFrameAvailableListener implements SurfaceTexture.OnFrameAvailableListener {

        private WeakReference<AAVSurfaceTexture> weakReference;

        public InternalFrameAvailableListener(AAVSurfaceTexture aavSurfaceTexture) {
            weakReference = new WeakReference<>(aavSurfaceTexture);
        }

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            AAVSurfaceTexture aavSurfaceTexture = weakReference.get();
            if (aavSurfaceTexture == null) {
                return;
            }
            if (aavSurfaceTexture.mFrameAvailableListener != null) {
                aavSurfaceTexture.mFrameAvailableListener.onFrameAvailable(aavSurfaceTexture);
            }
        }
    }
}
