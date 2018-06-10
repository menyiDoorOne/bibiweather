package com.bibiweather.android;

import android.app.Application;
import android.content.Context;

import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by 门一 on 2018/6/4.
 */

public class MyApplication extends Application {

    private static Context context;


    //这是另一种语音工具类涉及的APPLICATION
    //在这次APP中没有用到
    @Override
    public void onCreate() {
        super.onCreate();
        SpeechUtility.createUtility(this, "appid=5b137938");
        // 以下语句用于设置日志开关（默认开启），设置成false时关闭语音云SDK日志打印
        /*Setting.setShowLog(false);
        TTSUtils.getInstance().init();*/
    }

    public static Context getContext() {
        return context;
    }
}
