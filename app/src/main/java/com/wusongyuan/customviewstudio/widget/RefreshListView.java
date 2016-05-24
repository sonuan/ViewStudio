package com.wusongyuan.customviewstudio.widget;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

import com.wusongyuan.customviewstudio.R;

/**********************
 * @author: wusongyuan
 * @date: 2016-05-17
 * @desc:
 **********************/
@SuppressWarnings("ResourceType")
public class RefreshListView extends ListView {

    private static final String TAG = RefreshListView.class.getSimpleName();
    /**
     * 偏移系数
     */
    private final static int RATIO = 3;
    /**
     * 动画加速器
     */
    static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();

    /**
     * 是否允许刷新
     */
    private boolean isRefreshable = true;
    /**
     * 是否正在刷新
     */
    private boolean isRefreshing = false;
    /**
     * 是否允许加载更多
     */
    private boolean isLoadMoreable = false;

    public boolean isLoadMoreable() {
        return isLoadMoreable;
    }

    public void setLoadMoreable(boolean isLoadMoreable) {
        this.isLoadMoreable = isLoadMoreable;
        if (!isLoadMoreable) {
            onLoadMoreSuccess();
        } else {
            if (mOnLoadMoreListener != null) {
                try {
                    removeFooterView(mViewFooter);
                    mProgressFooter.setVisibility(View.VISIBLE);
                    mViewFooterFailure.setVisibility(View.GONE);
                    addFooterView(mViewFooter, null, false);
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 是否正在加载更多
     */
    private boolean isLoadMoreing = false;

    /**
     * 当前列表状态
     */
    private State mState;
    /**
     * 列表移动初始位置
     */
    private int mStartY = -1;

    /**
     * 下拉刷新控件
     */
    private LinearLayout mViewHeader;
    private ImageView mIVHeader;
    private ProgressBar mProgressHeader;
    private TextView mTVHeader;
    private int mHeaderHeight;
    private int mCurrHeaderHeight;


    /**
     * 加载更多控件
     */
    private LinearLayout mViewFooter;
    private ProgressBar mProgressFooter;
    /**
     * 加载更多失败提示控件
     */
    private View mViewFooterFailure;

    /**
     * 箭头翻转动画
     */
    private Animation mFlipAnimation;
    /**
     * 箭头重置动画
     */
    private Animation mResetAnimation;
    /**
     * 刷新中动画
     */
    private Animation mHeaderAnimation;
    /**
     * 加载更多动画
     */
    private Animation mFooterAnimation;

    /**
     * 列表滑动状态监听
     */
    private OnScrollListener mOnScrollListener;
    /**
     * 列表项点击监听
     */
    private OnItemClickListener mOnItemClickListener;
    /**
     * 列表项长点击监听
     */
    private OnItemLongClickListener mOnItemLongClickListener;
    /**
     * 列表项选择监听
     */
    private OnItemSelectedListener mOnItemSelectedListener;

    /**
     * 下拉刷新监听
     */
    private OnRefreshListener mOnRefreshListener;
    /**
     * 加载更多监听
     */
    private OnLoadMoreListener mOnLoadMoreListener;

    private final static int SCROLL_DURATION = 400; // scroll back duration

    private Scroller mScroller;

    /**
     * 构造方法
     *
     * @param context 设备上下文环境
     * @param attrs   配置信息
     */
    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 初始化基本配置
     *
     * @param context 设备上下文环境
     */
    private void init(Context context) {
        mScroller = new Scroller(context);

        // 翻转动画
        mFlipAnimation = new RotateAnimation(0, -180,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mFlipAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
        mFlipAnimation.setDuration(150);
        mFlipAnimation.setFillAfter(true);
        // 翻转重置动画
        mResetAnimation = new RotateAnimation(-180, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mResetAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
        mResetAnimation.setDuration(150);
        mResetAnimation.setFillAfter(true);
        // 刷新中动画
        mHeaderAnimation = new RotateAnimation(0, 720,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mHeaderAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
        mHeaderAnimation.setDuration(1200);
        mHeaderAnimation.setRepeatCount(Animation.INFINITE);
        mHeaderAnimation.setRepeatMode(Animation.RESTART);

        // 加载更多动画
        mFooterAnimation = new RotateAnimation(0, 720,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mFooterAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
        mFooterAnimation.setDuration(1200);
        mFooterAnimation.setRepeatCount(Animation.INFINITE);
        mFooterAnimation.setRepeatMode(Animation.RESTART);

        mState = State.RESET;
        super.setOnScrollListener(mOnInnerScrollListener);
    }

    /**
     * 初始化下拉刷新控件
     *
     * @param context 设备上下文环境
     */
    private void initHeaderLayout(Context context) {
        FrameLayout.LayoutParams params;

        mViewHeader = new LinearLayout(context);

        // 拉动刷新状态图片展示
        FrameLayout fl = new FrameLayout(context);

        // 图片缩放配置
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inDensity = 240;
        opts.inTargetDensity = getResources().getDisplayMetrics().densityDpi;

        // 箭头图片
        mIVHeader = new ImageView(context);
        params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mIVHeader.setLayoutParams(params);
        mIVHeader.setImageResource(R.drawable.ptr_flip);

        // 载入中图片
        mProgressHeader = new ProgressBar(context);
        mProgressHeader.setVisibility(INVISIBLE);
        params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mProgressHeader.setLayoutParams(params);

        fl.addView(mIVHeader);
        fl.addView(mProgressHeader);

        // 刷新状态文字
        mTVHeader = new TextView(context);
        mTVHeader.setTextAppearance(context, android.R.attr.textAlignment);
        mTVHeader.setSingleLine();

        // 添加拉动刷新视图
        FrameLayout innerLayout = new FrameLayout(context);
        innerLayout.setPadding(24, 12, 24, 12);
        params = new FrameLayout.LayoutParams(-2, -2);
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
        innerLayout.addView(fl, params);
        params = new FrameLayout.LayoutParams(-2, -2);
        params.gravity = Gravity.CENTER;
        innerLayout.addView(mTVHeader, params);

        mViewHeader.addView(innerLayout, -1, -2);
        mViewHeader.measure(0, 0);
        mHeaderHeight = mViewHeader.getMeasuredHeight();
        mViewHeader.setPadding(0, -1 * mHeaderHeight, 0, 0);

        addHeaderView(mViewHeader, null, false);
    }

    /**
     * 初始化加载更多控件
     *
     * @param context 设备上下文环境
     */
    private void initFooterLayout(Context context) {
        mViewFooter = new LinearLayout(context);
        mViewFooter.setGravity(Gravity.CENTER);

        // 图片缩放配置
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inDensity = 240;
        opts.inTargetDensity = getResources().getDisplayMetrics().densityDpi;

        mProgressFooter = new ProgressBar(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mProgressFooter.setLayoutParams(params);

        if (mViewFooterFailure == null) {
            // 默认加载更多失败提示信息
            TextView tvTip = new TextView(context);
            tvTip.setText("加载失败，点击重试");
            tvTip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            tvTip.setTextColor(0xFF000000);
            tvTip.setGravity(Gravity.CENTER);
            tvTip.setPadding(10, 10, 10, 10);
            tvTip.setVisibility(View.GONE);
            tvTip.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    onLoadMore();
                }
            });
            mViewFooterFailure = tvTip;
        }

        mViewFooter.addView(mProgressFooter);
        mViewFooter.addView(mViewFooterFailure);
        mViewFooter.setPadding(10, 10, 10, 10);

        if (mViewHeader == null) {
            addFooterView(mViewFooter);
            removeFooterView(mViewFooter);
        }
    }

    /**
     * 设置加载更多加载失败显示内容
     *
     * @param resource View资源
     */
    public void setLoadFailureView(int resource) {
        View view = LayoutInflater.from(getContext()).inflate(resource, null);
        setLoadFailureView(view);
    }

    /**
     * 设置加载更多加载失败显示内容
     *
     * @param view View资源
     */
    public void setLoadFailureView(View view) {
        if (mViewFooter != null) {
            mViewFooter.removeView(mViewFooterFailure);
            mViewFooter.addView(view);
        }
        mViewFooterFailure = view;
        mViewFooterFailure.setVisibility(View.GONE);
        mViewFooterFailure.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onLoadMore();
            }
        });
    }

    /**
     * 手动实现下拉刷新
     */
    public void refresh() {
        setState(State.REFRESHING);
        onRefresh();
    }

    /**
     * 设置下拉刷新监听
     *
     * @param listener 下拉刷新监听器
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        mOnRefreshListener = listener;
        if (mOnRefreshListener != null) {
            initHeaderLayout(getContext());
        }
    }

    /**
     * 设置加载更多监听
     *
     * @param listener 加载更多监听器
     */
    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mOnLoadMoreListener = listener;
        if (mOnLoadMoreListener != null) {
            initFooterLayout(getContext());
        }
    }

    /**
     * 设置列表滚动监听器
     *
     * @param listener 滚动监听器
     */
    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        mOnScrollListener = listener;
    }

    private OnScrollListener mOnInnerScrollListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // 判断是否调用加载更多
            if (getLastVisiblePosition() == getCount() - 1 && !isRefreshing && !isLoadMoreing
                    && isLoadMoreable && scrollState == SCROLL_STATE_IDLE) {
                onLoadMore();
            }

            if (mOnScrollListener != null) {
                mOnScrollListener.onScrollStateChanged(view, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            // 判断是否允许下拉刷新
            isRefreshable = firstVisibleItem == 0;
            // 判断是否调用加载更多
            if (visibleItemCount == totalItemCount && !isRefreshing && !isLoadMoreing
                    && isLoadMoreable && !isRefreshing) {
                onLoadMore();
            }

            if (mOnScrollListener != null) {
                mOnScrollListener.onScroll(view, firstVisibleItem,
                        visibleItemCount, totalItemCount);
            }
        }
    };

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
        super.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mOnItemClickListener.onItemClick(parent, view, position
                        - getHeaderViewsCount(), id);
            }
        });
    }

    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
        super.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                return mOnItemLongClickListener.onItemLongClick(parent, view,
                        position - getHeaderViewsCount(), id);
            }
        });
    }

    @Override
    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mOnItemSelectedListener = listener;
        super.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                mOnItemSelectedListener.onItemSelected(parent, view, position
                        - getHeaderViewsCount(), id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mOnItemSelectedListener.onNothingSelected(parent);
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isRefreshing) {
            mProgressHeader.setVisibility(View.VISIBLE);
        }
        if (isLoadMoreing) {
            mProgressFooter.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mOnRefreshListener == null) {
            return super.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isRefreshable && mStartY == -1) {// 设置初始位置
                    mStartY = (int) event.getY();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mState != State.REFRESHING) {// 设置当前状态
                    if (mState == State.PULL_TO_REFRESH) {
                        setState(State.RESET);
                    }
                    if (mState == State.RELEASE_TO_REFRESH) {
                        setState(State.REFRESHING);
                        onRefresh();
                    }
                }
                mStartY = -1;
                break;
            case MotionEvent.ACTION_MOVE:
                int tempY = (int) event.getY();
                if (mStartY == -1 && isRefreshable) {// 设置初始位置
                    mStartY = tempY;
                }
                if (mState != State.REFRESHING && mStartY != -1) {
                    if (mState == State.RELEASE_TO_REFRESH) {
                        setSelection(0);
                        if (((tempY - mStartY) / RATIO < mHeaderHeight)
                                && (tempY - mStartY) > 0) {
                            setState(State.PULL_TO_REFRESH);
                        } else if (tempY - mStartY <= 0) {
                            setState(State.RESET);
                        }
                    }
                    if (mState == State.PULL_TO_REFRESH) {
                        setSelection(0);
                        if ((tempY - mStartY) / RATIO >= mHeaderHeight) {
                            setState(State.RELEASE_TO_REFRESH);
                        } else if (tempY - mStartY <= 0) {
                            setState(State.RESET);
                        }
                    }
                    if (mState == State.RESET) {
                        if (tempY - mStartY > 0) {
                            setState(State.PULL_TO_REFRESH);
                        }
                    }
                    if (mState == State.PULL_TO_REFRESH) {
                        scrollToRefresh(-1 * mHeaderHeight + (tempY - mStartY)
                                / RATIO);
                    }
                    if (mState == State.RELEASE_TO_REFRESH) {
                        scrollToRefresh((tempY - mStartY) / RATIO - mHeaderHeight);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 下拉刷新滚动到指定位置
     *
     * @param scrollY 指定位置
     */
    private final void scrollToRefresh(int scrollY) {
        if (mViewHeader == null) {
            return;
        }
        mCurrHeaderHeight = mHeaderHeight + scrollY;
        mViewHeader.setPadding(0, scrollY, 0, 0);
    }

    @Override
    public void computeScroll() {
        // 判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {
            mCurrHeaderHeight = mScroller.getCurrY();
            mViewHeader.setPadding(0, mScroller.getCurrY() - mHeaderHeight, 0, 0);
            postInvalidate();
        }
        super.computeScroll();
    }

    /**
     * 根据状态变更下拉刷新控件内容
     *
     * @param state 当前状态
     */
    private void setState(State state) {
        mState = state;
        switch (mState) {
            case RESET:// 重置
                reset();
                break;
            case PULL_TO_REFRESH:// 下拉刷新
                pullToRefresh();
                break;
            case RELEASE_TO_REFRESH:// 释放刷新
                releaseToRefresh();
                break;
            case REFRESHING:// 刷新中
                refreshing();
                break;
        }
    }

    /**
     * 重置
     */
    private final void reset() {
        if (mViewHeader == null) {
            return;
        }
        mIVHeader.clearAnimation();
        mProgressHeader.setVisibility(View.INVISIBLE);
        resetHeaderHeight();
    }

    private void resetHeaderHeight() {
        if (mHeaderHeight == 0) // not visible.
            return;
        // refreshing and header isn't shown fully. do nothing.
        if (isRefreshing && mCurrHeaderHeight <= mHeaderHeight) {
            return;
        }
        int finalHeight = 0;
        if (isRefreshing && mCurrHeaderHeight > mHeaderHeight) {
            finalHeight = mHeaderHeight;
        }
        Log.i("resetHeaderHeight", "currHeaderHeight" + mCurrHeaderHeight + "  finalHeight" + finalHeight + " " +
                "mHeaderHeight" + mHeaderHeight);

        mScroller.startScroll(0, mCurrHeaderHeight, 0, finalHeight - mCurrHeaderHeight,
                SCROLL_DURATION);
        invalidate();
    }

    /**
     * 下拉刷新
     */
    private final void pullToRefresh() {
        if (mViewHeader == null) {
            return;
        }
        if (mFlipAnimation == mIVHeader.getAnimation()) {
            mIVHeader.startAnimation(mResetAnimation);
        }
        mTVHeader.setText("下拉刷新");
    }

    /**
     * 释放刷新
     */
    private final void releaseToRefresh() {
        if (mViewHeader == null) {
            return;
        }
        mIVHeader.startAnimation(mFlipAnimation);
        mTVHeader.setText("释放刷新");
    }

    /**
     * 正在刷新
     */
    private final void refreshing() {
        if (mViewHeader == null) {
            return;
        }
        isRefreshing = true;
        mIVHeader.clearAnimation();
        mIVHeader.setVisibility(View.INVISIBLE);
        mProgressHeader.setVisibility(View.VISIBLE);
        mTVHeader.setText("正在刷新");
        resetHeaderHeight();
    }

    /**
     * 调用下拉刷新
     */
    private void onRefresh() {
        isRefreshing = true;

        removeFooterView(mViewFooter);
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh();
        } else {
            onRefreshSuccess();
        }
    }


    private void onRefreshComplete(boolean isSuccess) {
        if (isSuccess) {
            mTVHeader.setText("刷新成功");
        } else {
            mTVHeader.setText("刷新失败");
        }
        isRefreshing = false;
        mState = State.RESET;
        mIVHeader.clearAnimation();
        mProgressHeader.setVisibility(View.INVISIBLE);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                resetHeaderHeight();
            }
        }, SCROLL_DURATION);
        if (mOnLoadMoreListener != null) {
            try {
                mProgressFooter.setVisibility(INVISIBLE);
                removeFooterView(mViewFooter);
                if (isLoadMoreable) {
                    mProgressFooter.setVisibility(View.INVISIBLE);
                    mViewFooterFailure.setVisibility(View.GONE);
                    addFooterView(mViewFooter, null, false);
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * 下拉刷新加载成功
     */
    public void onRefreshSuccess() {
        onRefreshComplete(true);
    }

    /**
     * 下拉刷新加载失败
     */
    public void onRefreshFailure() {
        onRefreshComplete(false);
    }

    /**
     * 调用加载更多
     */
    private void onLoadMore() {
        isLoadMoreing = true;

        mProgressFooter.setVisibility(View.VISIBLE);
        mViewFooterFailure.setVisibility(View.GONE);
        if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener.onLoadMore();
        } else {
            onLoadMoreSuccess();
        }
    }

    /**
     * 加载更多加载成功
     */
    public void onLoadMoreSuccess() {
        isLoadMoreing = false;
        if (!isLoadMoreable) {
            try {
                mProgressFooter.setVisibility(INVISIBLE);
                removeFooterView(mViewFooter);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 加载更多加载失败
     */
    public void onLoadMoreFailure() {
        isLoadMoreing = false;
        isLoadMoreable = false;
        mProgressFooter.setVisibility(View.INVISIBLE);
        mViewFooterFailure.setVisibility(View.VISIBLE);
    }

    /**
     * 下拉刷新监听器
     *
     * @author kycq
     */
    public interface OnRefreshListener {
        /**
         * 下拉刷新
         */
        public void onRefresh();
    }

    /**
     * 加载更多监听器
     *
     * @author kycq
     */
    public interface OnLoadMoreListener {
        /**
         * 加载更多
         */
        public void onLoadMore();
    }

    private enum State {
        /**
         * 重置
         */
        RESET,
        /**
         * 下拉刷新
         */
        PULL_TO_REFRESH,
        /**
         * 释放刷新
         */
        RELEASE_TO_REFRESH,
        /**
         * 刷新中
         */
        REFRESHING;
    }

}
