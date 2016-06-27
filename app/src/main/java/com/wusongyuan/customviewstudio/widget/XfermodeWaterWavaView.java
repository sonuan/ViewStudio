package com.wusongyuan.customviewstudio.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.wusongyuan.customviewstudio.R;

/**********************
 * @author: wusongyuan
 * @date: 2016-06-24
 * @desc:
 **********************/
public class XfermodeWaterWavaView extends View {

    private static final String TAG = XfermodeWaterWavaView.class.getSimpleName();
    private PorterDuffXfermode mPorterDuffXfermode;
    private int mWidth;
    private int mHeight;

    private Paint mBitmapPaint;
    private Paint mCanvasPaint;

    private Resources mResources;
    private Bitmap mCirleBitmap;
    private Bitmap mWavaBitmap;
    private int mBitmapWidth;
    private int mBitmapHeight;

    private Rect mSrcRect;
    private Rect mDstRect;

    private Rect mWavaSrcRect;
    private Rect mWavaDstRect;
    private int mWavaWidth;
    private int mWavaHeight;

    private PaintFlagsDrawFilter mDrawFilter;

    private int mCurrentPosition;
    private int mHalfWidth;
    private boolean mIsExit = false;

    public XfermodeWaterWavaView(Context context) {
        this(context, null);
    }

    public XfermodeWaterWavaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_NONE, null); // 注意此设置, 不然当mCurrentPosition >= mWavaWidth时,图片会出现空白块
        mResources = getResources();
        initPaint();
        initBitmap();
        // 对蓝色相近的颜色进行替换
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        mDrawFilter = new PaintFlagsDrawFilter(Paint.ANTI_ALIAS_FLAG, Paint.DITHER_FLAG);
        new Thread() {
            public void run() {
                while (!mIsExit) {
                    // 不断改变绘制的波浪的位置
                    mCurrentPosition += 10;
                    if (mCurrentPosition >= mWavaWidth) {
                        mCurrentPosition = 0;
                    }
                    try {
                        // 为了保证效果的同时，尽可能将cpu空出来，供其他部分使用
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                    }

                    postInvalidate();
                }

            };
        }.start();
    }

    private void initPaint() {
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setFilterBitmap(true);

        mCanvasPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCanvasPaint.setDither(true);
        mCanvasPaint.setColor(Color.RED);
    }

    private void initBitmap() {
        mWavaBitmap = BitmapFactory.decodeResource(mResources, R.drawable.wave_2000);
        mWavaWidth = mWavaBitmap.getWidth();
        mWavaHeight = mWavaBitmap.getHeight();

        mCirleBitmap = BitmapFactory.decodeResource(mResources, R.drawable.circle_500);
        mBitmapWidth = mCirleBitmap.getWidth();
        mBitmapHeight = mCirleBitmap.getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(mDrawFilter);
        canvas.drawColor(Color.TRANSPARENT);

        int savaLayoutCount = canvas.saveLayer(0, 0, mWidth, mHeight, null, Canvas.ALL_SAVE_FLAG);
        mWavaSrcRect.set(mCurrentPosition, 0, mCurrentPosition + mHalfWidth, mHeight);
        canvas.drawBitmap(mWavaBitmap, mWavaSrcRect, mWavaDstRect, mCanvasPaint);
        mCanvasPaint.setXfermode(mPorterDuffXfermode);
        canvas.drawBitmap(mCirleBitmap, mSrcRect, mDstRect, mCanvasPaint);
        mCanvasPaint.setXfermode(null);
        canvas.restoreToCount(savaLayoutCount);

//        mCurrentPosition += 10;
//        if (mCurrentPosition >= mWavaWidth) {
//            mCurrentPosition = 0;
//        }
//
//        postInvalidateDelayed(20);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mHalfWidth = mWidth / 2;
        Log.i(TAG, "onSizeChanged: width:" + mWidth + " height:" + mHeight);
        mSrcRect = new Rect(0, 0, mBitmapWidth, mBitmapHeight);
        mDstRect = new Rect(0, 0, mWidth, mHeight);

        mWavaSrcRect = new Rect(0, 0, mBitmapWidth, mBitmapHeight);
        mWavaDstRect = new Rect(0, 0, mWidth, mHeight);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsExit = true;
    }
}
