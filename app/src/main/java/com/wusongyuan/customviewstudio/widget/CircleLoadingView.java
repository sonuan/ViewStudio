package com.wusongyuan.customviewstudio.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

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
    private Paint mCirclePaint2 = new Paint();
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
    public static final int STATUS_LOADING = 2;
    public static final int STATUS_LOADING2 = STATUS_LOADING + 1;

    private int mStatus = STATUS_CIRCLE;

    private int mWidth;
    private int mHeight;
    private RectF mRectF = new RectF();


    private int mColor = 0xffffffff;
    /**
     * 圆形背景色
     */
    private int mColor_BG = 0x99ffffff;
    /**
     * 渐变颜色
     */
    private int mColors[] = new int[]{0xFFFFFFFF, 0xFF888888, 0xFF000000};
    private int mRadius;

    private ValueAnimator mStartAngleAnimator;
    private ValueAnimator mEndAngleAnimator;
    private ValueAnimator mLoadingAnimator;
    /**
     * 起始角度
     */
    private float mStartAngle;
    private static final float START_ANGLE = -90;
    /**
     * 结束角度
     */
    private float mEndAngle;

    private static final float END_TRIM_DURATION_OFFSET = 1.0f;
    private static final float START_TRIM_DURATION_OFFSET = 0.5f;
    private ValueAnimator mValueAnimator;


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
        mStartAngle = START_ANGLE;
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

        mCirclePaint2.setAntiAlias(true);
        mCirclePaint2.setColor(mColor);
        mCirclePaint2.setStrokeWidth(mStrokeWidth);
        mCirclePaint2.setStyle(Paint.Style.STROKE); //
        mCirclePaint2.setStrokeCap(Paint.Cap.ROUND); // 圆角线条

        mCircleBgPaint.setAntiAlias(true);
        mCircleBgPaint.setColor(mColor_BG);
        mCircleBgPaint.setStrokeWidth(mStrokeWidth * 2);
        mCircleBgPaint.setStyle(Paint.Style.STROKE);
        mCircleBgPaint.setStrokeCap(Paint.Cap.BUTT);

        mTickPaint.setAntiAlias(true);
        mTickPaint.setStrokeCap(Paint.Cap.ROUND);
        mTickPaint.setColor(mColor);
        mTickPaint.setStyle(Paint.Style.STROKE);
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

    private Path mPath = new Path();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制圆形进度条背景
        canvas.drawArc(mRectF, 0, 360, false, mCircleBgPaint);
        switch (mStatus) {
            case STATUS_LOADING2:
                int saveCount = canvas.save();
                canvas.rotate(mGroupRotation, mRectF.centerX(), mRectF.centerY());

                canvas.drawArc(mRectF, mStartDegrees, mSwipeDegrees, false, mCirclePaint2);
                canvas.restoreToCount(saveCount);

                break;
            case STATUS_LOADING:
                if (progress > 0) {
                    //                    canvas.drawArc(mRectF, -90 + mStartAngle * 360, (mEndAngle - mStartAngle) *
                    //                                    360, false,
                    //                            mCirclePaint);
                    canvas.drawArc(mRectF, -90 + progress * 360, progress * 360, false, mCirclePaint2);
                }
                break;
            case STATUS_CIRCLE:
                if (progress > 0) {
                    canvas.drawArc(mRectF, mStartAngle, progress * 360, false,
                            mCirclePaint);
                }
                break;
            case STATUS_TICK:
                canvas.drawArc(mRectF, 0, 360, false, mCirclePaint);
                float startX = mRectF.left + mRadius / 2;
                float startY = mRectF.top + mRadius;

                mPath.moveTo(startX, startY);
                if (tickPercent > 0 && tickPercent < ONE_THIRD_PERCENT) {
                    stop1X = startX + tickPercent * mRadius;
                    stop1Y = startY + tickPercent * mRadius;
                    start2X = stop1X;
                    start2Y = stop1Y;
                    mPath.lineTo(stop1X, stop1Y);
                }
                if (tickPercent >= ONE_THIRD_PERCENT && tickPercent <= 1f) {
                    mPath.lineTo(start2X, start2Y);
                    float stop2X = start2X + (tickPercent - ONE_THIRD_PERCENT) * mRadius;
                    float stop2Y = start2Y - (tickPercent - ONE_THIRD_PERCENT) * mRadius;
                    mPath.lineTo(stop2X, stop2Y);
                }

                canvas.drawPath(mPath, mTickPaint);

                //-------画两条线,已用path替换了-----------
                //                if (tickPercent > 0 && tickPercent < ONE_THIRD_PERCENT) {
                //                    stop1X = startX + tickPercent * mRadius;
                //                    stop1Y = startY + tickPercent * mRadius;
                //                    start2X = stop1X;
                //                    start2Y = stop1Y;
                //                    canvas.drawLine(startX, startY, stop1X, stop1Y, mTickPaint);
                //                }
                //
                //                if (tickPercent >= ONE_THIRD_PERCENT && tickPercent <= 1f) {
                //                    canvas.drawLine(startX, startY, start2X, start2Y, mTickPaint);
                //                    float stop2X = start2X + (tickPercent - ONE_THIRD_PERCENT) * mRadius;
                //                    float stop2Y = start2Y - (tickPercent - ONE_THIRD_PERCENT) * mRadius;
                //                    canvas.drawLine(start2X, start2Y, stop2X, stop2Y, mTickPaint);
                //                }
                break;
        }

    }

    private float mEndDegrees;
    private float mStartDegrees;
    private float mSwipeDegrees;
    private float mOriginEndDegrees;
    private float mOriginStartDegrees;

    private float mRotationCount;
    private float mGroupRotation;


    private static final int DEGREE_360 = 360;
    private static final int NUM_POINTS = 5;


    private static final Interpolator MATERIAL_INTERPOLATOR = new FastOutSlowInInterpolator();

    private static final float MIN_SWIPE_DEGREE = 0.1f;
    private static final float MAX_SWIPE_DEGREES = 0.8f * DEGREE_360;
    private static final float FULL_GROUP_ROTATION = 3.0f * DEGREE_360;
    private static final float MAX_ROTATION_INCREMENT = 0.25f * DEGREE_360;

    public void loading2() {
        mStatus = STATUS_LOADING2;
        mValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.setDuration(2000);
        mValueAnimator.setRepeatCount(Animation.INFINITE);
        mValueAnimator.setRepeatMode(Animation.RESTART);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float renderProgress = (float) animation.getAnimatedValue();
                if (renderProgress <= START_TRIM_DURATION_OFFSET) {
                    float startTrimProgress = renderProgress / START_TRIM_DURATION_OFFSET;
                    mStartDegrees = mOriginStartDegrees + MAX_SWIPE_DEGREES * MATERIAL_INTERPOLATOR.getInterpolation
                            (startTrimProgress);
                }

                if (renderProgress > START_TRIM_DURATION_OFFSET) {
                    float endTrimProgress = (renderProgress - START_TRIM_DURATION_OFFSET) / (END_TRIM_DURATION_OFFSET
                            - START_TRIM_DURATION_OFFSET);
                    mEndDegrees = mOriginEndDegrees + MAX_SWIPE_DEGREES * MATERIAL_INTERPOLATOR.getInterpolation
                            (endTrimProgress);

                }

                if (Math.abs(mEndDegrees - mStartDegrees) > MIN_SWIPE_DEGREE) {
                    mSwipeDegrees = mEndDegrees - mStartDegrees;
                }
                Log.i(TAG, "mStartDegrees: " + mStartDegrees + " mEndDegrees:" + mEndDegrees + " mSwipeDegrees:" +
                        mSwipeDegrees);
                mGroupRotation = ((FULL_GROUP_ROTATION / NUM_POINTS) * renderProgress) + (FULL_GROUP_ROTATION * (mRotationCount / NUM_POINTS));

                postInvalidate();
            }
        });
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
                mOriginEndDegrees = mEndDegrees;
                mOriginStartDegrees = mStartDegrees;
                mStartDegrees = mEndDegrees;
                mRotationCount = (mRotationCount + 1) % (NUM_POINTS);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mRotationCount = 0;
            }
        });
        mValueAnimator.start();
    }

    public void loading() {
        mStatus = STATUS_LOADING;
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
                setProgress(value);
                postInvalidate();
                //                if (value >= 0.3) {
                //                    if (mStartAngleAnimator != null) {
                //                        mStartAngleAnimator.start();
                //                    }
                //                }
            }
        });
        mEndAngleAnimator.addListener(new Animator.AnimatorListener() {
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
        mEndAngleAnimator.start();
        //        mLoadingAnimator = ValueAnimator.ofFloat(0, 1f);
        //        mLoadingAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        //        mLoadingAnimator.setDuration(2000);
        //        mLoadingAnimator.setRepeatCount(-1);
        //        mLoadingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        //            @Override
        //            public void onAnimationUpdate(ValueAnimator animation) {
        //                postInvalidate();
        //            }
        //        });
        //        mLoadingAnimator.addListener(new Animator.AnimatorListener() {
        //            @Override
        //            public void onAnimationStart(Animator animation) {
        //                mEndAngleAnimator.start();
        //            }
        //
        //            @Override
        //            public void onAnimationEnd(Animator animation) {
        //
        //            }
        //
        //            @Override
        //            public void onAnimationCancel(Animator animation) {
        //
        //            }
        //
        //            @Override
        //            public void onAnimationRepeat(Animator animation) {
        //
        //            }
        //        });
        //        mLoadingAnimator.start();
    }

    public void loadSuccess() {
        startCircleAnim();
    }

    private void startCircleAnim() {
        if (mStartAngleAnimator != null) {
            if (mStartAngleAnimator.isRunning())
                mStartAngleAnimator.cancel();
            mStartAngleAnimator.start();
            return;
        }
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
                startTickAnim();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mStartAngleAnimator.start();
    }

    private ValueAnimator mTickAnimator;

    private void startTickAnim() {
        mStatus = STATUS_TICK;
        mPath.reset();
        if (mTickAnimator != null) {
            if (mTickAnimator.isRunning()) {
                mTickAnimator.cancel();
            }
            mTickAnimator.start();
            return;
        }
        mTickAnimator = ValueAnimator.ofFloat(0f, 1f);
        mTickAnimator.setDuration(1000);
        mTickAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setTickPercent(value);
                postInvalidate();
            }
        });
        mTickAnimator.start();
    }

    public void cancel() {
        mValueAnimator.reverse();
    }
}
