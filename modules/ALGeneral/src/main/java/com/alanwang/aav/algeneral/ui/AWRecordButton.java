package com.alanwang.aav.algeneral.ui;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.alanwang.aav.algeneral.R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author: AlanWang4523.
 * Date: 19/3/21 00:25.
 * Mail: alanwang4523@gmail.com
 */
public class AWRecordButton extends View {
    private final static String TAG = AWRecordButton.class.getSimpleName();

    public interface OnRecordListener {
        void onRecordStart();
        void onRecordStop();
    }

    public interface OnTakePictureListener {
        void onTakePicture();
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({Mode.RECORD, Mode.TAKE_PHOTO})
    public @interface Mode {
        int RECORD = 0;
        int TAKE_PHOTO = 1;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({Status.INIT, Status.IDLE, Status.RECORDING})
    public @interface Status {
        int INIT = 0;
        int IDLE = 1;
        int RECORDING = 2;
    }

    // 拍照按钮相关参数
    private final static long TP_ANIMATION_DURATION = 300L;
    private final static float TP_CENTER_MIN_RADIUS_PERCENTAGE = 0.56f;
    private final static float TP_CENTER_MAX_RADIUS_PERCENTAGE = 0.78f;
    private final static float TP_RING_THICKNESS_PERCENTAGE = 0.068f;
    private final static float TP_CENTER_AND_RING_MARGIN_PERCENTAGE = 0.03f;

    private final int TP_CENTER_COLOR = getColor(R.color.lib_general_white_alpha_ff);
    private final int TP_RING_COLOR = getColor(R.color.lib_general_white_ff_55);

    // 录制按钮相关参数
    private final static long RC_CENTER_AREA_ANIMATION_DURATION = 500L;// 中间方形的动画时长
    private final static long RC_RING_REPEAT_ANIMATION_DURATION = 1000L;// 环形呼吸动画时长
    // 中间方形参数
    private final static float RC_CENTER_AREA_MIN_CORNER_RADIUS_PERCENTAGE = 0.08f;// 中间方形圆角的最小百分比
    private final static float RC_CENTER_AREA_MIN_RADIUS_PERCENTAGE = 0.28f;// 中间矩形的最小宽度百分比
    private final static float RC_CENTER_AREA_MAX_RADIUS_PERCENTAGE = 0.56f;// 中间矩形的最大宽度百分比
    // 外围环形参数
    private final static float RC_RING_MIN_BREATHE_RADIUS_PERCENTAGE = 0.7f;// 环形最小呼吸闪动半径百分比
    private final static float RC_RING_MAX_BREATHE_RADIUS_PERCENTAGE = 0.9f;// 环形最大呼吸闪动半径百分比
    private final static float RC_RING_MIN_THICKNESS_PERCENTAGE = 0.06f;// 环形最小厚度百分比
    private final static float RC_RING_MAX_THICKNESS_PERCENTAGE = 0.136f;// 环形最大厚度百分比（即内外半径差）

    private final int RC_CENTER_AREA_COLOR = getColor(R.color.lib_general_white_alpha_ff);// 中间方形按钮颜色
    private final int RC_RING_STATIC_COLOR = getColor(R.color.lib_general_white_alpha_ff);// 环形静止时的颜色
    private final int RC_RING_BREATHE_COLOR = getColor(R.color.lib_general_white_ff_55);// 环形做呼吸闪动时的颜色

    private float tpCenterCircleRadius = 0f;
    private float tpRingThicknessWithMarginToCenter = 0f;
    private ValueAnimator tpRecordAnimation = null;
    private RectF tpRingRectF = new RectF();

    private float rcCenterAreaRadius = 0f;
    private float rcCenterAreaCornerRadius = 0f;
    private Paint rcCenterPaint = new Paint();
    private RectF rcCenterRectF = new RectF();
    private ValueAnimator rcCenterAnimation = null;

    // 环的包含中间空隙的宽度（即环宽度 + 空隙宽度，也即环最外圆半径 - 中心圆半径）
    private float rcRingThicknessWithMarginToCenter = 0f;
    private float rcRingThickness = 0f;// 环的厚度，不包括空隙
    private Paint rcRingPaint = new Paint();
    private RectF rcRingRectF = new RectF();
    private ValueAnimator rcRingAnimation = null;

