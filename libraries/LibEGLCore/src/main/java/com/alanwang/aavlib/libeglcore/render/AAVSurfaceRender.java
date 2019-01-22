package com.alanwang.aavlib.libeglcore.render;

import android.opengl.GLES20;
import com.alanwang.aavlib.libeglcore.common.AAVCoordinateUtil;
import com.alanwang.aavlib.libeglcore.egl.GlUtil;
import java.nio.FloatBuffer;

/**
 * AAVSurfaceRender
 * 通用的 AAVSurfaceRender ，支持将纹理渲染到 FrameBuffer、屏幕、编码器
 *
 * Author: AlanWang4523.
 * Date: 19/1/23 00:29.
 * Mail: alanwang4523@gmail.com
 */

public class AAVSurfaceRender extends AAVBaseRender {

    private FloatBuffer mTextureCoordinateFBuffer;

    public AAVSurfaceRender() {
        super();
        mTextureCoordinateFBuffer = GlUtil.createFloatBuffer(AAVCoordinateUtil.TEXTURE_NO_ROTATION);
    }

    /**
     * 更新纹理坐标
     * @param fArr
     */
    public void updateTextureCoord(float[] fArr) {
        this.mTextureCoordinateFBuffer.put(fArr).position(0);
    }

    /**
     * 渲染数据到 surface
     * @param textureId 要渲染的纹理
     * @param width 视口的宽
     * @param height 视口的高
     */
    public void drawFrame(int textureId, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        super.drawFrame(textureId, DEFAULT_VERTEX_COORDINATE_BUFFER, mTextureCoordinateFBuffer);
    }
}
