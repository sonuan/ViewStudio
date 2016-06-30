package com.wusongyuan.customviewstudio.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.wusongyuan.customviewstudio.R;

/**********************
 * @author: wusongyuan
 * @date: 2016-06-28
 * @desc:
 **********************/
public class ShipGiftView extends View {
    private int mWidth;
    private int mHeight;

    private Bitmap mBitmap;

    private int mWavaWidth = 1540;
    private int mWavaHeight = 512;

    private int mShipWidth = 624;
    private int mShipHeight = 307;

    private Rect mWavaSrcRect;
    private Rect mWavaDstRect;

    private Rect mShipSrcRect;
    private Rect mShipDstRect;

    private Paint mPaint;
    private DrawFilter mDrawFilter;

    private int mCurrentPosition;
    private boolean mIsExit;

    public ShipGiftView(Context context) {
        this(context, null);
    }

    public ShipGiftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!mIsExit) {
                    mCurrentPosition += 10;
                    if (mCurrentPosition >= mWavaWidth) {
                        mCurrentPosition = 0;
                    }
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (mCurrentPosition == 0) {
                        mIsExit = true;
                    }
                    postInvalidate();
                }
            }
        }).start();
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mDrawFilter = new PaintFlagsDrawFilter(Paint.ANTI_ALIAS_FLAG, Paint.DITHER_FLAG);
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pic_ship);
        Log.i("init", "init: " + mBitmap.getWidth() + " " + mBitmap.getHeight() + " " + mBitmap.getScaledHeight
                (context.getResources().getDisplayMetrics()) + " " + TypedValue.applyDimension(TypedValue
                .COMPLEX_UNIT_DIP, mBitmap.getHeight(), context.getResources().getDisplayMetrics()));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(mDrawFilter);
        canvas.drawColor(Color.TRANSPARENT);
        mWavaSrcRect.set(mCurrentPosition, 0, mCurrentPosition + mWidth, mHeight);
        canvas.drawBitmap(mBitmap, mWavaSrcRect, mWavaDstRect, mPaint);
        canvas.drawBitmap(mBitmap, mShipSrcRect, mShipDstRect, mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        mWavaSrcRect = new Rect(0, 0, mWidth, mWavaHeight);
        mWavaDstRect = new Rect(0, 0, mWidth, mHeight);
        mShipSrcRect = new Rect(mWavaWidth, 0, mWavaWidth + mShipWidth, mShipHeight);
        mShipDstRect = new Rect(0, 0, mShipWidth / 2, mShipHeight / 2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsExit = true;
    }
}