    private float wholeBtnEffectiveRadius = 0.0f;

    private boolean isHandlingClickEvent = false;
    private @Mode int curBtnMode = Mode.RECORD;
    private @Status int curBtnStatus = Status.INIT;
    private OnRecordListener onRecordListener;
    private OnTakePictureListener onTakePictureListener;


    public AWRecordButton(Context context) {
        super(context);
        init();
    }

    public AWRecordButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AWRecordButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        rcCenterPaint.setFilterBitmap(true);
        rcCenterPaint.setDither(true);
        rcCenterPaint.setAntiAlias(true);
        rcCenterPaint.setColor(RC_CENTER_AREA_COLOR);
        rcCenterPaint.setStyle(Paint.Style.FILL);

        rcRingPaint.setFilterBitmap(true);
        rcRingPaint.setDither(true);
        rcRingPaint.setAntiAlias(true);
        rcRingPaint.setColor(RC_RING_STATIC_COLOR);
        rcRingPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (curBtnMode) {
            case Mode.RECORD:
                drawRCCenter(canvas);
                drawRCRing(canvas);
                break;
            case Mode.TAKE_PHOTO:
                drawTPCenter(canvas);
                drawTPRing(canvas);
                break;
            default:
                break;
        }
    }

    /**
     * 绘制拍照模式下的中心区域
     * @param canvas
     */
    private void drawTPCenter(Canvas canvas) {
        rcCenterPaint.setColor(TP_CENTER_COLOR);
        canvas.drawCircle(getEffectiveRadius(), getEffectiveRadius(), tpCenterCircleRadius, rcCenterPaint);
    }

    /**
     * 绘制拍照模式下的环形
     * @param canvas
     */
    private void drawTPRing(Canvas canvas) {
        rcRingPaint.setStrokeWidth(TP_RING_THICKNESS_PERCENTAGE * getEffectiveRadius());
        rcRingPaint.setColor(TP_RING_COLOR);
        canvas.drawArc(tpRingRectF, -90f, 360f, false, rcRingPaint);
    }

    /**
     * 绘制录制模式下的中心区域
     * @param canvas
     */
    private void drawRCCenter(Canvas canvas) {
        rcCenterPaint.setColor(RC_CENTER_AREA_COLOR);
        canvas.drawRoundRect(rcCenterRectF, rcCenterAreaCornerRadius, rcCenterAreaCornerRadius, rcCenterPaint);
    }

    /**
     * 绘制录制模式下的环形
     * @param canvas
     */
    private void drawRCRing(Canvas canvas) {
        rcRingPaint.setStrokeWidth(rcRingThickness);
        if (curBtnStatus == Status.IDLE) {
            if (rcRingThickness == RC_RING_MIN_THICKNESS_PERCENTAGE * getWidth() / 2.0f) {
                rcRingPaint.setColor(RC_RING_STATIC_COLOR);
            } else {
                rcRingPaint.setColor(RC_RING_BREATHE_COLOR);
            }
        } else if (curBtnStatus == Status.RECORDING) {
            rcRingPaint.setColor(RC_RING_BREATHE_COLOR);
        }
        canvas.drawArc(rcRingRectF, -90f, 360f, false, rcRingPaint);
    }

    /**
     * 获取有效的最大半径
     */
    private float getEffectiveRadius() {
        return wholeBtnEffectiveRadius;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            handlerEventActionUpByState();
        }
        return true;
    }

    //当手指松开按钮时候处理的逻辑
    private void handlerEventActionUpByState() {
        if (!isHandlingClickEvent) {
            if (curBtnMode == Mode.RECORD && onRecordListener != null) {
                if (curBtnStatus == Status.IDLE) {
                    onRecordListener.onRecordStart();
                    setRecordStatus(Status.RECORDING);
                } else if (curBtnStatus == Status.RECORDING) {
                    onRecordListener.onRecordStop();
                    setRecordStatus(Status.IDLE);
                }
            } else if (curBtnMode == Mode.TAKE_PHOTO && onTakePictureListener != null) {
                onTakePictureListener.onTakePicture();
                setRecordStatus(Status.RECORDING);
            }

            isHandlingClickEvent = true;
            postDelayed(resetHandlingEventStatusRunnable, 500L);
        }
    }

