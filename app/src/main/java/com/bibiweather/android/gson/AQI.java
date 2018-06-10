package com.bibiweather.android.gson;

/**
 * Created by 门一 on 2018/5/26.
 */

public class AQI {
    public AQICity city;
    public class AQICity{
        public String aqi;
        public String pm25;
        public String qlty;
    }
}
