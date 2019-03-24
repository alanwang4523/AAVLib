package com.alanwang.aav.algeneral.ui.render;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import com.alanwang.aav.algeneral.ui.AWBaseSurfaceView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Random;

/**
 * Author: AlanWang4523.
 * Date: 19/3/24 21:43.
 * Mail: alanwang4523@gmail.com
 */
public class AWAudioWaveView extends AWBaseSurfaceView {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({Status.INIT, Status.RANDOM_STATIC, Status.RUNNING, Status.STOP})
    @interface Status {
        int INIT = 0;
        int RANDOM_STATIC = 1;
        int RUNNING = 2;
        int STOP = 3;
    }

    private AWAudioWaveRender realRender;
    private long randomPassedTimeMs = 0L;
    private int randomVolume = 0;
    private long startTimeMs = 0;
    private @Status int curStatus = Status.INIT;

    public AWAudioWaveView(Context context) {
        super(context);
    }

    public AWAudioWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AWAudioWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        super.surfaceCreated(surfaceHolder);
        startTimeMs = System.currentTimeMillis();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        super.surfaceChanged(surfaceHolder, i, i1, i2);
        realRender.setLocation(Math.round(getX()), Math.round(getY()), i1, i2);
    }

    @Override
    protected void initView(AttributeSet attrs) {
        super.initView(attrs);
        setWillNotDraw(false);
        //将RenderView放到最顶层
        setZOrderOnTop(true);
        //使窗口支持透明度
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);

        realRender = new AWAudioWaveRender();
        realRender.initAttr(getContext(), attrs);
    }

    @Override
    protected void justToDraw(Canvas canvas) {
        realRender.doDrawBackground(canvas, true);

        if (curStatus == Status.INIT || curStatus == Status.RANDOM_STATIC) {
            realRender.onRender(canvas, randomPassedTimeMs, randomVolume);
        } else if (curStatus == Status.STOP) {
            realRender.onRender(canvas, 0, 0);
        }
        else {
            realRender.onRender(canvas, (System.currentTimeMillis() - startTimeMs));
        }
    }

    @Override
    protected void handleLogic() {

    }

    /**
     * 显示静态的随机波形
     */
    public void showStaticRandomWave() {
        setStatus(Status.RANDOM_STATIC);
    }

    /**
     * 开始动态波形
     */
    public void startWave() {
        setStatus(Status.RUNNING);
    }

    /**
     * 停止波形，包括动态和静态的
     */
    public void stopWave() {
        setStatus(Status.STOP);
    }

    private void setStatus(@Status int status) {
        this.curStatus = status;
        if (status == Status.INIT) {
            randomPassedTimeMs = 0;
            randomVolume = 0;
        } else if (status == Status.RANDOM_STATIC) {
            randomPassedTimeMs = new Random().nextInt(1000);
            randomVolume = new Random().nextInt(30) + 30;
        }
    }

    public void setVolume(int volume) {
        realRender.setVolume(volume);
    }
}
