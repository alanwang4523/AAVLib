package com.alanwang.aav.algeneral.ui.render;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.SparseArray;
import com.alanwang.aav.algeneral.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: AlanWang4523.
 * Date: 19/3/24 22:36.
 * Mail: alanwang4523@gmail.com
 */
public class AWAudioWaveRender {

    private static final int MAX_VOLUME = 100;
    private static final int MIN_VOLUME = 0;
    private static final int MAX_SENSITIVITY = 20;
    private static final int MIN_SENSITIVITY = 1;

    private static final int DEFAULT_SAMPLING_SIZE = 48;
    private static final float DEFAULT_OFFSET_SPEED =  180F;
    private static final int DEFAULT_SENSIBILITY =  15;
    private static final String DEFAULT_LINE_COLOR = "#2ED184";

    //采样点的数量，越高越精细，但是高于一定限度肉眼很难分辨，越高绘制效率越低
    private int sampleCount;
    //控制向右偏移速度，越小偏移速度越快
    private float moveSpeed;
    //平滑改变的音量值
    private float volume = 0;
    //用户设置的音量，[0,100]
    private int targetVolume = 0;
    //每次平滑改变的音量单元
    private float perVolume;
    //灵敏度，越大越灵敏[1,20]
    private int sensitivity;
    //背景色
    private int backGroundColor = Color.WHITE;
    //波浪线颜色
    private int lineColor;
    //粗线宽度
    private int thickLineWidth;
    //细线宽度
    private int fineLineWidth;
    private int[] shaderArray;
    private final Paint paint = new Paint();
    private LinearGradient shader;
    private List<Path> paths = new ArrayList<>();


    //不同函数曲线系数
    private float[] pathFuncs = {
            0.6f, 0.35f, 0.1f, -0.1f
    };

    //采样点X坐标
    private float[] sampleX;
    //采样点位置映射到[-2,2]之间
    private float[] mapX;
    //画布宽高
    private int width,height;
    //画布中心的高度
    private int centerHeight;
    //振幅
    private float amplitude;
    //存储衰变系数
    private SparseArray<Double> recessionFuncs = new SparseArray<>();
    //连线动画结束标记
    private boolean isPrepareLineAnimEnd = false;
    //连线动画位移
    private int lineAnimX = 0;
    //渐入动画百分比值[0,1f]
    private float prepareAlpha = 0f;
    //是否开启准备动画
    private boolean isOpenPrepareAnim = false;

    public AWAudioWaveRender() {
        paint.setDither(true);
        paint.setAntiAlias(true);
        lineAnimX = 0;
        prepareAlpha = 0f;
        isPrepareLineAnimEnd = false;
        sampleX = null;
        for (int i = 0; i < 4; i++) {
            paths.add(new Path());
        }
    }

