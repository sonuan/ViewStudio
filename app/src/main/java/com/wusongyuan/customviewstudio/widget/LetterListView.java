package com.wusongyuan.customviewstudio.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**********************
 * @author: wusongyuan
 * @date: 2016-03-19
 * @desc: 通讯录字母索引View
 **********************/
public class LetterListView extends View {

    private static final String TAG = LetterListView.class.getSimpleName();
    private String[] mLetterArray = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "L", "J", "K", "M", "N", "O", "P", "Q",
            "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private Paint mPaint = new Paint();
    private int mCurrentIndex = -1; //当前数组的Index
    private boolean mIsDown = false;//是否触发MotionEvent.ACTION_DOWN

    public LetterListView(Context context) {
        super(context);
    }

    public LetterListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //-------设置画笔基本属性--------
        mPaint.setTextSize(24);
        mPaint.setColor(Color.parseColor("#ff323232"));
        mPaint.setAntiAlias(true);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        //-----------------------------
        // 该View的width
        int width = getWidth();
        // 每个字母的高度, 由屏幕的高度/字母数量
        int singleHeight = getHeight() / mLetterArray.length;

        //如果View触发MotionEvent.ACTION_DOWN,则改变该View背景颜色
        if (mIsDown) {
            canvas.drawColor(Color.parseColor("#40000000"));
        }
        for (int i = 0; i < mLetterArray.length; i++) {
            // 字母X轴位置, View一半再减去字母的一半,是为了字母居中
            float x = width / 2 - mPaint.measureText(mLetterArray[i]) / 2;
            //字母Y轴位置
            float y = singleHeight * i + singleHeight;
            // View触发MotionEvent.ACTION_DOWN, 并且为当前位置的字母设置选中效果
            if (mIsDown && mCurrentIndex == i) {
                mPaint.setColor(Color.parseColor("#ff3399ff"));
            } else {
                mPaint.setColor(Color.parseColor("#ff323232"));
            }
            canvas.drawText(mLetterArray[i], x, y, mPaint);
        }
    }

    /**
     * 自定义View时,最好加上该方法,便于在xml视图中显示出效果
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float y = event.getY();
        //根据触点y与View的height的百分比,计算出在字母数组的index,用于得出具体的字母
        int index = (int) (y / getHeight() * mLetterArray.length);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsDown = true; // 用户改变View的背景颜色
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                //判断选中的index位置, 只有在数组范围内,才能执行
                if (mCurrentIndex != index && 0 <= index && index < mLetterArray.length) {
                    mCurrentIndex = index; //当前数组index替换为最新的index
                    invalidate();
                    //打印选中的字母,如果需回调,则加在此处
                    Log.i(TAG, "dispatchTouchEvent: currentIndex----->" + mLetterArray[mCurrentIndex]);
                }
                break;
            case MotionEvent.ACTION_UP:
                mIsDown = false;
                mCurrentIndex = -1;
                invalidate();
                break;
        }
        return true;
    }
}
