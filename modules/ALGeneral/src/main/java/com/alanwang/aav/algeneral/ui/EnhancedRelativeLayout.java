package com.alanwang.aav.algeneral.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.alanwang.aav.algeneral.R;

/**
 * 可设置圆角和宽高比的增强型 RelativeLayout
 *
 * @author SKY
 * @since 2015.12.16
 */
public class EnhancedRelativeLayout extends RelativeLayout {

    private static final int BASE_EDGE_WIDTH = 0; // 以宽为基准边
    private static final int BASE_EDGE_HEIGHT = 1; // 以高为基准边
    private static final int BASE_EDGE_SHORTER = 2; // 以较短边为基准边
    private static final int BASE_EDGE_LONGER = 3; // 以较长边为基准边

    private float mRoundRadius = -1.0f; // 圆角半径
    private float mRatio = -1.0f; // 高宽比
    private float mMaxWidth;
    private float mMaxHeight;
    private int mBaseEdge = 0; // 基准边

    private Path mClipPath;
    private final RectF mWHRectF = new RectF();
    private final DrawFilter mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG);

    public EnhancedRelativeLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public EnhancedRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public EnhancedRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.EnhancedRelativeLayout, defStyle, 0);
            mRoundRadius = a.getDimension(R.styleable.EnhancedRelativeLayout_layout_roundCornerRadius, mRoundRadius);
            mRatio = a.getFloat(R.styleable.EnhancedRelativeLayout_layout_ratio, mRatio);
            mBaseEdge = a.getInt(R.styleable.EnhancedRelativeLayout_layout_baseEdge, mBaseEdge);
            mMaxWidth = a.getDimension(R.styleable.EnhancedRelativeLayout_layout_maxWidth, Float.MAX_VALUE);
            mMaxHeight = a.getDimension(R.styleable.EnhancedRelativeLayout_layout_maxHeight, Float.MAX_VALUE);
            a.recycle();
        }
        mClipPath = new Path();
//        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mRatio >= 0f) {
            int w = getMeasuredWidth();
            int h = getMeasuredHeight();

            switch (mBaseEdge) {
                case BASE_EDGE_WIDTH: {// 以宽度为基准
                    h = (int) (w * mRatio);
                    break;
                }
                case BASE_EDGE_HEIGHT: {// 以高度为基准
                    w = (int) (h * mRatio);
                    break;
                }
                case BASE_EDGE_SHORTER: {// 以短边为基准
                    if (w < h) h = (int) (w * mRatio);
                    else w = (int) (h * mRatio);
                    break;
                }
                case BASE_EDGE_LONGER: {// 以长边为基准
                    if (w > h) h = (int) (w * mRatio);
                    else w = (int) (h * mRatio);
                    break;
                }
            }

            // 不超过最长宽高
            w = (int) Math.min(w, mMaxWidth);
            h = (int) Math.min(h, mMaxHeight);

            //measure children
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            mClipPath.reset();
            mWHRectF.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
            if (mRoundRadius > 0) {
                mClipPath.addRoundRect(mWHRectF, mRoundRadius, mRoundRadius, Path.Direction.CW);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        //实现圆角，在组件绘制前先限定范围
        canvas.setDrawFilter(mDrawFilter);
        if (mRoundRadius > 0) {
            canvas.clipPath(mClipPath);
        }
        super.draw(canvas);
    }

    /**
     * 获取圆角半径
     * @return
     */
    public float getRoundCornerRadius() {
        return mRoundRadius;
    }

    /**
     * 设置圆角半径
     * @param roundCornerRadius
     */
    public void setRoundCornerRadius(float roundCornerRadius) {
        if(mRoundRadius != roundCornerRadius){
            mRoundRadius = roundCornerRadius;
            invalidate();
        }
    }

    /**
     * 设置高宽比，即高/宽
     *
     * @param ratio
     */
    public void setRatio(float ratio) {
        if(mRatio != ratio){
            mRatio = ratio;
            requestLayout();
            invalidate();
        }
    }

}
