package com.alanwang.aavlib.libeglcore.render;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.opengl.GLES20;
import com.alanwang.aavlib.libeglcore.common.AAVCoordinateUtil;
import com.alanwang.aavlib.libeglcore.common.AAVRect;
import com.alanwang.aavlib.libeglcore.egl.GlUtil;
import java.nio.FloatBuffer;

/**
 * Author: AlanWang4523.
 * Date: 19/1/23 00:58.
 * Mail: alanwang4523@gmail.com
 */

public class AAVWatermarkRender extends AAVBaseRender {

    private static FloatBuffer TEXTURE_COORDINATE_BUFFER = GlUtil.createFloatBuffer(AAVCoordinateUtil.TEXTURE_NO_ROTATION);

    private FloatBuffer mVertexCoordinateBuffer;
    private int mWatermarkTextureId = -1;
    private Bitmap mWatermarkBmp;
    private AAVRect mWatermarkPos;
    private int mSrcTextureWidth = -1;
    private int mSrcTextureHeight = -1;
    private volatile boolean mIsNeedUpdate = false;

    public AAVWatermarkRender() {
        super();
    }

    /**
     * 设置水印及其位置
     * @param bitmap
     * @param position
     */
    public void setWatermark(Bitmap bitmap, AAVRect position) {
        synchronized (AAVWatermarkRender.this) {
            mWatermarkBmp = bitmap;
            mWatermarkPos = position;
            mIsNeedUpdate = true;
        }
    }

    /**
     * 更新水印位置
     * @param position
     */
    public void updatePostion(AAVRect position) {
        synchronized (AAVWatermarkRender.this) {
            mWatermarkPos = position;
            mIsNeedUpdate = true;
        }
    }

    /**
     * 渲染水印
     * @param srcWidth 原始纹理宽度
     * @param srcHeight 原始纹理高度
     */
    public void draw(int srcWidth, int srcHeight) {
        if (mSrcTextureWidth != srcWidth || mSrcTextureHeight != srcHeight) {
            mIsNeedUpdate = true;
        }
        updateWatermark();

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        super.drawFrame(mWatermarkTextureId, mVertexCoordinateBuffer, TEXTURE_COORDINATE_BUFFER);
        GLES20.glDisable(GLES20.GL_BLEND);
    }

    /**
     * 更新水印图片或位置
     */
    private void updateWatermark() {
        if (mIsNeedUpdate) {
            synchronized (AAVWatermarkRender.this) {
                if (mWatermarkBmp != null) {
                    mWatermarkTextureId = GlUtil.loadImageTexture(mWatermarkBmp);
                    mWatermarkBmp = null;
                }

                Rect rect = mWatermarkPos.getRect();
                float leftX = (rect.left / (mSrcTextureWidth / 2.0f) - 1.0f);
                float rightX = (rect.right / (mSrcTextureWidth / 2.0f) - 1.0f);
                float topY = 1.0f - rect.top / (mSrcTextureHeight / 2.0f);
                float bottomY = 1.0f - rect.bottom / (mSrcTextureHeight / 2.0f);

                float vertexCoords[] = {
                        leftX, bottomY,  // 0 bottom left
                        rightX, bottomY,   // 1 bottom right
                        leftX,  topY,  // 2 top left
                        rightX, topY,   // 3 top right
                };
                mVertexCoordinateBuffer = GlUtil.createFloatBuffer(vertexCoords);

                mIsNeedUpdate = false;
            }
        }
    }
}
