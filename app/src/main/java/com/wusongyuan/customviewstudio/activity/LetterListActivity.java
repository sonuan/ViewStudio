package com.wusongyuan.customviewstudio.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wusongyuan.customviewstudio.R;

/**
 * 通讯录字母索引View
 */
public class LetterListActivity extends AppCompatActivity {

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, LetterListActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_list);
        setTitle("通讯录列表字母索引");
    }
}
