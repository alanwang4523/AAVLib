package com.alanwang.aavlib.libeglcore.render;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.alanwang.aavlib.libeglcore.common.AAVCoordinateUtil;
import com.alanwang.aavlib.libeglcore.egl.GlUtil;

import java.nio.FloatBuffer;

/**
 * AAVOESTextureRender
 * 用于 OES 纹理渲染，OES 纹理可来源于相机、player、编码器
 *
 * Author: AlanWang4523.
 * Date: 19/1/23 00:19.
 * Mail: alanwang4523@gmail.com
 */

public class AAVOESTextureRender extends AAVBaseRender {

    private static final String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;\n" +
                    "uniform mat4 uSTMatrix;\n" +
                    "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "  gl_Position = uMVPMatrix * aPosition;\n" +
                    "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                    "}\n";

    private static final String FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision highp float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                    "}\n";

    private float[] mMVPMatrix = new float[16];
    private float[] mTextureTransformMatrix = new float[16];
    private int muMVPMatrixLoc;
    private int muTexMatrixLoc;

    private static final FloatBuffer VERTEX_COORDINATE_BUFFER =
            GlUtil.createFloatBuffer(AAVCoordinateUtil.DEFAULT_VERTEX_COORDS);

    private static final FloatBuffer TEXTURE_COORDINATE_BUFFER =
            GlUtil.createFloatBuffer(AAVCoordinateUtil.DEFAULT_TEXTURE_COORDS);


    public AAVOESTextureRender() {
        super(VERTEX_SHADER, FRAGMENT_SHADER, GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        Matrix.setIdentityM(this.mMVPMatrix, 0);
        Matrix.setIdentityM(this.mTextureTransformMatrix, 0);
    }

    /**
     * 沿 Z 轴旋转
     * @param i
     */
    public void rotate(int i) {
        resetMVPMatrix();
        rotate(i, 0, 0, 1);
    }

    /**
     * 沿各轴旋转
     * @param angle
     * @param x
     * @param y
     * @param z
     */
    public void rotate(int angle, int x, int y, int z) {
        Matrix.rotateM(this.mMVPMatrix, 0, (float) angle, (float) x, (float) y, (float) z);
    }

    /**
     * 重置 MVP 矩阵
     */
    public void resetMVPMatrix() {
        Matrix.setIdentityM(this.mMVPMatrix, 0);
    }

    /**
     * 渲染纹理
     * @param textureId
     */
    public void drawFrame(int textureId) {
        super.drawFrame(textureId, VERTEX_COORDINATE_BUFFER, TEXTURE_COORDINATE_BUFFER);
    }

    @Override
    protected boolean onInitialized() {
        this.muMVPMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uMVPMatrix");
        this.muTexMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uSTMatrix");
        return true;
    }

    @Override
    protected void preDraw() {
        GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, mTextureTransformMatrix, 0);
    }
}