    /**
     * 设置当前模式
     */
    public void setMode(@Mode int mode) {
        if (this.curBtnMode == mode) {
            return;
        }
        this.curBtnMode = mode;
        setRecordStatus(Status.IDLE, true);
    }

    public void setRecordListener(OnRecordListener onRecordListener) {
        this.onRecordListener = onRecordListener;
    }

    public void setTakePictureListener(OnTakePictureListener onTakePictureListener) {
        this.onTakePictureListener = onTakePictureListener;
    }

    private void setRecordStatus(@Status int status) {
        setRecordStatus(status, false);
    }

    /**
     * 设置当前状态
     */
    private void setRecordStatus(@Status int status, boolean isForce) {
        if (this.curBtnStatus == status && !isForce) {
            return;
        }
        if (curBtnMode == Mode.RECORD) {
            resetRecordModeParams(status);
        } else if (curBtnMode == Mode.TAKE_PHOTO) {
            resetTPParams(status);
        }
        this.curBtnStatus = status;
    }

    private void resetRecordModeParamsWhenStatusIdle() {
        cancelAllAnimations();
        rcCenterAreaRadius = RC_CENTER_AREA_MAX_RADIUS_PERCENTAGE * getEffectiveRadius();
        rcCenterAreaCornerRadius = rcCenterAreaRadius;
        rcRingThicknessWithMarginToCenter = RC_RING_MIN_BREATHE_RADIUS_PERCENTAGE * getEffectiveRadius() - rcCenterAreaRadius;
        rcRingThickness = RC_RING_MIN_THICKNESS_PERCENTAGE * getEffectiveRadius();
        resetRCRingRect();
        resetRCCenterRect();
        invalidate();
    }

