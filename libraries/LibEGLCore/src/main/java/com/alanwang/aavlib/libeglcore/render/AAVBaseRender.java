package com.alanwang.aavlib.libeglcore.render;

import android.opengl.GLES20;
import com.alanwang.aavlib.libeglcore.common.AAVCoordinateUtil;
import com.alanwang.aavlib.libeglcore.egl.GlUtil;
import java.nio.FloatBuffer;

/**
 * Author: AlanWang4523.
 * Date: 19/1/22 23:58.
 * Mail: alanwang4523@gmail.com
 */

public abstract class AAVBaseRender {

    protected static final String DEFAULT_VERTEX_SHADER =
            "attribute vec4 aPosition;\n" +
                    "attribute vec2 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "   vTextureCoord = aTextureCoord;\n" +
                    "   gl_Position = aPosition;\n" +
                    "}\n";

    protected static final String DEFAULT_FRAGMENT_SHADER =
            "precision highp float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform sampler2D uTexture;\n" +
                    "void main() {\n" +
                    "   gl_FragColor = texture2D(uTexture, vTextureCoord);\n" +
                    "}\n";

    protected static final FloatBuffer DEFAULT_VERTEX_COORDINATE_BUFFER =
            GlUtil.createFloatBuffer(AAVCoordinateUtil.DEFAULT_VERTEX_COORDS);

    protected static final FloatBuffer DEFAULT_TEXTURE_COORDINATE_BUFFER =
            GlUtil.createFloatBuffer(AAVCoordinateUtil.DEFAULT_TEXTURE_COORDS);

    protected int mProgramHandle = -1;

    private int mAPositionLoc;
    private int mATextureCoordinateLoc;
    private int mUTextureLoc;
    private int mTextureTarget = GLES20.GL_TEXTURE_2D;
    private String mVertexShader;
    private String mFragmentShader;
    private boolean mIsInit = false;

    protected AAVBaseRender() {
        this(DEFAULT_VERTEX_SHADER, DEFAULT_FRAGMENT_SHADER, GLES20.GL_TEXTURE_2D);
    }

    protected AAVBaseRender(String vertexShader, String fragmentShader, int textureTarget) {
        mTextureTarget = textureTarget;
        mVertexShader = vertexShader;
        mFragmentShader = fragmentShader;
    }

    /**
     * 绘制
     * @param textureId
     * @param vertexCoordinateBuf
     * @param textureCoordinateBuf
     */
    protected void drawFrame(int textureId, FloatBuffer vertexCoordinateBuf, FloatBuffer textureCoordinateBuf) {
        if (!mIsInit) {
            // 放在这里初始化是为了保证初始化在 GL 线程调用，否则 init 里 load shader 创建 program 会失败
            mIsInit = init(mVertexShader, mFragmentShader);
        }
        GLES20.glUseProgram(mProgramHandle);
        bindTexture(textureId);
        GlUtil.checkGlError("drawFrame()--->>>glBindTexture");

        GLES20.glEnableVertexAttribArray(mAPositionLoc);
        GLES20.glVertexAttribPointer(mAPositionLoc, 2, GLES20.GL_FLOAT, false, 8, vertexCoordinateBuf);

        GLES20.glEnableVertexAttribArray(mATextureCoordinateLoc);
        GLES20.glVertexAttribPointer(mATextureCoordinateLoc, 2, GLES20.GL_FLOAT, false, 8, textureCoordinateBuf);

        preDraw();

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GlUtil.checkGlError("drawFrame()--->>>glDrawArrays");

        GLES20.glDisableVertexAttribArray(mAPositionLoc);
        GLES20.glDisableVertexAttribArray(mATextureCoordinateLoc);

        unbindTexture();
        GLES20.glUseProgram(0);
    }

    /**
     * 初始化结束时调用，子类的额外初始化集成该函数实现
     * @return
     */
    protected boolean onInitialized() {
        return true;
    }

    /**
     * 绘制之前的操作
     */
    protected void preDraw() {}

    private boolean init(String vertexShader, String fragmentShader) {
        if (mProgramHandle == -1) {
            mProgramHandle = GlUtil.createProgram(vertexShader, fragmentShader);
            if (mProgramHandle <= 0) {
                return false;
            }

            mAPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "aPosition");
            mATextureCoordinateLoc = GLES20.glGetAttribLocation(mProgramHandle, "aTextureCoord");
            mUTextureLoc = GLES20.glGetUniformLocation(mProgramHandle, "uTexture");
        }

        return onInitialized();
    }

    /**
     * 绑定 texture
     * @param textureId
     */
    private void bindTexture(int textureId) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GlUtil.checkGlError("bindTexture(S)--->>>glActiveTexture");
        GLES20.glBindTexture(mTextureTarget, textureId);
        GlUtil.checkGlError("bindTexture(S)--->>>glActiveTexture");
        GLES20.glUniform1i(mUTextureLoc, 0);
    }

    /**
     * 解绑 Texture
     */
    private void unbindTexture() {
        GLES20.glBindTexture(mTextureTarget, 0);
    }

    /**
     * 释放资源
     */
    public void release() {
        try {
            this.mIsInit = false;
            if (this.mProgramHandle != -1) {
                GLES20.glDeleteProgram(this.mProgramHandle);
                this.mProgramHandle = -1;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
