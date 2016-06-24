package com.wusongyuan.customviewstudio.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.wusongyuan.customviewstudio.R;

/**********************
 * @author: wusongyuan
 * @date: 2016-06-24
 * @desc:
 **********************/
public class XfermodeStudioView extends View {

    private AvoidXfermode mAvoidXfermode;
    private PorterDuffXfermode mPorterDuffXfermode;
    private Resources mResources;

    private int mWidth;
    private int mHeight;

    private Bitmap mCirleBitmap;
    private int mBitmapWidth;
    private int mBitmapHeight;

    private Paint mBitmapPaint;
    private Paint mCanvasPaint;

    private Rect mSrcRect;
    private Rect mDstRect;
    private Rect mDynamicRect;

    public XfermodeStudioView(Context context) {
        super(context);
    }

    public XfermodeStudioView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mResources = getResources();
        initPaint();
        initBitmap();
        // 对蓝色相近的颜色进行替换
        mAvoidXfermode = new AvoidXfermode(Color.RED, 150, AvoidXfermode.Mode.TARGET);
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    }

    private void initPaint() {
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setFilterBitmap(true);

        mCanvasPaint = new Paint(mBitmapPaint);
        mCanvasPaint.setColor(Color.GRAY);
    }

    private void initBitmap() {

        mCirleBitmap = BitmapFactory.decodeResource(mResources, R.drawable.circle_500);
        mBitmapWidth = mCirleBitmap.getWidth();
        mBitmapHeight = mCirleBitmap.getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int savaLayoutCount = canvas.saveLayer(0, 0, mWidth, mHeight, mCanvasPaint, Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(mCirleBitmap, mSrcRect, mDstRect, mCanvasPaint);
        mCanvasPaint.setXfermode(mAvoidXfermode); // 第一次混合
        canvas.drawRect(mDstRect, mCanvasPaint);
        mCanvasPaint.setXfermode(mPorterDuffXfermode); // 第二次混合
        canvas.drawRect(mDynamicRect, mCanvasPaint);
        mCanvasPaint.setXfermode(null);

        canvas.restoreToCount(savaLayoutCount);

        mDynamicRect.top = mDynamicRect.top - 8;
        if (mDynamicRect.top < 0) {
            mDynamicRect.top = mDstRect.bottom;
        }
        postInvalidateDelayed(20);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mSrcRect = new Rect(0, 0, mBitmapWidth, mBitmapHeight);
        mDstRect = new Rect(0, 0, mBitmapWidth, mBitmapHeight);
        mDynamicRect = new Rect(mDstRect);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
