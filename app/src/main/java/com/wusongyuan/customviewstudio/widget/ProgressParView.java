package com.wusongyuan.customviewstudio.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.wusongyuan.customviewstudio.R;
import com.wusongyuan.customviewstudio.activity.ProgressActivity;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
    private static final int INVALIDATE = 0x01;
    private final List<Leaf> mLeafInfos;

    private RectF mArcRectF = new RectF();
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
    private static final long LEAF_ROTATE_TIME = 2000;

    private float mSpeed;
    private long mLeafFloatTime;
    private int mAddTime;
    private Random mRandom = new Random();

    private static InvalidateHandler mInvalidateHandler;


    public ProgressParView(Context context) {
        this(context, null);
    }

    public ProgressParView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mResources = getResources();
        initPaint();
        initBitmap();
        mLeafFloatTime = LEAF_FLOAT_TIME;

        mLeafInfos = new LeafFactory().generateLeafs();
        mInvalidateHandler = new InvalidateHandler();
        mInvalidateHandler.sendEmptyMessage(INVALIDATE);
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

        mArcRectF.set(new RectF(0, 0, 2 * mArcRadius, 2 * mArcRadius));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        long currentTime = System.currentTimeMillis();
        Log.i(TAG, "currentTime: " + currentTime );

        for (int i = 0; i < mLeafInfos.size(); i++) {
            Leaf leaf = mLeafInfos.get(i);
            if (currentTime > leaf.startTime && leaf.startTime != 0) {
                float x = mProgressWidth - mSpeed * (currentTime - leaf.startTime);
                float w = (float) ((float) 2 * Math.PI / mProgressWidth);
                float y = (float) (10 * Math.sin(w * x) + mArcRadius * 2 / 3.0f);

                Matrix matrix = new Matrix();
                matrix.setTranslate(x, y);
                float rotateFraction = ((currentTime - leaf.startTime) % LEAF_ROTATE_TIME)
                        / (float) LEAF_ROTATE_TIME;
                int angle = (int) (rotateFraction * 360);
                // 根据叶子旋转方向确定叶子旋转角度
                int rotate = leaf.rotateDirection == 0 ? angle + leaf.rotateAngle : -angle
                        + leaf.rotateAngle;
                matrix.postRotate(rotate, x
                        + mLeafWidth / 2, y + mLeafHeight / 2);
                canvas.drawBitmap(mLeafBitmap, matrix, mBitmapPaint);

                int intervalTime = (int) (currentTime - leaf.startTime);
                mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
                if (intervalTime > mLeafFloatTime) {
                    leaf.startTime = System.currentTimeMillis() + 100 + mRandom.nextInt((int) (mLeafFloatTime));
                }
            } else {
                continue;
            }
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


        canvas.save();
        // 风车旋转
        canvas.rotate(mRotateDegrees, mRotateDstRect.centerX(), mRotateDstRect.centerY());
        canvas.drawBitmap(mRotateBitmap, mRotateSrcRect, mRotateDstRect, mBitmapPaint);
        mRotateDegrees += -10;
        canvas.restore();
        postInvalidate();
    }

    private class Leaf {
        float x;
        float y;
        long startTime;
        int rotateAngle;
        int rotateDirection;
    }

    private class LeafFactory {
        private static final int MAX_LEAFS = 8;
        Random random = new Random();

        // 生成一个叶子信息
        public Leaf generateLeaf() {
            Leaf leaf = new Leaf();
            //            int randomType = random.nextInt(3);
            //            // 随时类型－ 随机振幅
            //            StartType type = StartType.MIDDLE;
            //            switch (randomType) {
            //                case 0:
            //                    break;
            //                case 1:
            //                    type = StartType.LITTLE;
            //                    break;
            //                case 2:
            //                    type = StartType.BIG;
            //                    break;
            //                default:
            //                    break;
            //            }
            //            leaf.type = type;
            // 随机起始的旋转角度
            leaf.rotateAngle = random.nextInt(360);
            // 随机旋转方向（顺时针或逆时针）
            leaf.rotateDirection = random.nextInt(2);
            // 为了产生交错的感觉，让开始的时间有一定的随机性
            mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
            leaf.startTime = System.currentTimeMillis() + mAddTime;
            mAddTime += random.nextInt((int) (mLeafFloatTime * 2));
            return leaf;
        }

        // 根据最大叶子数产生叶子信息
        public List<Leaf> generateLeafs() {
            return generateLeafs(MAX_LEAFS);
        }

        // 根据传入的叶子数量产生叶子信息
        public List<Leaf> generateLeafs(int leafSize) {
            List<Leaf> leafs = new LinkedList<Leaf>();
            for (int i = 0; i < leafSize; i++) {
                leafs.add(generateLeaf());
            }
            return leafs;
        }
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
    }

    private class InvalidateHandler extends android.os.Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == INVALIDATE) {
                postInvalidate();
                mInvalidateHandler.sendEmptyMessageDelayed(INVALIDATE, 40);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mInvalidateHandler.removeMessages(INVALIDATE);
    }
}
