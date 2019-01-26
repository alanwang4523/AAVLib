package com.alanwang.aavlib.libvideo.surface;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Author: AlanWang4523.
 * Date: 19/1/26 22:56.
 * Mail: alanwang4523@gmail.com
 */

public class AAVSurfaceView extends SurfaceView implements SurfaceHolder.Callback2 {

    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private SurfaceHolder mSurfaceHolder;
    private ISurfaceCallback mSurfaceCallback;

    public AAVSurfaceView(Context context) {
        this(context, null);
    }

    public AAVSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AAVSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
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
            if (mSurfaceHolder != null) {
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
            if (mSurfaceCallback != null) {
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
            mSurfaceCallback = null;
        }
    }


    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {

    }
}
