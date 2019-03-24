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

    @IntDef({
            Mode.RECORD,
            Mode.TAKE_PHOTO
    })

    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
        int RECORD = 0;
        int TAKE_PHOTO = 1;
    }

    @IntDef({
            Status.INIT,
            Status.IDLE,
            Status.RECORDING
    })

    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
        int INIT = 0;
        int IDLE = 1;
        int RECORDING = 2;
    }

    // 拍照按钮相关参数
    private final static long TP_ANIMATION_DURATION = 300L;
    private final static float TP_BTN_MIN_SIZE_PERCENTAGE = 0.56f;
    private final static float TP_BTN_MAX_SIZE_PERCENTAGE = 0.78f;
    private final static float TP_RING_THICKNESS_PERCENTAGE = 0.12f;
    private final static float TP_CENTER_AND_RING_MAGIN_PERCENTAGE = 0.10f;

    private final int TP_CENTER_COLOR = getColor(R.color.lib_general_white_alpha_ff);
    private final int TP_RING_COLOR = getColor(R.color.lib_general_white_ff_55);

    // 录制按钮相关参数
    private final static long RC_CENTER_AREA_ANIMATION_DURATION = 500L;// 中间方形的动画时长
    private final static long RC_RING_REPEAT_ANIMATION_DURATION = 1000L;// 环形呼吸动画时长
    // 中间方形参数
    private final static float RC_CENTER_AREA_MIN_CORNER_RADIUS_PERCENTAGE = 0.04f;// 中间方形圆角的最小百分比
    private final static float RC_CENTER_AREA_MIN_WIDTH_PERCENTAGE = 0.28f;// 中间矩形的最小宽度百分比
    private final static float RC_CENTER_AREA_MAX_WIDTH_PERCENTAGE = 0.56f;// 中间矩形的最大宽度百分比
    // 外围环形参数
    private final static float RC_RING_MIN_BREATHE_DIAMETER_PERCENTAGE = 0.7f;// 环形最小呼吸闪动直径百分比
    private final static float RC_RING_MAX_BREATHE_DIAMETER_PERCENTAGE = 0.9f;// 环形最大呼吸闪动直径百分比
    private final static float RC_RING_MIN_THICKNESS_PERCENTAGE = 0.03f;// 环形最小厚度百分比
    private final static float RC_RING_MAX_THICKNESS_PERCENTAGE = 0.068f;// 环形最大厚度百分比（即内外半径差）

    private final int RC_CENTER_AREA_COLOR = getColor(R.color.lib_general_white_alpha_ff);// 中间方形按钮颜色
    private final int RC_RING_STATIC_COLOR = getColor(R.color.lib_general_white_alpha_ff);// 环形静止时的颜色
    private final int RC_RING_BREATHE_COLOR = getColor(R.color.lib_general_white_ff_55);// 环形做呼吸闪动时的颜色

    private float tpCenterCircleRadius = 0f;
    private float tpRingRadiusWidth = 0f;
    private ValueAnimator tpRecordAnimation = null;
    private RectF tpRingRectF = new RectF();

    private float rcCenterAreaWidth = 0f;
    private float rcCenterAreaRadius = 0f;
    private Paint rcCenterPaint = new Paint();
    private RectF rcCenterRectF = new RectF();
    private ValueAnimator rcCenterAnimation = null;

    private float rcRingRadiusWidth = 0f;
    private float rcRingLineWidth = 0f;
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
        canvas.drawRoundRect(rcCenterRectF, rcCenterAreaRadius, rcCenterAreaRadius, rcCenterPaint);
    }

    /**
     * 绘制录制模式下的环形
     * @param canvas
     */
    private void drawRCRing(Canvas canvas) {
        rcRingPaint.setStrokeWidth(rcRingLineWidth);
        if (curBtnStatus == Status.IDLE) {
            if (rcRingLineWidth == RC_RING_MIN_THICKNESS_PERCENTAGE * getWidth()) {
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
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //do nothing
                break;
            case MotionEvent.ACTION_MOVE:
                //do nothing
                break;
            case MotionEvent.ACTION_UP:
                handlerEventActionUpByState();
                break;
            default:
                break;
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
            initRecordModeParams(status);
        } else if (curBtnMode == Mode.TAKE_PHOTO) {
            initTPParams(status);
        }
        this.curBtnStatus = status;
    }

    /**
     * 初始化录制模式参数
     */
    private void initRecordModeParams(@Status final int status) {
        if (rcCenterAnimation != null) {
            rcCenterAnimation.cancel();
            rcCenterAnimation = null;
        }
        if (rcRingAnimation != null) {
            rcRingAnimation.cancel();
            rcRingAnimation = null;
        }
        float targetButtonWidth = 0f;
        float nowButtonWidth = rcCenterAreaWidth;
        float targetButtonRadius = 0f;
        float nowButtonRadius = rcCenterAreaRadius;
        float targetRingWidthRadius = 0f;
        float nowRingWidthRadius = rcRingRadiusWidth;
        float minRingWidthRadius = 0f;
        float targetRingLineWidth = 0f;
        float nowRingLineWidth = rcRingLineWidth;
        float minRingLineWidth = 0f;
        switch (status) {
            case Status.IDLE: {
                targetButtonWidth = RC_CENTER_AREA_MAX_WIDTH_PERCENTAGE * getEffectiveRadius() * 2.0f;
                targetButtonRadius = targetButtonWidth / 2.0f;
                targetRingWidthRadius = RC_RING_MIN_BREATHE_DIAMETER_PERCENTAGE * getEffectiveRadius() - targetButtonWidth / 2.0f;
                minRingWidthRadius = targetRingWidthRadius;
                targetRingLineWidth = RC_RING_MIN_THICKNESS_PERCENTAGE * getEffectiveRadius() * 2.0f;
                minRingLineWidth = targetRingLineWidth;
                if (this.curBtnStatus == Status.INIT) {
                    rcCenterAreaWidth = targetButtonWidth;
                    rcCenterAreaRadius = targetButtonRadius;
                    rcRingRadiusWidth = targetRingWidthRadius;
                    rcRingLineWidth = targetRingLineWidth;
                    resetRCRingRect();
                    resetRCCenterRect();
                    invalidate();
                    return;
                }
            }
            break;
            case Status.RECORDING: {
                targetButtonWidth = RC_CENTER_AREA_MIN_WIDTH_PERCENTAGE * getEffectiveRadius() * 2.0f;
                targetButtonRadius = RC_CENTER_AREA_MIN_CORNER_RADIUS_PERCENTAGE * getEffectiveRadius() * 2.0f;
                targetRingWidthRadius = RC_RING_MAX_BREATHE_DIAMETER_PERCENTAGE * getEffectiveRadius() - RC_CENTER_AREA_MAX_WIDTH_PERCENTAGE * getEffectiveRadius();
                minRingWidthRadius = RC_RING_MIN_BREATHE_DIAMETER_PERCENTAGE * getEffectiveRadius() - RC_CENTER_AREA_MAX_WIDTH_PERCENTAGE * getEffectiveRadius();
                targetRingLineWidth = RC_RING_MAX_THICKNESS_PERCENTAGE * getEffectiveRadius() * 2.0f;
                minRingLineWidth = RC_RING_MIN_THICKNESS_PERCENTAGE * getEffectiveRadius() * 2.0f;
            }
            break;
        }
        PropertyValuesHolder btnWidthValuesHolder = PropertyValuesHolder.ofFloat("buttonWidth", nowButtonWidth, targetButtonWidth);
        PropertyValuesHolder btnRadiusValuesHolder = PropertyValuesHolder.ofFloat("buttonRadius", nowButtonRadius, targetButtonRadius);
        PropertyValuesHolder ringWidthRadiusValuesHolder = PropertyValuesHolder.ofFloat("ringWidthRadius", nowRingWidthRadius, minRingWidthRadius);
        PropertyValuesHolder ringLineWidthValuesHolder = PropertyValuesHolder.ofFloat("ringLineWidth", nowRingLineWidth, minRingLineWidth);
        rcCenterAnimation = ValueAnimator.ofPropertyValuesHolder(
                btnWidthValuesHolder, btnRadiusValuesHolder,
                ringWidthRadiusValuesHolder, ringLineWidthValuesHolder)
                .setDuration(RC_CENTER_AREA_ANIMATION_DURATION);
        rcCenterAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                rcCenterAreaWidth = (Float) animation.getAnimatedValue("buttonWidth");
                rcCenterAreaRadius = (Float) animation.getAnimatedValue("buttonRadius");
                rcRingRadiusWidth = (Float) animation.getAnimatedValue("ringWidthRadius");
                rcRingLineWidth = (Float) animation.getAnimatedValue("ringLineWidth");
                resetRCRingRect();
                resetRCCenterRect();
                invalidate();
            }
        });

        final float tempMinRingWidthRadius = minRingWidthRadius;
        final float tempTargetRingWidthRadius = targetRingWidthRadius;
        final float tempMinRingLineWidth = minRingLineWidth;
        final float tempTargetRingLineWidth = targetRingLineWidth;

        rcCenterAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (status == Status.RECORDING) {
                    //如果是录制状态，需要在变换动画结束后开始呼吸动画
                    PropertyValuesHolder repeatRingWidthRadiusValuesHolder = PropertyValuesHolder.ofFloat("repeatRingWidthRadius", tempMinRingWidthRadius, tempTargetRingWidthRadius);
                    PropertyValuesHolder repeatRingLineWidthValuesHolder = PropertyValuesHolder.ofFloat("repeatRingLineWidth", tempMinRingLineWidth, tempTargetRingLineWidth);
                    rcRingAnimation = ValueAnimator.ofPropertyValuesHolder(repeatRingWidthRadiusValuesHolder, repeatRingLineWidthValuesHolder)
                            .setDuration(RC_RING_REPEAT_ANIMATION_DURATION);
                    rcRingAnimation.setRepeatCount(ValueAnimator.INFINITE);
                    rcRingAnimation.setRepeatMode(ValueAnimator.REVERSE);
                    rcRingAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            rcRingRadiusWidth = (Float) animation.getAnimatedValue("repeatRingWidthRadius");
                            rcRingLineWidth = (Float) animation.getAnimatedValue("repeatRingLineWidth");
                            resetRCRingRect();
                            invalidate();
                        }
                    });
                    rcRingAnimation.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        rcCenterAnimation.start();
    }

    private void resetRCRingRect() {
        rcRingRectF.set(getPaddingStart() + getWidth() / 2 - getEffectiveRadius() * RC_CENTER_AREA_MAX_WIDTH_PERCENTAGE
                - rcRingRadiusWidth + rcRingLineWidth / 2, getPaddingTop() + getHeight() / 2
                - getEffectiveRadius() * RC_CENTER_AREA_MAX_WIDTH_PERCENTAGE - rcRingRadiusWidth + rcRingLineWidth / 2,
                getWidth() / 2 - getPaddingEnd() + getEffectiveRadius() * RC_CENTER_AREA_MAX_WIDTH_PERCENTAGE + rcRingRadiusWidth - rcRingLineWidth / 2,
                getHeight() / 2 - getPaddingBottom() + getEffectiveRadius() * RC_CENTER_AREA_MAX_WIDTH_PERCENTAGE + rcRingRadiusWidth - rcRingLineWidth / 2);
    }

    private void resetRCCenterRect() {
        rcCenterRectF.set(getPaddingStart() + getWidth() / 2 - rcCenterAreaWidth / 2,
                getPaddingTop() + getHeight() / 2 - rcCenterAreaWidth / 2,
                getWidth() / 2 - getPaddingEnd() + rcCenterAreaWidth / 2,
                getHeight() / 2 - getPaddingBottom() + rcCenterAreaWidth / 2);
    }


    /**
     * 初始化 TakePhoto 模式下的参数
     */
    private void initTPParams(@Status int status) {
        if (status == Status.RECORDING) {
            //拍照状态下的recording其实就是闪一下然后就需要把状态变为ready
            if (tpRecordAnimation != null) {
                tpRecordAnimation.cancel();
                tpRecordAnimation = null;
            }

            float maxButtonRadius = TP_BTN_MAX_SIZE_PERCENTAGE * getEffectiveRadius();
            float minButtonRadius = TP_BTN_MIN_SIZE_PERCENTAGE * getEffectiveRadius();

            float maxRingRadiusWidth = (TP_CENTER_AND_RING_MAGIN_PERCENTAGE + TP_RING_THICKNESS_PERCENTAGE) * getEffectiveRadius();
            float minRingRadiusWidth = maxRingRadiusWidth;
            PropertyValuesHolder btnRadiusValuesHolder = PropertyValuesHolder.ofFloat("buttonRadius", minButtonRadius, maxButtonRadius, minButtonRadius);
            PropertyValuesHolder ringRadiusWidthValuesHolder = PropertyValuesHolder.ofFloat("ringRadiusWidth", minRingRadiusWidth, maxRingRadiusWidth, minRingRadiusWidth);
            tpRecordAnimation = ValueAnimator.ofPropertyValuesHolder(btnRadiusValuesHolder, ringRadiusWidthValuesHolder)
                    .setDuration(TP_ANIMATION_DURATION);
            tpRecordAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    tpCenterCircleRadius = (Float) animation.getAnimatedValue("buttonRadius");
                    tpRingRadiusWidth = (Float) animation.getAnimatedValue("ringRadiusWidth");
                    resetTPRingRect();
                    invalidate();
                }
            });
            tpRecordAnimation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setRecordStatus(Status.IDLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    setRecordStatus(Status.IDLE);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            tpRecordAnimation.start();

        } else {
            //除了recording状态，其它状态对于photo都是一样的
            tpCenterCircleRadius = TP_BTN_MIN_SIZE_PERCENTAGE * getEffectiveRadius();
            tpRingRadiusWidth = (TP_CENTER_AND_RING_MAGIN_PERCENTAGE + TP_RING_THICKNESS_PERCENTAGE) * getEffectiveRadius();
            resetTPRingRect();
            invalidate();
        }
    }

    private void resetTPRingRect() {
        tpRingRectF.set(getPaddingStart() + getWidth() / 2 - tpCenterCircleRadius - tpRingRadiusWidth / 2,
                getPaddingTop() + getHeight() / 2 - tpCenterCircleRadius - tpRingRadiusWidth / 2,
                getWidth() / 2 - getPaddingEnd() + tpCenterCircleRadius + tpRingRadiusWidth / 2,
                getHeight() / 2 - getPaddingBottom() + tpCenterCircleRadius + tpRingRadiusWidth / 2);
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
        removeCallbacks(resetHandlingEventStatusRunnable);
        isHandlingClickEvent = false;
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
}