    private void resetRecordModeParamsWhenStatusRecording() {
        cancelAllAnimations();
        float targetCenterAreaRadius = RC_CENTER_AREA_MIN_RADIUS_PERCENTAGE * getEffectiveRadius();
        float curCenterAreaRadius = rcCenterAreaRadius;
        float targetCenterAreaCornerRadius = RC_CENTER_AREA_MIN_CORNER_RADIUS_PERCENTAGE * getEffectiveRadius();
        float curCenterAreaCornerRadius = rcCenterAreaCornerRadius;

        float curRingThicknessWithMarginToCenter = rcRingThicknessWithMarginToCenter;
        float minRingThicknessWithMarginToCenter = (RC_RING_MIN_BREATHE_RADIUS_PERCENTAGE - RC_CENTER_AREA_MAX_RADIUS_PERCENTAGE) * getEffectiveRadius();

        float curRingThickness = rcRingThickness;
        float minRingThickness = RC_RING_MIN_THICKNESS_PERCENTAGE * getEffectiveRadius();

        PropertyValuesHolder centerAreaRadiusValuesHolder = PropertyValuesHolder.ofFloat("centerAreaRadius", curCenterAreaRadius, targetCenterAreaRadius);
        PropertyValuesHolder centerAreaCornerRadiusValuesHolder = PropertyValuesHolder.ofFloat("centerAreaCornerRadius", curCenterAreaCornerRadius, targetCenterAreaCornerRadius);
        PropertyValuesHolder ringThicknessWithMarginToCenterValuesHolder = PropertyValuesHolder.ofFloat("ringThicknessWithMarginToCenter", curRingThicknessWithMarginToCenter, minRingThicknessWithMarginToCenter);
        PropertyValuesHolder ringThicknessValuesHolder = PropertyValuesHolder.ofFloat("ringThickness", curRingThickness, minRingThickness);
        rcCenterAnimation = ValueAnimator.ofPropertyValuesHolder(
                centerAreaRadiusValuesHolder, centerAreaCornerRadiusValuesHolder,
                ringThicknessWithMarginToCenterValuesHolder, ringThicknessValuesHolder)
                .setDuration(RC_CENTER_AREA_ANIMATION_DURATION);
        rcCenterAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                rcCenterAreaRadius = (Float) animation.getAnimatedValue("centerAreaRadius");
                rcCenterAreaCornerRadius = (Float) animation.getAnimatedValue("centerAreaCornerRadius");
                rcRingThicknessWithMarginToCenter = (Float) animation.getAnimatedValue("ringThicknessWithMarginToCenter");
                rcRingThickness = (Float) animation.getAnimatedValue("ringThickness");
                resetRCRingRect();
                resetRCCenterRect();
                invalidate();
            }
        });

        final float tempMinRingThicknessWithMarginToCenter = minRingThicknessWithMarginToCenter;
        final float tempTargetRingThicknessWithMarginToCenter = (RC_RING_MAX_BREATHE_RADIUS_PERCENTAGE - RC_CENTER_AREA_MAX_RADIUS_PERCENTAGE) * getEffectiveRadius();
        final float tempMinRingThickness = minRingThickness;
        final float tempTargetRingThickness = RC_RING_MAX_THICKNESS_PERCENTAGE * getEffectiveRadius();

        rcCenterAnimation.addListener(new AbsAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //如果是录制状态，需要在变换动画结束后开始呼吸动画
                PropertyValuesHolder repeatRingThicknessWithMarginToCenterValuesHolder = PropertyValuesHolder.ofFloat("repeatRingThicknessWithMarginToCenter", tempMinRingThicknessWithMarginToCenter, tempTargetRingThicknessWithMarginToCenter);
                PropertyValuesHolder repeatRingThicknessValuesHolder = PropertyValuesHolder.ofFloat("repeatRingThickness", tempMinRingThickness, tempTargetRingThickness);
                rcRingAnimation = ValueAnimator.ofPropertyValuesHolder(repeatRingThicknessWithMarginToCenterValuesHolder, repeatRingThicknessValuesHolder)
                        .setDuration(RC_RING_REPEAT_ANIMATION_DURATION);
                rcRingAnimation.setRepeatCount(ValueAnimator.INFINITE);
                rcRingAnimation.setRepeatMode(ValueAnimator.REVERSE);
                rcRingAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        rcRingThicknessWithMarginToCenter = (Float) animation.getAnimatedValue("repeatRingThicknessWithMarginToCenter");
                        rcRingThickness = (Float) animation.getAnimatedValue("repeatRingThickness");
                        resetRCRingRect();
                        invalidate();
                    }
                });
                rcRingAnimation.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });

        rcCenterAnimation.start();
    }

    /**
     * 初始化录制模式参数
     */
    private void resetRecordModeParams(@Status final int status) {
        if (status == Status.IDLE) {
            resetRecordModeParamsWhenStatusIdle();
        } else if (status == Status.RECORDING) {
            resetRecordModeParamsWhenStatusRecording();
        }
    }

    private void resetRCRingRect() {
        rcRingRectF.set(getPaddingStart() + getWidth() / 2 - getEffectiveRadius() * RC_CENTER_AREA_MAX_RADIUS_PERCENTAGE
                - rcRingThicknessWithMarginToCenter + rcRingThickness, getPaddingTop() + getHeight() / 2
                - getEffectiveRadius() * RC_CENTER_AREA_MAX_RADIUS_PERCENTAGE - rcRingThicknessWithMarginToCenter + rcRingThickness,
                getWidth() / 2 - getPaddingEnd() + getEffectiveRadius() * RC_CENTER_AREA_MAX_RADIUS_PERCENTAGE + rcRingThicknessWithMarginToCenter - rcRingThickness,
                getHeight() / 2 - getPaddingBottom() + getEffectiveRadius() * RC_CENTER_AREA_MAX_RADIUS_PERCENTAGE + rcRingThicknessWithMarginToCenter - rcRingThickness);
    }

    private void resetRCCenterRect() {
        rcCenterRectF.set(getPaddingStart() + getWidth() / 2 - rcCenterAreaRadius,
                getPaddingTop() + getHeight() / 2 - rcCenterAreaRadius,
                getWidth() / 2 - getPaddingEnd() + rcCenterAreaRadius,
                getHeight() / 2 - getPaddingBottom() + rcCenterAreaRadius);
    }


    /**
     * 初始化 TakePhoto 模式下的参数
     */
    private void resetTPParams(@Status int status) {
        if (status == Status.RECORDING) {
            //拍照状态下的recording其实就是闪一下然后就需要把状态变为ready
            if (tpRecordAnimation != null) {
                tpRecordAnimation.cancel();
                tpRecordAnimation = null;
            }

            float maxCenterRadius = TP_CENTER_MAX_RADIUS_PERCENTAGE * getEffectiveRadius();
            float minCenterRadius = TP_CENTER_MIN_RADIUS_PERCENTAGE * getEffectiveRadius();

            float maxRingThicknessWithMarginToCenter = (TP_CENTER_AND_RING_MARGIN_PERCENTAGE + TP_RING_THICKNESS_PERCENTAGE) * getEffectiveRadius();
            float minRingThicknessWithMarginToCenter = maxRingThicknessWithMarginToCenter;
            PropertyValuesHolder centerRadiusValuesHolder = PropertyValuesHolder.ofFloat("centerRadius", minCenterRadius, maxCenterRadius, minCenterRadius);
            PropertyValuesHolder ringRadiusWidthMarginToCenterValuesHolder = PropertyValuesHolder.ofFloat("ringRadiusWidthMarginToCenter", minRingThicknessWithMarginToCenter, maxRingThicknessWithMarginToCenter, minRingThicknessWithMarginToCenter);
            tpRecordAnimation = ValueAnimator.ofPropertyValuesHolder(centerRadiusValuesHolder, ringRadiusWidthMarginToCenterValuesHolder)
                    .setDuration(TP_ANIMATION_DURATION);
            tpRecordAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    tpCenterCircleRadius = (Float) animation.getAnimatedValue("centerRadius");
                    tpRingThicknessWithMarginToCenter = (Float) animation.getAnimatedValue("ringRadiusWidthMarginToCenter");
                    resetTPRingRect();
                    invalidate();
                }
            });
            tpRecordAnimation.addListener(new AbsAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    setRecordStatus(Status.IDLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    setRecordStatus(Status.IDLE);
                }
            });
            tpRecordAnimation.start();

        } else {
            //除了recording状态，其它状态对于photo都是一样的
            tpCenterCircleRadius = TP_CENTER_MIN_RADIUS_PERCENTAGE * getEffectiveRadius();
            tpRingThicknessWithMarginToCenter = (TP_CENTER_AND_RING_MARGIN_PERCENTAGE + TP_RING_THICKNESS_PERCENTAGE) * getEffectiveRadius();
            resetTPRingRect();
            invalidate();
        }
    }

    private void resetTPRingRect() {
        tpRingRectF.set(getPaddingStart() + getWidth() / 2 - tpCenterCircleRadius - tpRingThicknessWithMarginToCenter,
                getPaddingTop() + getHeight() / 2 - tpCenterCircleRadius - tpRingThicknessWithMarginToCenter,
                getWidth() / 2 - getPaddingEnd() + tpCenterCircleRadius + tpRingThicknessWithMarginToCenter,
                getHeight() / 2 - getPaddingBottom() + tpCenterCircleRadius + tpRingThicknessWithMarginToCenter);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        wholeBtnEffectiveRadius = (getWidth() - getPaddingStart() - getPaddingEnd()) / 2.0f;
        setRecordStatus(Status.IDLE, true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAllAnimations();
        removeCallbacks(resetHandlingEventStatusRunnable);
        isHandlingClickEvent = false;
    }

    /**
     * 取消所有动画
     */
    private void cancelAllAnimations() {
        if (tpRecordAnimation != null) {
            tpRecordAnimation.cancel();
            tpRecordAnimation = null;
        }
        if (rcCenterAnimation != null) {
            rcCenterAnimation.cancel();
            rcCenterAnimation = null;
        }
        if (rcRingAnimation != null) {
            rcRingAnimation.cancel();
            rcRingAnimation = null;
        }
    }

    private int getColor(@ColorRes int resId) {
        return this.getContext().getResources().getColor(resId);
    }

    private Runnable resetHandlingEventStatusRunnable = new Runnable() {
        @Override
        public void run() {
            isHandlingClickEvent = false;
        }
    };

    private abstract class AbsAnimatorListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
