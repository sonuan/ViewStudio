package com.wusongyuan.customviewstudio.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Scroller;

import com.wusongyuan.customviewstudio.R;

/**
 * @author wusongyuan
 * @date 2017.01.16
 * @desc
 */

public class NestingScrollParentLayout extends ViewGroup {

    private static final String TAG = "NestingScrollParentLayo";

    private View mHeaderView;
    private ScrollView mTargetView;
    private int mHeaderViewId;
    private int mTargetViewId;
    private int sOffsetY = 300;
    private int mHeaderCurrentOffset;
    private int mTargetCurrentOffset;

    private int mTargetEndOffset = 0;

    private float mStartY;
    private float mLastY;

    private Scroller mScroller;

    private boolean mIsDragging;

    public NestingScrollParentLayout(Context context) {
        this(context, null);
    }

    public NestingScrollParentLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestingScrollParentLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NestingScrollParentLayout, 0, 0);
            mHeaderViewId = typedArray.getResourceId(R.styleable.NestingScrollParentLayout_nsp_header_view, 0);
            mTargetViewId = typedArray.getResourceId(R.styleable.NestingScrollParentLayout_nsp_target_view, 0);
            typedArray.recycle();
        }
        mHeaderCurrentOffset = sOffsetY;
        mTargetCurrentOffset = sOffsetY;
        mScroller = new Scroller(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mHeaderViewId != 0) {
            mHeaderView = findViewById(mHeaderViewId);
        }

        if (mTargetViewId != 0) {
            mTargetView = (ScrollView) findViewById(mTargetViewId);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() <= 0) {
            return;
        }
        mHeaderView.layout(l, t, r, t + sOffsetY);
        mTargetView.layout(l, t + sOffsetY, r, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
        //measureChild(mTargetView, widthMeasureSpec, heightMeasureSpec);

        int measureWidthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
        int measureHeightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
        mTargetView.measure(measureWidthSpec, measureHeightSpec);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        if (mTargetView.getScrollY() > 0) {
            return false;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartY = event.getX();
                mIsDragging = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                Log.i(TAG, "onInterceptTouchEvent: y > mStartY " + (y > mStartY ));
                if (y > mStartY || mTargetCurrentOffset > 0) {
                    if (!mIsDragging) {
                        mIsDragging = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        Log.i(TAG, "onInterceptTouchEvent: mIsDragging:" + mIsDragging);
        return mIsDragging;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsDragging = false;
                mStartY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                if (y > mStartY || mTargetCurrentOffset > 0) {
                    if (!mIsDragging) {
                        mIsDragging = true;
                        mLastY = y;
                    }
                }
                if (mIsDragging) {
                    int dy = (int) (y - mLastY);
                    if (dy >= 0) {
                        moveTargetView(dy);
                    } else {
                        if (mTargetCurrentOffset + dy <= 0) {
                            moveTargetView(dy);
                            int oldAction = event.getAction();
                            event.setAction(MotionEvent.ACTION_DOWN);
                            dispatchTouchEvent(event);
                            event.setAction(oldAction);
                        } else {
                            moveTargetView(dy);
                        }
                    }
                    mLastY = y;
                }
                break;
            case MotionEvent.ACTION_UP:
                mLastY = 0;
                mIsDragging = false;
                break;
        }
        return true;
    }

    private void moveTargetView(float dy) {
        int target = (int) (mTargetCurrentOffset + dy);
        moveTargetViewTo(target);
    }
    private void moveTargetViewTo(int target) {
        if (target <= mTargetEndOffset) {
            target = Math.max(target, mTargetEndOffset);
        } else if (target >= sOffsetY) {
            target = Math.min(target, sOffsetY);
        }
        int dy = target - mTargetCurrentOffset;
        Log.i(TAG, "moveTargetViewTo: dy:" + dy + " target:" + target);
        ViewCompat.offsetTopAndBottom(mTargetView, dy);
        ViewCompat.offsetTopAndBottom(mHeaderView, dy);
        mTargetCurrentOffset = target;
    }
}
