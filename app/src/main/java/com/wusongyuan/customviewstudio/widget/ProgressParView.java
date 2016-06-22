package com.wusongyuan.customviewstudio.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.wusongyuan.customviewstudio.R;
import com.wusongyuan.customviewstudio.activity.ProgressActivity;

/**********************
 * @author: wusongyuan
 * @date: 2016-06-21
 * @desc:
 **********************/
public class ProgressParView extends View {

    // 橙色
    private static final int ORANGE_COLOR = 0xffffa800;
    private static final int MAX_PROGREE = 100;
    private static final String TAG = ProgressActivity.class.getSimpleName();

    private RectF mRectF = new RectF();
    private RectF mArcRectF = new RectF();
    private Rect mRect = new Rect();
    private Rect mBitmapSrcRect = new Rect();
    private Rect mBitmapDstRect = new Rect();

    private Paint mBitmapPaint = new Paint();
    private Paint mProgressPaint = new Paint();


    private int mArcRadius;

    private int mHeight;
    private int mWidth;

    private Resources mResources;

    private Bitmap mLeafBitmap;
    private Bitmap mOuterBitmap;
    private Bitmap mRotateBitmap;

    private int mLeafWidth;
    private int mLeafHeight;

    private int mOuterWidth;
    private int mOuterHeight;

    private int mRotateWidth;
    private int mRotateHeight;

    private int mProgress = 0;
    private int mProgressWidth = 0;

    private int mCurrentProgressPosition;
    private Rect mRotateSrcRect;
    private Rect mRotateDstRect;
    private float mRotateDegrees;

    // 叶子飘动一个周期所花的时间
    private static final long LEAF_FLOAT_TIME = 3000;
    private long mLeafStartTime;
    private float mSpeed;

    public ProgressParView(Context context) {
        this(context, null);
    }

    public ProgressParView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mResources = getResources();
        initPaint();
        initBitmap();


    }

    private void initPaint() {
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);

        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setColor(ORANGE_COLOR);
    }

    private void initBitmap() {
        mLeafBitmap = BitmapFactory.decodeResource(mResources, R.drawable.leaf);
        mLeafWidth = mLeafBitmap.getWidth();
        mLeafHeight = mLeafBitmap.getHeight();

        mOuterBitmap = BitmapFactory.decodeResource(mResources, R.drawable.leaf_kuang);
        mOuterWidth = mOuterBitmap.getWidth();
        mOuterHeight = mOuterBitmap.getHeight();

        mRotateBitmap = BitmapFactory.decodeResource(mResources, R.drawable.fengshan);
        mRotateWidth = mRotateBitmap.getWidth();
        mRotateHeight = mRotateBitmap.getHeight();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mArcRadius = mHeight / 2;

        mProgressWidth = mWidth - mArcRadius;
        mSpeed = (mProgressWidth / (float) LEAF_FLOAT_TIME);

        mBitmapSrcRect = new Rect(0, 0, mOuterWidth, mOuterHeight);
        mBitmapDstRect = new Rect(0, 0, mWidth, mHeight);

        mRotateSrcRect = new Rect(0, 0, mRotateWidth, mRotateHeight);
        mRotateDstRect = new Rect(mWidth - mRotateWidth - 8, 8, mWidth - 8, mHeight - 8);
        int top = getPaddingTop();
        int bottom = mWidth - getPaddingBottom();
        int left = getPaddingLeft();
        int right = mWidth - getPaddingRight();
        mRectF.set(new RectF(left, top, right, bottom));
        mRect.set(new Rect(left, top, right, bottom));
        mArcRectF.set(new RectF(0, 0, 2 * mArcRadius, 2 * mArcRadius));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLeafStartTime == 0) {
            mLeafStartTime = System.currentTimeMillis();
        }
        mCurrentProgressPosition = mProgressWidth * mProgress / MAX_PROGREE;

        if (mCurrentProgressPosition < mArcRadius) {
            int angle = (int) Math.toDegrees(Math.acos((mArcRadius - mCurrentProgressPosition) / (float) mArcRadius));
            int startAngle = 180 - angle;
            canvas.drawArc(mArcRectF, startAngle, 2 * angle, false, mProgressPaint);
        } else {
            canvas.drawArc(mArcRectF, 90, 180, false, mProgressPaint);
            canvas.drawRect(mArcRadius, 0, mCurrentProgressPosition, mHeight, mProgressPaint);
        }
        canvas.drawBitmap(mOuterBitmap, mBitmapSrcRect, mBitmapDstRect, mBitmapPaint);

        canvas.drawBitmap(mLeafBitmap, mProgressWidth - mSpeed * (System.currentTimeMillis() - mLeafStartTime),
                (mHeight - mLeafHeight) / 2.0f, mBitmapPaint);
        // 风车旋转
        canvas.rotate(mRotateDegrees, mRotateDstRect.centerX(), mRotateDstRect.centerY());
        canvas.drawBitmap(mRotateBitmap, mRotateSrcRect, mRotateDstRect, mBitmapPaint);
        mRotateDegrees += -10;

        Log.i(TAG, "onDraw: ");
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mOuterWidth, mOuterHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void setProgress(int progress) {
        mProgress = progress;
        postInvalidate();
    }
}
