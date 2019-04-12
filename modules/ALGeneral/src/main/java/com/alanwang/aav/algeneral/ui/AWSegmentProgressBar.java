package com.alanwang.aav.algeneral.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.alanwang.aav.algeneral.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: AlanWang4523.
 * Date: 19/4/4 21:35.
 * Mail: alanwang4523@gmail.com
 */
public class AWSegmentProgressBar extends View {

    private final int DEFAULT_MAX_PROGRESS = 100;
    private final int DEFAULT_PROGRESS = 0;
    private final int DEFAULT_SEGMENT_DIVIDING_LINE_WIDTH = 4;
    private final float LAST_SEGMENT_ALPHA_NORMAL = 1.0f;
    private final float LAST_SEGMENT_ALPHA_PRE_DELETE = 0.4f;

    private long mCurProgress = DEFAULT_PROGRESS;
    private long mMaxProgress = DEFAULT_MAX_PROGRESS;
    private long mMinProgress = DEFAULT_PROGRESS;

    private Paint mPaint;
    private int mProgressColor;
    private int mBackgroundColor;
    private int mSegmentsDividingLineColor = Color.GRAY;// 两片段间分割线的颜色

    private float mLastSegmentAlpha = LAST_SEGMENT_ALPHA_NORMAL;
    private ValueAnimator mLastSegmentAnimation;

    private float mWidth, mHeight;
    private float mStartX, mStartY;
    private float mSegmentsDividingLineWidth;
    private List<Long> mSegmentList;
    private boolean isSupportDeleteWhenRunning = false;

    public AWSegmentProgressBar(Context context) {
        this(context, null);
    }

