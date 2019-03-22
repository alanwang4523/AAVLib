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
import android.util.Log;
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

    public interface OnClickListener {
        void onClick();
    }

    @IntDef({
            Mode.MODE_SINGLE_CLICK,
            Mode.MODE_TAKE_PHOTO
    })

    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
        int MODE_SINGLE_CLICK = 0;
        int MODE_TAKE_PHOTO = 1;
    }

    @IntDef({
            Status.STATUS_INIT,
            Status.STATUS_READY,
            Status.STATUS_RECORDING,
            Status.STATUS_CHANGING_MODE
    })

    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
        int STATUS_INIT = 0;
        int STATUS_READY = 1;
        int STATUS_RECORDING = 2;
        int STATUS_CHANGING_MODE = 4;
    }

    private float tpButtonWidthMaxPercent = 0.78f;
    private float tpButtonWidthMinPercent = 0.56f;
    private float scButtonMinRadiusPercent = 0.04f;
    private float scButtonWidthMaxPercent = 0.56f;
    private float scButtonWidthMinPercent = 0.28f;
    private float scRingWidthRecordingMaxPercent = 0.9f;
    private float scRingWidthRecordingMinPercent = 0.7f;
    private float scRingLineWidthRecordingMaxPercent = 0.068f;
    private float scRingLineWidthRecordingMinPercent = 0.03f;

    private float tpButtonRadius = 0f;
    private float tpRingRadiusWidth = 0f;
    private float scButtonWidth = 0f;
    private float scButtonRadius = 0f;
    private float scRingRadiusWidth = 0f;
    private float scRingLineWidth = 0f;

    private int tpButtonColor = getColor(R.color.lib_general_white_alpha_ff);
    private int tpRingColor = getColor(R.color.lib_general_white_ff_55);
    private int scButtonColor = getColor(R.color.lib_general_white_alpha_ff);// 中间方形按钮颜色
    private int scRingColorStatic = getColor(R.color.lib_general_white_alpha_ff);
    private int scRingColorBreath = getColor(R.color.lib_general_white_ff_55);

    private Paint scButtonPaint = new Paint();
    private Paint scRingPaint = new Paint();

    private ValueAnimator tpRecordAnimation = null;
    private long tpRecordAnimDuration = 300L;
    private ValueAnimator scButtonAnimation = null;
    private ValueAnimator scRingAnimation = null;
    private long scButtonChangeAnimDuration = 500L;
    private long scRingRepeatAnimDuration = 1000L;

    private RectF tpRingRectF = new RectF();
    private RectF scRingRectF = new RectF();
    private RectF scButtonRectF = new RectF();

    private EnableSingleClickRunnable enableSingleClickRunnable = new EnableSingleClickRunnable();
    private boolean enableSingleClick = true;

    private OnClickListener listener = null;

    private @Mode int btnMode = Mode.MODE_SINGLE_CLICK;
    private @Status int status = Status.STATUS_INIT;


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
        scButtonPaint.setFilterBitmap(true);
        scButtonPaint.setDither(true);
        scButtonPaint.setAntiAlias(true);
        scButtonPaint.setColor(scButtonColor);
        scButtonPaint.setStyle(Paint.Style.FILL);

        scRingPaint.setFilterBitmap(true);
        scRingPaint.setDither(true);
        scRingPaint.setAntiAlias(true);
        scRingPaint.setColor(scRingColorStatic);
        scRingPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (btnMode) {
            case Mode.MODE_SINGLE_CLICK:
                drawSCRing(canvas);
                drawSCButton(canvas);
                break;
            case Mode.MODE_TAKE_PHOTO:
                drawTPRing(canvas);
                drawTPButton(canvas);
                break;
            default:
                break;
        }
    }

    private void drawTPButton(Canvas canvas) {
        scButtonPaint.setColor(tpButtonColor);
        canvas.drawCircle(getEffectiveRadius(), getEffectiveRadius(), tpButtonRadius, scButtonPaint);
    }

    private void drawTPRing(Canvas canvas) {
        scRingPaint.setStrokeWidth(tpRingRadiusWidth - 0.08f * getEffectiveRadius());
        scRingPaint.setColor(tpRingColor);
        canvas.drawArc(tpRingRectF, -90f, 360f, false, scRingPaint);
    }

    private void drawSCButton(Canvas canvas) {
        scButtonPaint.setColor(scButtonColor);
        canvas.drawRoundRect(scButtonRectF, scButtonRadius, scButtonRadius, scButtonPaint);
    }

    private void drawSCRing(Canvas canvas) {
        scRingPaint.setStrokeWidth(scRingLineWidth);
        if (status == Status.STATUS_READY) {
            if (scRingLineWidth == scRingLineWidthRecordingMinPercent * getWidth()) {
                scRingPaint.setColor(scRingColorStatic);
            } else {
                scRingPaint.setColor(scRingColorBreath);
            }
        } else if (status == Status.STATUS_RECORDING) {
            scRingPaint.setColor(scRingColorBreath);
        }
        canvas.drawArc(scRingRectF, -90f, 360f, false, scRingPaint);
    }

    /**
     * 获取有效的最大半径
     */
    private float getEffectiveRadius() {
        return (getWidth() - getPaddingStart() - getPaddingEnd()) / 2.0f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "OnTouchEvent--->Down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "OnTouchEvent--->Move");
                //do nothing
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "OnTouchEvent--->Up");
                handlerEventActionUpByState();
                break;
            default:
                break;
        }
        return true;
    }

    //当手指松开按钮时候处理的逻辑
    private void handlerEventActionUpByState() {
        // 如果是正在改变状态的状态就直接不处理所有touch事件
        if (status == Status.STATUS_CHANGING_MODE) {
            return;
        }
        if (enableSingleClick) {
            if (listener != null) {
                listener.onClick();
            }
            enableSingleClick = false;
            postDelayed(enableSingleClickRunnable, 500L);
        }
    }

    /**
     * 设置当前模式
     */
    public void setMode(@Mode int mode) {
        if (this.btnMode == mode) {
            return;
        }
        this.btnMode = mode;
        setRecordStatus(Status.STATUS_READY, true);
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    public void setRecordStatus(@Status int status) {
        setRecordStatus(status, false);
    }

    /**
     * 设置当前状态
     */
    private void setRecordStatus(@Status int status, boolean isForce) {
        if (this.status == status && !isForce) {
            return;
        }
        //如果是正在改变模式的状态，直接设置后返回
        if (status == Status.STATUS_CHANGING_MODE) {
            this.status = status;
            return;
        }
        if (btnMode == Mode.MODE_SINGLE_CLICK) {
            calculateSCParams(status);
        } else if (btnMode == Mode.MODE_TAKE_PHOTO) {
            calculateTPParams(status);
        }
        this.status = status;
    }

    /**
     * 计算SingleClick下的参数并刷新
     */
    private void calculateSCParams(@Status final int status) {
        if (scButtonAnimation != null) {
            scButtonAnimation.cancel();
            scButtonAnimation = null;
        }
        if (scRingAnimation != null) {
            scRingAnimation.cancel();
            scRingAnimation = null;
        }
        float targetButtonWidth = 0f;
        float nowButtonWidth = scButtonWidth;
        float targetButtonRadius = 0f;
        float nowButtonRadius = scButtonRadius;
        float targetRingWidthRadius = 0f;
        float nowRingWidthRadius = scRingRadiusWidth;
        float minRingWidthRadius = 0f;
        float targetRingLineWidth = 0f;
        float nowRingLineWidth = scRingLineWidth;
        float minRingLineWidth = 0f;
        switch (status) {
            case Status.STATUS_READY: {
                targetButtonWidth = scButtonWidthMaxPercent * getEffectiveRadius() * 2.0f;
                targetButtonRadius = targetButtonWidth / 2.0f;
                targetRingWidthRadius = scRingWidthRecordingMinPercent * getEffectiveRadius() - targetButtonWidth / 2.0f;
                minRingWidthRadius = targetRingWidthRadius;
                targetRingLineWidth = scRingLineWidthRecordingMinPercent * getEffectiveRadius() * 2.0f;
                minRingLineWidth = targetRingLineWidth;
                if (this.status == Status.STATUS_INIT) {
                    scButtonWidth = targetButtonWidth;
                    scButtonRadius = targetButtonRadius;
                    scRingRadiusWidth = targetRingWidthRadius;
                    scRingLineWidth = targetRingLineWidth;
                    calculateSCRingRect();
                    calculateSCButtonRect();
                    invalidate();
                    return;
                }
            }
            break;
            case Status.STATUS_RECORDING : {
                targetButtonWidth = scButtonWidthMinPercent * getEffectiveRadius() * 2.0f;
                targetButtonRadius = scButtonMinRadiusPercent * getEffectiveRadius() * 2.0f;
                targetRingWidthRadius = scRingWidthRecordingMaxPercent * getEffectiveRadius() - scButtonWidthMaxPercent * getEffectiveRadius();
                minRingWidthRadius = scRingWidthRecordingMinPercent * getEffectiveRadius() - scButtonWidthMaxPercent * getEffectiveRadius();
                targetRingLineWidth = scRingLineWidthRecordingMaxPercent * getEffectiveRadius() * 2.0f;
                minRingLineWidth = scRingLineWidthRecordingMinPercent * getEffectiveRadius() * 2.0f;
            }
            break;
        }
        PropertyValuesHolder btnWidthValuesHolder = PropertyValuesHolder.ofFloat("buttonWidth", nowButtonWidth, targetButtonWidth);
        PropertyValuesHolder btnRadiusValuesHolder = PropertyValuesHolder.ofFloat("buttonRadius", nowButtonRadius, targetButtonRadius);
        PropertyValuesHolder ringWidthRadiusValuesHolder = PropertyValuesHolder.ofFloat("ringWidthRadius", nowRingWidthRadius, minRingWidthRadius);
        PropertyValuesHolder ringLineWidthValuesHolder = PropertyValuesHolder.ofFloat("ringLineWidth", nowRingLineWidth, minRingLineWidth);
        scButtonAnimation = ValueAnimator.ofPropertyValuesHolder(
                btnWidthValuesHolder, btnRadiusValuesHolder,
                ringWidthRadiusValuesHolder, ringLineWidthValuesHolder)
                .setDuration(scButtonChangeAnimDuration);
        scButtonAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scButtonWidth = (Float) animation.getAnimatedValue("buttonWidth");
                scButtonRadius = (Float) animation.getAnimatedValue("buttonRadius");
                scRingRadiusWidth = (Float) animation.getAnimatedValue("ringWidthRadius");
                scRingLineWidth = (Float) animation.getAnimatedValue("ringLineWidth");
                calculateSCRingRect();
                calculateSCButtonRect();
                invalidate();
            }
        });

        final float tempMinRingWidthRadius = minRingWidthRadius;
        final float tempTargetRingWidthRadius = targetRingWidthRadius;
        final float tempMinRingLineWidth = minRingLineWidth;
        final float tempTargetRingLineWidth = targetRingLineWidth;

        scButtonAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (status == Status.STATUS_RECORDING) {
                    //如果是录制状态，需要在变换动画结束后开始呼吸动画
                    PropertyValuesHolder repeatRingWidthRadiusValuesHolder = PropertyValuesHolder.ofFloat("repeatRingWidthRadius", tempMinRingWidthRadius, tempTargetRingWidthRadius);
                    PropertyValuesHolder repeatRingLineWidthValuesHolder = PropertyValuesHolder.ofFloat("repeatRingLineWidth", tempMinRingLineWidth, tempTargetRingLineWidth);
                    scRingAnimation = ValueAnimator.ofPropertyValuesHolder(repeatRingWidthRadiusValuesHolder, repeatRingLineWidthValuesHolder)
                            .setDuration(scRingRepeatAnimDuration);
                    scRingAnimation.setRepeatCount(ValueAnimator.INFINITE);
                    scRingAnimation.setRepeatMode(ValueAnimator.REVERSE);
                    scRingAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            scRingRadiusWidth = (Float) animation.getAnimatedValue("repeatRingWidthRadius");
                            scRingLineWidth = (Float) animation.getAnimatedValue("repeatRingLineWidth");
                            calculateSCRingRect();
                            invalidate();
                        }
                    });
                    scRingAnimation.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        scButtonAnimation.start();
    }

    private void calculateSCRingRect() {
        scRingRectF.set(getPaddingStart() + getWidth() / 2 - getEffectiveRadius() * scButtonWidthMaxPercent
                - scRingRadiusWidth + scRingLineWidth / 2, getPaddingTop() + getHeight() / 2
                - getEffectiveRadius() * scButtonWidthMaxPercent - scRingRadiusWidth + scRingLineWidth / 2,
                getWidth() / 2 - getPaddingEnd() + getEffectiveRadius() * scButtonWidthMaxPercent + scRingRadiusWidth - scRingLineWidth / 2,
                getHeight() / 2 - getPaddingBottom() + getEffectiveRadius() * scButtonWidthMaxPercent + scRingRadiusWidth - scRingLineWidth / 2);
    }

    private void calculateSCButtonRect() {
        scButtonRectF.set(getPaddingStart() + getWidth() / 2 - scButtonWidth / 2,
                getPaddingTop() + getHeight() / 2 - scButtonWidth / 2,
                getWidth() / 2 - getPaddingEnd() + scButtonWidth / 2,
                getHeight() / 2 - getPaddingBottom() + scButtonWidth / 2);
    }


    /**
     * 计算TakePhoto下的参数并刷新
     */
    private void calculateTPParams(@Status int status) {
        if (status == Status.STATUS_RECORDING) {
            //拍照状态下的recording其实就是闪一下然后就需要把状态变为ready
            if (tpRecordAnimation != null) {
                tpRecordAnimation.cancel();
                tpRecordAnimation = null;
            }

            float maxButtonRadius = tpButtonWidthMaxPercent * getEffectiveRadius();
            float minButtonRadius = tpButtonWidthMinPercent * getEffectiveRadius();

            float maxRingRadiusWidth = (tpButtonWidthMaxPercent + 0.22f) * getEffectiveRadius() - maxButtonRadius;
            float minRingRadiusWidth = (tpButtonWidthMinPercent + 0.22f) * getEffectiveRadius() - minButtonRadius;
            PropertyValuesHolder btnRadiusValuesHolder = PropertyValuesHolder.ofFloat("buttonRadius", minButtonRadius, maxButtonRadius, minButtonRadius);
            PropertyValuesHolder ringRadiusWidthValuesHolder = PropertyValuesHolder.ofFloat("ringRadiusWidth", minRingRadiusWidth, maxRingRadiusWidth, minRingRadiusWidth);
            tpRecordAnimation = ValueAnimator.ofPropertyValuesHolder(btnRadiusValuesHolder, ringRadiusWidthValuesHolder)
                    .setDuration(tpRecordAnimDuration);
            tpRecordAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    tpButtonRadius = (Float) animation.getAnimatedValue("buttonRadius");
                    tpRingRadiusWidth = (Float) animation.getAnimatedValue("ringRadiusWidth");
                    calculateTPRingRect();
                    invalidate();
                }
            });
            tpRecordAnimation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setRecordStatus(Status.STATUS_READY);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    setRecordStatus(Status.STATUS_READY);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            tpRecordAnimation.start();

        } else {
            //除了recording状态，其它状态对于photo都是一样的
            tpButtonRadius = tpButtonWidthMinPercent * getEffectiveRadius();
            tpRingRadiusWidth = (tpButtonWidthMinPercent + 0.22f) * getEffectiveRadius() - tpButtonRadius;
            calculateTPRingRect();
            invalidate();
        }
    }

    private void calculateTPRingRect() {
        tpRingRectF.set(getPaddingStart() + getWidth() / 2 - tpButtonRadius - tpRingRadiusWidth / 2,
                getPaddingTop() + getHeight() / 2 - tpButtonRadius - tpRingRadiusWidth / 2,
                getWidth() / 2 - getPaddingEnd() + tpButtonRadius + tpRingRadiusWidth / 2,
                getHeight() / 2 - getPaddingBottom() + tpButtonRadius + tpRingRadiusWidth / 2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setRecordStatus(Status.STATUS_READY);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (tpRecordAnimation != null) {
            tpRecordAnimation.cancel();
        }
        if (scButtonAnimation != null) {
            scButtonAnimation.cancel();
        }
        if (scRingAnimation != null) {
            scRingAnimation.cancel();
        }
        removeCallbacks(enableSingleClickRunnable);
        enableSingleClick = true;
    }

    private int getColor(@ColorRes int resId) {
        return this.getContext().getResources().getColor(resId);
    }

    private class EnableSingleClickRunnable implements Runnable {
        @Override
        public void run() {
            enableSingleClick = true;
        }
    }
}
