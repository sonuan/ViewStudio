package com.wusongyuan.customviewstudio.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.wusongyuan.customviewstudio.R;
import com.wusongyuan.customviewstudio.base.BaseActivity;

public class XfermodeStudioActivity extends BaseActivity {

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, XfermodeStudioActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_xfermode_studio;
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initDatas(Bundle savedInstanceState) {

    }
}
