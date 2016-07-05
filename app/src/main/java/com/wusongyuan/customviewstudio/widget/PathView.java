package com.wusongyuan.customviewstudio.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**********************
 * @author: wusongyuan
 * @date: 2016-07-05
 * @desc:
 **********************/
public class PathView extends View {

    Path mPath;
    Path mPath2;
    Paint mPaint;

    public PathView(Context context) {
        this(context, null);
    }

    public PathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPath = new Path();
        mPath2 = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        mPath.moveTo(50, 50);
        mPath.lineTo(100, 100);
        mPath.moveTo(200, 100);
        mPath.lineTo(200, 200);
        mPath.addArc(new RectF(200f, 200f, 300f, 400f), 0f, 90f);

        mPath2.lineTo(100, 300);
        mPath2.moveTo(200, 300);
        mPath2.lineTo(400, 300);
        mPath2.setLastPoint(400, 400);
        mPath.addPath(mPath2);
        canvas.drawPath(mPath, mPaint);

        mPath.reset();
        canvas.translate(100, 400);
        mPath.moveTo(100, 100);
        mPath.lineTo(300, 100);
        mPath.lineTo(300, 400);
        mPath.lineTo(100, 400);
        mPath.close();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.BLUE);
        mPath.offset(200, 0);
        mPath.rLineTo(-100, 10);

        canvas.drawPath(mPath, mPaint);

        //        canvas.drawPath(mPath2, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
