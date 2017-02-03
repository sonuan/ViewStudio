package com.wusongyuan.customviewstudio.activity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.wusongyuan.customviewstudio.R;
import com.wusongyuan.customviewstudio.base.BaseActivity;
import com.wusongyuan.customviewstudio.widget.ProgressParView;
import com.wusongyuan.customviewstudio.widget.ShadowLayout;

import butterknife.BindView;
import butterknife.OnClick;

public class ProgressActivity extends BaseActivity {


    public static void toActivity(Context context) {
        Intent intent = new Intent(context, ProgressActivity.class);
        context.startActivity(intent);
    }
    @BindView(R.id.progressview)
    ProgressParView mProgressParView;
    @BindView(R.id.shadowlayout)
    ShadowLayout mShadowLayout;

    @Override
    public int getContentView() {
        return R.layout.activity_progress;
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initDatas(Bundle savedInstanceState) {

    }
    @OnClick(R.id.btn_start1)
    public void start(){
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
        valueAnimator.setDuration(10000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                Log.i(TAG, "onAnimationUpdate: " + value);
                mProgressParView.setProgress(value);
            }
        });
        valueAnimator.start();
    }
    @OnClick(R.id.btn_start2)
    public void startShadow(){
        mShadowLayout.startAnim();
    }
}
