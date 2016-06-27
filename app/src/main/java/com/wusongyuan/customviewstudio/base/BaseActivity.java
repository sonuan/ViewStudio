package com.wusongyuan.customviewstudio.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    protected String TAG ;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
        setContentView(getContentView());
        ButterKnife.bind(this);
        initViews();
        initDatas(savedInstanceState);
    }

    public abstract int getContentView();

    public abstract void initViews();

    public abstract void initDatas(Bundle savedInstanceState);

}
