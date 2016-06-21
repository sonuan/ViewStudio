package com.wusongyuan.customviewstudio.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.wusongyuan.customviewstudio.R;
import com.wusongyuan.customviewstudio.base.BaseActivity;
import com.wusongyuan.customviewstudio.widget.CircleLoadingView;

import butterknife.BindView;
import butterknife.OnClick;


public class CircleLoadingActivity extends BaseActivity {

    private static final String TAG = "CircleLoadingActivity";

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, CircleLoadingActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.circle_loading_view)
    public CircleLoadingView mCircleLoadingView;

    @Override
    public int getContentView() {
        return R.layout.activity_circle_loading;
    }

    @Override
    public void initViews() {
        setTitle("仿支付宝sdk支付结果View");
    }

    @Override
    public void initDatas(Bundle savedInstanceState) {

    }

    @OnClick(R.id.btn_loading)
    public void loading() {
        mCircleLoadingView.loading();
    }

    @OnClick(R.id.btn_loading2)
    public void loading2() {
        mCircleLoadingView.loading2();
    }

    @OnClick(R.id.btn_success)
    public void success() {
        mCircleLoadingView.loadSuccess();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCircleLoadingView.cancel();
    }
}
