package com.wusongyuan.customviewstudio.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**********************
 * @author: wusongyuan
 * @date: 2016-05-03
 * @desc: 仿支付宝sdk支付结果View, 加载中\支付成功\支付失败
 **********************/
public class CircleLoadingView extends View {
    private static final String TAG = CircleLoadingView.class.getSimpleName();
    /**
     * 打钩
     */
    private Paint mTickPaint = new Paint();
    /**
     * 圆形进度条
     */
    private Paint mCirclePaint = new Paint();
    /**
     * 圆形进度条
     */
    private Paint mCircleBgPaint = new Paint();
    /**
     * 画笔宽度
     */
    private int mStrokeWidth = 10;
    /**
     * 当前进度
     */
    private float progress = 0f; // 0~1

    /**
     * 最大进度
     */
    private float mMaxProgress = 100f;
    /**
     * 进度的1/3
     */
    private static final float ONE_THIRD_PERCENT = 1 / 3f;
    /**
     * 打钩百分比
     */
    private float tickPercent = 0f;

    /**
     * 正在绘制圆形进度条状态
     */
    public static final int STATUS_CIRCLE = 0;
    /**
     * 正在绘制打钩进度条状态
     */
    public static final int STATUS_TICK = 1;

    private int mStatus = STATUS_CIRCLE;

    private int mWidth;
    private int mHeight;
    private RectF mRectF = new RectF();


    private int mColor = 0xffffffff;
    /**圆形背景色*/
    private int mColor_BG = 0x99ffffff;
    /**渐变颜色*/
    private int mColors[] = new int[]{0xFFFFFFFF, 0xFF888888, 0xFF000000};
    private int mRadius;

    private ValueAnimator mStartAngleAnimator;
    private ValueAnimator mEndAngleAnimator;
    private ValueAnimator mLoadingAnimator;
    /**
     * 起始角度
     */
    private float mStartAngle;
    /**
     * 结束角度
     */
    private float mEndAngle;


    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        postInvalidate();
    }

    public float getTickPercent() {
        return tickPercent;
    }

    public void setTickPercent(float tickPercent) {
        this.tickPercent = tickPercent;
        postInvalidate();
    }

    public float getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(float startAngle) {
        mStartAngle = startAngle;
    }

    public float getEndAngle() {
        return mEndAngle;
    }

    public void setEndAngle(float endAngle) {
        mEndAngle = endAngle;
    }

    public CircleLoadingView(Context context) {
        super(context);

    }

    public CircleLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public CircleLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initPaint() {
        Shader shader = new SweepGradient(0, 0, mColors, null);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(mColor);
        mCirclePaint.setStrokeWidth(mStrokeWidth);
        mCirclePaint.setStyle(Paint.Style.STROKE); //
        mCirclePaint.setStrokeCap(Paint.Cap.ROUND); // 圆角线条
        mCirclePaint.setShader(shader); // 渐变

        mCircleBgPaint.setAntiAlias(true);
        mCircleBgPaint.setColor(mColor_BG);
        mCircleBgPaint.setStrokeWidth(mStrokeWidth * 2);
        mCircleBgPaint.setStyle(Paint.Style.STROKE);
        mCircleBgPaint.setStrokeCap(Paint.Cap.BUTT);

        mTickPaint.setAntiAlias(true);
        mTickPaint.setStrokeCap(Paint.Cap.ROUND);
        mTickPaint.setColor(mColor);
        mTickPaint.setStyle(Paint.Style.FILL);
        mTickPaint.setStrokeWidth(mStrokeWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int width = getWidth();
        // 半径
        mRadius = (width - getPaddingLeft() - getPaddingRight() - 2 * mStrokeWidth) / 2;
        int top = mStrokeWidth + getPaddingTop();
        int bottom = width - mStrokeWidth - getPaddingBottom();
        int left = mStrokeWidth + getPaddingLeft();
        int right = width - mStrokeWidth - getPaddingRight();
        mRectF.set(new RectF(left, top, right, bottom));
    }

    float stop1X = 0;
    float stop1Y = 0;
    float start2X = 0;
    float start2Y = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制圆形进度条背景
        canvas.drawArc(mRectF, 0, 360, false, mCircleBgPaint);
        switch (mStatus) {
            case STATUS_CIRCLE:
                if (mEndAngle > 0) {
                    canvas.drawArc(mRectF, -90 + mStartAngle * 360, (mEndAngle-mStartAngle) * 360, false, mCirclePaint);
                }
                break;
            case STATUS_TICK:
                canvas.drawArc(mRectF, 0, 360, false, mCirclePaint);
                float startX = mRectF.left + mRadius / 2;
                float startY = mRectF.top + mRadius;

                if (tickPercent > 0 && tickPercent < ONE_THIRD_PERCENT) {
                    stop1X = startX + tickPercent * mRadius;
                    stop1Y = startY + tickPercent * mRadius;
                    start2X = stop1X;
                    start2Y = stop1Y;
                    canvas.drawLine(startX, startY, stop1X, stop1Y, mTickPaint);
                }

                if (tickPercent >= ONE_THIRD_PERCENT && tickPercent <= 1f) {
                    canvas.drawLine(startX, startY, start2X, start2Y, mTickPaint);
                    float stop2X = start2X + (tickPercent - ONE_THIRD_PERCENT) * mRadius;
                    float stop2Y = start2Y - (tickPercent - ONE_THIRD_PERCENT) * mRadius;
                    canvas.drawLine(start2X, start2Y, stop2X, stop2Y, mTickPaint);
                }
                break;
        }


    }

    public void loading() {
        mStartAngleAnimator = ValueAnimator.ofFloat(0, 1f);
        mStartAngleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mStartAngleAnimator.setDuration(2000);
        mStartAngleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setStartAngle(value);
            }
        });
        mStartAngleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mEndAngleAnimator = ValueAnimator.ofFloat(0, 1f);
        mEndAngleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mEndAngleAnimator.setDuration(5000);
        mEndAngleAnimator.setRepeatCount(-1);
        mEndAngleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setEndAngle(value);
                if (value >= 0.5) {
                    if (mStartAngleAnimator != null && !mStartAngleAnimator.isRunning()) {
                        mStartAngleAnimator.start();
                    }
                }
            }
        });
        mEndAngleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mStatus = STATUS_CIRCLE;

            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mLoadingAnimator = ValueAnimator.ofFloat(0, 1f);
        mLoadingAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mLoadingAnimator.setDuration(2000);
        mLoadingAnimator.setRepeatCount(-1);
        mLoadingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                postInvalidate();
            }
        });
        mLoadingAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mEndAngleAnimator.start();
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mLoadingAnimator.start();
    }

    public void loadSuccess() {
        mStartAngleAnimator = ValueAnimator.ofFloat(0, 1f);
        mStartAngleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mStartAngleAnimator.setDuration(2000);
        mStartAngleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setProgress(value);
            }
        });
        mStartAngleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mStatus = STATUS_CIRCLE;

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mStatus = STATUS_TICK;
                ObjectAnimator.ofFloat(CircleLoadingView.this, "tickPercent", 0f, 1f).setDuration(1000).start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
