package com.wusongyuan.customviewstudio.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wusongyuan.customviewstudio.R;

public class WaterWavaActivity extends AppCompatActivity {
    
    public static void toActivity(Context context) {
        Intent intent = new Intent(context, WaterWavaActivity.class);
        context.startActivity(intent);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_wava);
    }
}