    public void initAttr(Context context, AttributeSet attrs) {
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.AudioWaveView);
        backGroundColor = t.getColor(R.styleable.AudioWaveView_awvBackgroundColor, Color.WHITE);
        sampleCount = t.getInt(R.styleable.AudioWaveView_awvSampleCount, DEFAULT_SAMPLING_SIZE);
        lineColor = t.getColor(R.styleable.AudioWaveView_awvLineColor, Color.parseColor(DEFAULT_LINE_COLOR));
        thickLineWidth = (int)t.getDimension(R.styleable.AudioWaveView_awvThickLineWidth, 6);
        fineLineWidth = (int)t.getDimension(R.styleable.AudioWaveView_awvThinLineWidth, 2);
        moveSpeed = t.getFloat(R.styleable.AudioWaveView_awvMoveSpeed, DEFAULT_OFFSET_SPEED);
        sensitivity = t.getInt(R.styleable.AudioWaveView_awvSensitivity, DEFAULT_SENSIBILITY);
        int shaderArrayResId = t.getResourceId(R.styleable.AudioWaveView_awvLineColorShader, 0);
        if (shaderArrayResId > 0) {
            shaderArray = context.getResources().getIntArray(shaderArrayResId);
            if (isLayoutRtl()) {
                reverseNumArray(shaderArray);
            }
        }
        t.recycle();
        checkVolumeValue();
        checkSensitivityValue();
    }

    public void doDrawBackground(Canvas canvas, boolean needClear) {
        //绘制背景
        if (needClear) {
            //启用CLEAR模式，所绘制内容不会提交到画布上。
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            canvas.drawColor(backGroundColor);
        }else {
            canvas.drawColor(backGroundColor);
        }
    }

    public void onRender(Canvas canvas, long millisPassed){
        onRender(canvas, millisPassed, -1);
    }

    public void onRender(Canvas canvas, long millisPassed, int randomVolume) {
        float offset = millisPassed / moveSpeed;

        if (sampleX == null || mapX == null || pathFuncs == null){
            initDraw(canvas);
        }

        if (lineAnim(canvas)) {
            resetPaths();
            if (randomVolume >= 0) {
                setVolume(randomVolume);
                volume = targetVolume;
            } else {
                softerChangeVolume();
            }
            //波形函数的值
            float curY;
            for (int i = 0; i <= sampleCount; i++) {
                float x = sampleX[i];
                curY = (float) (amplitude * calcValue(mapX[i], offset));
                for (int n = 0; n < paths.size(); n++) {
                    //四条线分别乘以不同的函数系数
                    float realY = curY * pathFuncs[n] * volume * 0.01f;
                    if (isLayoutRtl()) {
                        paths.get(n).lineTo(getRealPosition(x), centerHeight + realY);
                    } else {
                        paths.get(n).lineTo(x, centerHeight + realY);
                    }
                }
            }

            //连线至终点
            for (int i = 0; i < paths.size(); i++) {
                paths.get(i).moveTo(width, centerHeight);
            }

            //绘制曲线
            for (int n = 0; n < paths.size(); n++) {

                if (n == 0) {
                    paint.setStrokeWidth(thickLineWidth);
                    paint.setAlpha((int)(255 * alphaInAnim()));
                } else {
                    paint.setStrokeWidth(fineLineWidth);
                    paint.setAlpha((int)(100 * alphaInAnim()));
                }
                canvas.drawPath(paths.get(n), paint);
            }

        }

    }

    /**
     * 使曲线振幅有较大改变时动画过渡自然
     */
    private void softerChangeVolume(){
        //这里减去perVolume是为了防止volume频繁在targetVolume上下抖动
        if (volume < targetVolume - perVolume){
            volume += perVolume;
        }else if (volume > targetVolume + perVolume){
            if (volume < perVolume * 2){
                volume = perVolume * 2;
            }else {
                volume -= perVolume;
            }
        }else {
            volume = targetVolume;
        }

    }

    /**
     * 渐入动画
     * @return progress of animation
     */
    private float alphaInAnim() {
        if (!isOpenPrepareAnim)return 1;
        if (prepareAlpha < 1f){
            prepareAlpha += 0.02f;
        }else {
            prepareAlpha = 1;
        }
        return prepareAlpha;
    }

    /**
     * 连线动画
     * @param canvas
     * @return whether animation is end
     */
    private boolean lineAnim(Canvas canvas) {
        if (isPrepareLineAnimEnd || !isOpenPrepareAnim)return true;
        paths.get(0).moveTo(0, centerHeight);
        paths.get(1).moveTo(width, centerHeight);

        for (int i = 1; i <= sampleCount; i++) {
            float x = i * lineAnimX / sampleCount;
            paths.get(0).lineTo(x, centerHeight);
            paths.get(1).lineTo(width - x, centerHeight);

        }

        paths.get(0).moveTo(width/2, centerHeight);
        paths.get(1).moveTo(width/2, centerHeight);

        lineAnimX += width / 60;
        canvas.drawPath(paths.get(0), paint);
        canvas.drawPath(paths.get(1), paint);

        if (lineAnimX > width / 2){
            isPrepareLineAnimEnd = true;
            return true;
        }
        return false;
    }

    /**
     * 重置path
     */
    private void resetPaths(){
        for (int i = 0; i < paths.size(); i++) {
            paths.get(i).rewind();
            if (isLayoutRtl()) {
                paths.get(i).moveTo(width, centerHeight);
            } else {
                paths.get(i).moveTo(0, centerHeight);
            }
        }
    }

    //初始化绘制参数
    private void initDraw(Canvas canvas) {

        width = canvas.getWidth();
        height = canvas.getHeight();
        centerHeight = height >> 1;
        //振幅为高度的1/4
        amplitude = height / 3.0f;

        //适合View的理论最大音量值，和音量不属于同一概念
        perVolume = sensitivity;

        //初始化采样点及映射
        //这里因为包括起点和终点，所以需要+1
        sampleX = new float[sampleCount + 1];
        mapX = new float[sampleCount + 1];
        //确定采样点之间的间距
        float gap = width / (float) sampleCount;
        //采样点的位置
        float x;
        for (int i = 0; i <= sampleCount; i++){
            x = i * gap;
            sampleX[i] = x;
            //将采样点映射到[-2，2]
            mapX[i] = (x / (float)width) * 4 - 2;
        }

        paint.setStyle(Paint.Style.STROKE);
        if (shader != null) {
            paint.setShader(shader);
        } else {
            paint.setColor(lineColor);
        }
        paint.setStrokeWidth(thickLineWidth);

    }

    /**
     * 计算波形函数中x对应的y值
     *
     * 使用稀疏矩阵进行暂存计算好的衰减系数值，下次使用时直接查找，减少计算量
     * @param mapX   换算到[-2,2]之间的x值
     * @param offset 偏移量
     * @return [-1, 1]
     */
    private double calcValue(float mapX, float offset) {
        int keyX = (int) (mapX * 1000);
        offset %= 2;
        double sinFunc = Math.sin(Math.PI * mapX - offset * Math.PI);
        double recessionFunc;
        if(recessionFuncs.indexOfKey(keyX) >= 0 ){
            recessionFunc = recessionFuncs.get(keyX);
        }else {
            recessionFunc = 4 / (4 + Math.pow(mapX, 4));
            recessionFuncs.put(keyX,recessionFunc);
        }
        return sinFunc * recessionFunc ;
    }

    /**
     * 清空画布所有内容
     * @param canvas
     */
    public void clearDraw(Canvas canvas) {
        canvas.drawColor(backGroundColor);
        resetPaths();
        for (int i=0; i< paths.size(); i++){
            canvas.drawPath(paths.get(i),paint);
        }
    }
    /**
     * 清空画布所有内容,包括横线
     * @param canvas
     */
    public void cleanAll(Canvas canvas){
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    /**
     * 设置当前音量, [0,100]
     * @param volume
     */
    public void setVolume(int volume) {
        if (Math.abs(targetVolume - volume) > perVolume) {
            this.targetVolume = volume;
            checkVolumeValue();
        }
    }

    //检查音量是否合法
    private void checkVolumeValue(){
        if (targetVolume > MAX_VOLUME) targetVolume = MAX_VOLUME;
        if (targetVolume < MIN_VOLUME) targetVolume = MIN_VOLUME;
    }

    //检查灵敏度值是否合法
    private void checkSensitivityValue(){
        if (sensitivity > MAX_SENSITIVITY) sensitivity = MAX_SENSITIVITY;
        if (sensitivity < MIN_SENSITIVITY) sensitivity = MIN_SENSITIVITY;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public void setLocation(int x, int y, int w, int h) {
        if (shaderArray != null) {
            shader = new LinearGradient(x, y, x + w, y + h, shaderArray, null, Shader.TileMode.CLAMP);
        }
    }

    private float getRealPosition(float position) {
        if (isLayoutRtl()) {
            return width - position;
        } else {
            return position;
        }
    }

    private void reverseNumArray(int[] array) {
        int head = 0;
        int tail = array.length - 1;
        int length = array.length / 2;
        for (int i = 0; i < length; i++) {
            array[head] = array[head] ^ array[tail];
            array[tail] = array[tail] ^ array[head];
            array[head] = array[head] ^ array[tail];
            head++;
            tail--;
        }
    }
    
    private boolean isLayoutRtl() {
        return false;
    }

}
