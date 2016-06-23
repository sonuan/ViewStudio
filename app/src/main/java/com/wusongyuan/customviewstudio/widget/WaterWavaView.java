package com.wusongyuan.customviewstudio.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.view.View;

/**********************
 * @author: wusongyuan
 * @date: 2016-06-23
 * @desc: 水波纹
 **********************/
public class WaterWavaView extends View {

    private int mWidth;
    private int mHeight;

    public static final int WAVA_COLOR = 0x880000aa;


    private float[] mYPositions;

    private float[] mResetOneYPositions;
    private float[] mResetTwoYPositions;
    private Paint mWavaPaint;

    private int mHalfHeight;
    private DrawFilter mDrawFilter;

    private int mOneOffsetX;
    private int mTwoOffsetX;

    // 第一条水波移动速度
    private static final int TRANSLATE_X_SPEED_ONE = 7;
    private static final int TRANSLATE_X_SPEED_TWO = 10;

    public WaterWavaView(Context context) {
        this(context, null);
    }

    public WaterWavaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();

    }

    private void initPaint() {
        mWavaPaint = new Paint();
        mWavaPaint.setAntiAlias(true);
        mWavaPaint.setStyle(Paint.Style.FILL);
        mWavaPaint.setColor(WAVA_COLOR);
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(mDrawFilter);
        // mXOneOffset代表当前第一条水波纹要移动的距离
        int yOneInterval = mYPositions.length - mOneOffsetX;
        // 使用System.arraycopy方式重新填充第一条波纹的数据
        System.arraycopy(mYPositions, mOneOffsetX, mResetOneYPositions, 0, yOneInterval);
        System.arraycopy(mYPositions, 0, mResetOneYPositions, yOneInterval, mOneOffsetX);

        int yTwoInterval = mYPositions.length - mTwoOffsetX;
        // 使用System.arraycopy方式重新填充第一条波纹的数据
        System.arraycopy(mYPositions, mTwoOffsetX, mResetTwoYPositions, 0, yTwoInterval);
        System.arraycopy(mYPositions, 0, mResetTwoYPositions, yTwoInterval, mTwoOffsetX);

        for (int i = 0; i < mWidth; i++) {
            canvas.drawLine(i, mHeight - mResetOneYPositions[i] - mHalfHeight, i, mHeight, mWavaPaint);

            canvas.drawLine(i, mHeight - mResetTwoYPositions[i] - mHalfHeight, i, mHeight, mWavaPaint);
        }

        mOneOffsetX += TRANSLATE_X_SPEED_ONE;

        if (mOneOffsetX >= mWidth) {
            mOneOffsetX = 0;
        }

        postInvalidateDelayed(20);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        mHalfHeight = mHeight / 2;

        // 用于保存原始波纹的y值
        mYPositions = new float[mWidth];
        // 用于保存波纹一的y值
        mResetOneYPositions = new float[mWidth];
        mResetTwoYPositions = new float[mWidth];
        // 将周期定为view总宽度
        float mCycleFactorW = (float) (2 * Math.PI / mWidth);
        for (int i = 0; i < mWidth; i++) {
            mYPositions[i] = (float) (20 * Math.sin(mCycleFactorW * i));
        }


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


}
