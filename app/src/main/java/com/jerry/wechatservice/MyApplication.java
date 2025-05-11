package com.jerry.wechatservice;

import android.annotation.SuppressLint;
import android.app.Application;

/**
 * @author Jerry
 * @createDate 2019/4/10
 * @copyright www.axiang.com
 * @description
 */
public class MyApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Application mInstance;

    public static Application getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
}
