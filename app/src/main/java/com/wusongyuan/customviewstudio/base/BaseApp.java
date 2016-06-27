package com.wusongyuan.customviewstudio.base;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**********************
 * @author: wusongyuan
 * @date: 2016-06-27
 * @desc:
 **********************/
public class BaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
