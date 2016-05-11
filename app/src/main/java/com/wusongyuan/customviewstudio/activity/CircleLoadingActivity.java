package com.wusongyuan.customviewstudio.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wusongyuan.customviewstudio.R;
import com.wusongyuan.customviewstudio.widget.CircleLoadingView;


public class CircleLoadingActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "CircleLoadingActivity";

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, CircleLoadingActivity.class);
        context.startActivity(intent);
    }

    private CircleLoadingView mCircleLoadingView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_loading);
        setTitle("仿支付宝sdk支付结果View");
        findViewById(R.id.btn_start).setOnClickListener(this);
        mCircleLoadingView = (CircleLoadingView) findViewById(R.id.myview);
    }

    @Override
    public void onClick(View v) {
        mCircleLoadingView.loading();
    }

}
