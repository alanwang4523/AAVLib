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
package com.alanwang.aavlib.libeglcore.common;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Surface;
import com.alanwang.aavlib.libeglcore.egl.GlUtil;
import com.alanwang.aavlib.libeglcore.render.AWOESTextureRender;
import java.lang.ref.WeakReference;

/**
 * 自定义 SurfaceTexture ，注意其需要在 GL 线程初始化
 * Author: AlanWang4523.
 * Date: 19/1/23 23:41.
 * Mail: alanwang4523@gmail.com
 */

public class AWSurfaceTexture {

    private int mTextureId;
    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;
    private AWOESTextureRender mTextureRender;
    private AWFrameAvailableListener mFrameAvailableListener;

    public AWSurfaceTexture() {
        mTextureRender = new AWOESTextureRender();
        mTextureId = GlUtil.createOESTexture();
        if (Build.VERSION.SDK_INT >= 19) {
            mSurfaceTexture = new SurfaceTexture(mTextureId, false);
        } else {
            mSurfaceTexture = new SurfaceTexture(mTextureId);
        }
        mSurfaceTexture.setOnFrameAvailableListener(new InternalFrameAvailableListener(this));
    }

    /**
     * 设置 AWFrameAvailableListener
     * @param frameAvailableListener
     */
    public void setFrameAvailableListener(AWFrameAvailableListener frameAvailableListener) {
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
    public int drawFrame(@NonNull AWFrameBuffer frameBuffer, int width, int height) {
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
        if (mTextureRender != null) {
            mTextureRender.release();
            mTextureRender = null;
        }
        if (mSurfaceTexture != null) {
            mSurfaceTexture.setOnFrameAvailableListener(null);
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }

        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
    }

    private static class InternalFrameAvailableListener implements SurfaceTexture.OnFrameAvailableListener {

        private WeakReference<AWSurfaceTexture> weakReference;

        public InternalFrameAvailableListener(AWSurfaceTexture AWSurfaceTexture) {
            weakReference = new WeakReference<>(AWSurfaceTexture);
        }

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            AWSurfaceTexture AWSurfaceTexture = weakReference.get();
            if (AWSurfaceTexture == null) {
                return;
            }
            if (AWSurfaceTexture.mFrameAvailableListener != null) {
                AWSurfaceTexture.mFrameAvailableListener.onFrameAvailable(AWSurfaceTexture);
            }
        }
    }
}
