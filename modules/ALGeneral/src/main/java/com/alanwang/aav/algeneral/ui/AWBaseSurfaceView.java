/*
 * Copyright (c) 2019-present AlanWang4523 <alanwang4523@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alanwang.aav.algeneral.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Author: AlanWang4523.
 * Date: 19/3/24 20:22.
 * Mail: alanwang4523@gmail.com
 */
public abstract class AWBaseSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    protected static int FRAME_INTERVAL_TIME = 30;
    protected Thread workThread;
    protected boolean isRun = true;
    protected SurfaceHolder surfaceHolder;
    protected Canvas canvas;

    public AWBaseSurfaceView(Context context) {
        this(context, null);
    }

    public AWBaseSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AWBaseSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    protected void initView(AttributeSet attrs){
        setKeepScreenOn(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        isRun = true;
        workThread = new Thread(this);
        workThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        isRun = false;
    }

    @Override
    public void run() {
        while(isRun){
            long startTime = System.currentTimeMillis();
            handleLogic();
            myDraw();
            long endTime = System.currentTimeMillis();
            try {
                if(endTime - startTime < FRAME_INTERVAL_TIME){
                    Thread.sleep(FRAME_INTERVAL_TIME - (endTime - startTime));
                }
            } catch (Exception ex) {
            }
        }
    }

    private void myDraw(){
        try {
            canvas = surfaceHolder.lockCanvas();
            if(canvas != null) {
                justToDraw(canvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(canvas != null){
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    /**
     * 具体的绘制
     * @param canvas
     */
    protected abstract void justToDraw(Canvas canvas);

    /**
     * 计算、处理逻辑
     */
    protected abstract void handleLogic();
}
