package com.alanwang.aavlib.libvideo.surface;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Author: AlanWang4523.
 * Date: 19/1/26 22:56.
 * Mail: alanwang4523@gmail.com
 */

public class AWSurfaceView extends SurfaceView implements SurfaceHolder.Callback2 {

    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private SurfaceHolder mSurfaceHolder;
    private ISurfaceCallback mSurfaceCallback;

    public AWSurfaceView(Context context) {
        this(context, null);
    }

    public AWSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AWSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
//        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
    }

    /**
     * 设置回调接口
     * @param surfaceCallback
     */
    public void setSurfaceCallback(ISurfaceCallback surfaceCallback) {
        synchronized (this) {
            if (surfaceCallback == null) {
                mSurfaceCallback = null;
                return;
            }
            mSurfaceCallback = surfaceCallback;
            if (mSurfaceHolder != null
                    && mSurfaceHolder.getSurface() != null
                    && mSurfaceHolder.getSurface().isValid()) {
                surfaceCallback.onSurfaceChanged(mSurfaceHolder.getSurface(), mSurfaceWidth, mSurfaceHeight);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // do nothing
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        synchronized (this) {
            mSurfaceWidth = width;
            mSurfaceHeight = height;
            mSurfaceHolder = holder;
            if (mSurfaceCallback != null
                    && mSurfaceHolder.getSurface() != null
                    && mSurfaceHolder.getSurface().isValid()) {
                mSurfaceCallback.onSurfaceChanged(mSurfaceHolder.getSurface(), width, height);
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized (this) {
            if (mSurfaceCallback != null) {
                mSurfaceCallback.onSurfaceDestroyed(mSurfaceHolder.getSurface());
            }
            mSurfaceHolder = null;
        }
    }


    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {

    }
}
