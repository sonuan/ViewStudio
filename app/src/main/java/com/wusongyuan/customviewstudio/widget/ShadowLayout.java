package com.wusongyuan.customviewstudio.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.wusongyuan.customviewstudio.R;


public class ShadowLayout extends FrameLayout {

    private static final String TAG = "ShadowLayout";

    private int mShadowColor;
    private float mShadowRadius;
    private float mOriginShadowRadius;
    private float mCornerRadius;
    private float mDx;
    private float mDy;


    private int mW;
    private int mH;
    private Paint mBitmapPaint;
    private Paint mShadowPaint;
    private RectF mShadowRectF;

    public ShadowLayout(Context context) {
        super(context);
        initView(context, null);
    }

    public ShadowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ShadowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return 0;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return 0;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mW = w;
        mH = h;
    }

    public void invalidateShadow() {
        requestLayout();
        invalidate();
    }

    private void initView(Context context, AttributeSet attrs) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        initAttributes(context, attrs);

        setPadding();
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setStyle(Paint.Style.FILL);
    }

    private void setPadding() {
        int xPadding = (int) (mShadowRadius + Math.abs(mDx));
        int yPadding = (int) (mShadowRadius + Math.abs(mDy));
        setPadding(xPadding, yPadding, xPadding, yPadding);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray attr = getTypedArray(context, attrs, R.styleable.ShadowLayout);
        if (attr == null) {
            return;
        }

        try {
            mCornerRadius = attr.getDimension(R.styleable.ShadowLayout_sl_cornerRadius,
                    10);
            mShadowRadius = attr.getDimension(R.styleable.ShadowLayout_sl_shadowRadius,
                    10);
            mOriginShadowRadius = mShadowRadius;
            mDx = attr.getDimension(R.styleable.ShadowLayout_sl_dx, 0);
            mDy = attr.getDimension(R.styleable.ShadowLayout_sl_dy, 0);
            mShadowColor = attr.getColor(R.styleable.ShadowLayout_sl_shadowColor, 0xffffd321);
        } finally {
            attr.recycle();
        }
    }

    private TypedArray getTypedArray(Context context, AttributeSet attributeSet, int[] attr) {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas(mW, mH, mCornerRadius, mShadowRadius, mDx, mDy, mShadowColor, Color.TRANSPARENT, canvas);
        super.onDraw(canvas);
    }

    private void canvas(int shadowWidth, int shadowHeight, float cornerRadius, float shadowRadius, float dx, float dy,
            int shadowColor, int fillColor, Canvas canvas) {
        if (mShadowRectF == null) {
            mShadowRectF = new RectF();
        }
        float radius = mOriginShadowRadius - shadowRadius;
        mShadowRectF.set(radius, radius, shadowWidth - radius, shadowHeight - radius);

        if (dy > 0) {
            mShadowRectF.top += dy;
            mShadowRectF.bottom -= dy;
        } else if (dy < 0) {
            mShadowRectF.top += Math.abs(dy);
            mShadowRectF.bottom -= Math.abs(dy);
        }

        if (dx > 0) {
            mShadowRectF.left += dx;
            mShadowRectF.right -= dx;
        } else if (dx < 0) {
            mShadowRectF.left += Math.abs(dx);
            mShadowRectF.right -= Math.abs(dx);
        }
        if (mShadowPaint == null) {
            mShadowPaint = new Paint();
            mShadowPaint.setAntiAlias(true);
            mShadowPaint.setColor(fillColor);
            mShadowPaint.setStyle(Paint.Style.FILL);
        }

        Log.i(TAG, "canvas: shadowRadius:" + shadowRadius);

        if (!isInEditMode()) {
            mShadowPaint.setShadowLayer(shadowRadius, dx, dy, shadowColor);
        }
        cornerRadius = cornerRadius + shadowRadius;
        canvas.drawRoundRect(mShadowRectF, cornerRadius, cornerRadius, mShadowPaint);
    }

    public void setShadowRadius(float shadowRadius) {
        mShadowRadius = shadowRadius;
        invalidate();
    }

    public float getShadowRadius() {
        return mShadowRadius;
    }

    public void setNoShadow() {
        setNoShadow(false);
    }

    public void setNoShadow(boolean isKeepPadding) {
        Log.i(TAG, "setNoShadow() mShadowRadius:" + mShadowRadius + " | isKeepPadding:" + isKeepPadding);
        if (mShadowRadius == 0) {
            return;
        }
        mShadowRadius = 0;
        if (!isKeepPadding) {
            setPadding();
        }
        cancelAnim();
    }

    public void setCornerRadius(float cornerRadius) {
        mCornerRadius = cornerRadius;
        postInvalidate();
    }

    public float getCornerRadius() {
        return mCornerRadius;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private AnimatorSet animSet;

    private AnimatorSet anim() {
        if (animSet == null) {
            animSet = new AnimatorSet();
            ObjectAnimator shadowRadius = ObjectAnimator.ofFloat(this, "ShadowRadius", mOriginShadowRadius,
                    2).setDuration(800);
            shadowRadius.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Log.i(TAG, "onAnimationUpdate: " + animation.getAnimatedValue());
                }
            });
            //ObjectAnimator cornerRadius = ObjectAnimator.ofFloat(this, "CornerRadius", mCornerRadius,
            //        mCornerRadius + 10).setDuration(1000);
            //shadowRadius.setRepeatCount(ValueAnimator.INFINITE);
            //shadowRadius.setRepeatMode(ValueAnimator.REVERSE);
            //cornerRadius.setRepeatCount(ValueAnimator.INFINITE);
            //cornerRadius.setRepeatMode(ValueAnimator.REVERSE);
            animSet.playTogether(shadowRadius);
        }
        return animSet;
    }

    public void startAnim() {
        mShadowRadius = mOriginShadowRadius;
        setPadding();
        if (anim() != null && !anim().isStarted()) {
            anim().start();
        }
    }

    public void cancelAnim() {
        if (anim() != null && anim().isStarted()) {
            anim().cancel();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAnim();
    }
}