    public AWSegmentProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AWSegmentProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.AWSegmentProgressBar, defStyleAttr, 0);
        mMaxProgress = attributes.getInteger(R.styleable.AWSegmentProgressBar_spb_maxValue, DEFAULT_MAX_PROGRESS);
        mCurProgress = attributes.getInteger(R.styleable.AWSegmentProgressBar_spb_progressValue, DEFAULT_PROGRESS);
        mProgressColor = attributes.getColor(R.styleable.AWSegmentProgressBar_spb_progressColor, getResources().getColor(R.color.lib_video_record_progress));
        mBackgroundColor = attributes.getColor(R.styleable.AWSegmentProgressBar_spb_backgroundColor, getResources().getColor(R.color.lib_general_white_fa_4c));
        mSegmentsDividingLineWidth = attributes.getDimension(R.styleable.AWSegmentProgressBar_spb_segmentDividingLineWidth, DEFAULT_SEGMENT_DIVIDING_LINE_WIDTH);
        mSegmentsDividingLineColor = attributes.getColor(R.styleable.AWSegmentProgressBar_spb_segmentsDividingLineColor, Color.GRAY);
        attributes.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);

        mSegmentList = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制背景
        mPaint.setColor(mBackgroundColor);
        mPaint.setStrokeWidth(10);
        mStartX = 0;
        mStartY = 0;
        canvas.drawRect(mStartX, mStartY, mWidth, mHeight, mPaint);

        //绘制最短位置分割线
        if (mMinProgress > 0 && mMinProgress < mMaxProgress) {
            mPaint.setColor(mSegmentsDividingLineColor);
            mPaint.setStrokeWidth(mSegmentsDividingLineWidth);
            canvas.drawLine(mMinProgress * mWidth / mMaxProgress, mStartY, mMinProgress * mWidth / mMaxProgress, mHeight, mPaint);
        }

        mPaint.setColor(mProgressColor);
        mPaint.setStrokeWidth(10);//
        if (mSegmentList == null || mSegmentList.isEmpty()) {
            canvas.drawRect(mStartX, mStartY, ((float) mCurProgress / mMaxProgress) * mWidth, mHeight, mPaint);
        } else {
            //如果片段数大于 1，则先绘制前面的progress
            float lastSegmentStart = 0f;
            if (mSegmentList.size() > 1) {
                canvas.drawRect(mStartX, mStartY, (mSegmentList.get(mSegmentList.size() - 2) * 1.0f / mMaxProgress) * mWidth, mHeight, mPaint);
                lastSegmentStart = (mSegmentList.get(mSegmentList.size() - 2) * 1.0f / mMaxProgress) * mWidth;
            }
            mPaint.setAlpha((int) (mLastSegmentAlpha * 255));
            canvas.drawRect(lastSegmentStart, mStartY, ((float) mCurProgress / mMaxProgress) * mWidth, mHeight, mPaint);
        }

        mPaint.setColor(mSegmentsDividingLineColor);
        mPaint.setStrokeWidth(mSegmentsDividingLineWidth);
        mPaint.setAlpha(255);

        if (mSegmentList.size() > 0) {
            for (int i = 0; i < mSegmentList.size() ; i++) {
                canvas.drawLine(
                        ((float) mSegmentList.get(i) / mMaxProgress) * mWidth, mStartY,
                        ((float) mSegmentList.get(i) / mMaxProgress) * mWidth,
                        mStartY + (mHeight - mStartY),
                        mPaint);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mLastSegmentAnimation != null) {
            mLastSegmentAnimation.cancel();
        }
    }

    /**
     * 设置最大进度
     * @param max
     */
    public void setMaxProgress(long max) {
        this.mMaxProgress = max;
    }

    /**
     * 设置最小进度
     * @param minProgress
     */
    public void setMinProgress(long minProgress) {
        this.mMinProgress = minProgress;
        invalidate();
    }

    /**
     * 获取当前进度
     * @return
     */
    public long getProgress() {
        return mCurProgress;
    }

    /**
     * 设置当前进度
     * @param progress
     */
    public void setProgress(long progress) {
        if (progress <= mMaxProgress) {
            this.mCurProgress = progress;
            invalidate();
        }
    }

    /**
     * 结束一个片段
     */
    public void finishASegment() {
        mSegmentList.add(mCurProgress);
    }

    /**
     * 预删除上一个片段，片段闪烁用于提示将要删除该片段
     */
    public void prepareDeleteLastSegment() {
        if (mLastSegmentAnimation == null) {
            mLastSegmentAnimation = ValueAnimator.ofFloat(LAST_SEGMENT_ALPHA_NORMAL, LAST_SEGMENT_ALPHA_PRE_DELETE);
            mLastSegmentAnimation.setDuration(500L);
            mLastSegmentAnimation.setRepeatCount(ValueAnimator.INFINITE);
            mLastSegmentAnimation.setRepeatMode(ValueAnimator.REVERSE);
        }

        mLastSegmentAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLastSegmentAlpha = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        if (!mLastSegmentAnimation.isStarted()) {
            mLastSegmentAnimation.start();
        }
    }

    /**
     * 取消删除上个片段
     */
    public void cancelDeleteLastSegment() {
        if (mLastSegmentAnimation != null) {
            mLastSegmentAnimation.cancel();
            mLastSegmentAnimation = null;
        }
        mLastSegmentAlpha = LAST_SEGMENT_ALPHA_NORMAL;
        invalidate();
    }

    /**
     * 删除上一个片段
     */
    public void deleteLastSegment() {
        if (mSegmentList.size() > 0) {
            long last = mSegmentList.get(mSegmentList.size()-1);
            if(mCurProgress > last && isSupportDeleteWhenRunning) {
                // 运行时删除
                mCurProgress = last;
            } else {
                mSegmentList.remove(mSegmentList.size() - 1);
                if (mSegmentList.size() == 0) {
                    mCurProgress = DEFAULT_PROGRESS;
                } else {
                    mCurProgress = mSegmentList.get(mSegmentList.size()-1);
                }
            }
        }else {
            mCurProgress = DEFAULT_PROGRESS;
        }
        invalidate();
    }

    /**
     * 删除所有片段
     */
    public void deleteAllSegments(){
        mSegmentList.clear();
        mCurProgress = DEFAULT_PROGRESS;
        invalidate();
    }
}
