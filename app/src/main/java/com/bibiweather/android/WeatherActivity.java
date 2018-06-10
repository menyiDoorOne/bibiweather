package com.bibiweather.android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bibiweather.android.gson.Forecast;
import com.bibiweather.android.gson.Weather;
import com.bibiweather.android.service.AutoUpdateService;
import com.bibiweather.android.util.HttpUtil;
import com.bibiweather.android.util.Utility;
import com.bumptech.glide.Glide;
import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    private Button navButton;
    public SwipeRefreshLayout swipeRefreshLayout;
    private String mWeatherId;
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView apiText;
    private TextView pm25Text;
    private TextView qltyText;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private TextView dressText;
    private TextView fluText;
    private TextView travelText;
    private TextView uvText;
    private Button searchButton;
    private Button earthButton;

    private KqwSpeechCompound kqwSpeechCompound;
    private static Context context;
    
    private static final String TAG = "MainActivity";
    private static final String appId = "5b137938";//请更换为自己创建的应用
    private static final String appKey = "AS7oPnwphif66WOasN5B2T43";//请更换为自己创建的应用
    private static final String secretKey = "gmk03dHMQXevspvtgqbB8o26pMQOBHVE";//请更换为自己创建的应用


    private ImageView bingPicImg;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SpeechUtility.createUtility(WeatherActivity.this, SpeechConstant.APPID + "=5b137938"); //初始化

        if(Build.VERSION.SDK_INT>=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        //初始化各控件
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        apiText = findViewById(R.id.api_text);
        pm25Text = findViewById(R.id.pm25_text);
        qltyText = findViewById(R.id.qlty_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
        dressText = findViewById(R.id.dress_text);
        fluText = findViewById(R.id.flu_text);
        travelText = findViewById(R.id.travel_text);
        uvText = findViewById(R.id.uv_text);
        bingPicImg = findViewById(R.id.bing_pic_img);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary);
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);
        searchButton = findViewById(R.id.search_button);
        earthButton = findViewById(R.id.earth_button);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        String bingPic = prefs.getString("bing_pic",null);

        kqwSpeechCompound=new KqwSpeechCompound(WeatherActivity.this);
        initPermissions();

        //悬浮按钮的监听
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view,"Open your location",Snackbar.LENGTH_SHORT).setAction("do", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(WeatherActivity.this, "do", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(WeatherActivity.this,LBSActivity.class);
                        WeatherActivity.this.startActivity(intent);
                    }
                }).show();


            }
        });


        //语音播报的监听
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TTSUtils.getInstance().speak("现在"+titleCity.getText().toString().trim()+weatherInfoText.getText().toString()+"温度"+degreeText.getText().toString());
                kqwSpeechCompound.speaking("亲爱的，现在"+titleCity.getText().toString().trim()+"天气，"+weatherInfoText.getText().toString()+"，温度，"+degreeText.getText().toString());
                /*Intent intent = new Intent(WeatherActivity.this,SearchCityActivity.class);
                WeatherActivity.this.startActivity(intent);*/
            }
        });



        //分享按钮的监听
        earthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent textIntent = new Intent(Intent.ACTION_SEND);
                textIntent.setType("text/plain");
                textIntent.putExtra(Intent.EXTRA_TEXT, "我正在使用碧碧天气APP，欢迎你也来华为应用市场下载！");
                startActivity(Intent.createChooser(textIntent, "分享"));


                //Intent intent = new Intent(WeatherActivity.this,Web2Activity.class);
                //WeatherActivity.this.startActivity(intent);
            }
        });


        //城市名的监听
        titleCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = titleCity.getText().toString();
                Intent intent = new Intent(WeatherActivity.this,WebActivity.class);
                intent.putExtra("cityName",city);
                WeatherActivity.this.startActivity(intent);
            }
        });



        //舒适度、洗车指数、运动指数的监听
        comfortText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kqwSpeechCompound.speaking("亲爱的，"+comfortText.getText().toString());
            }
        });

        carWashText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kqwSpeechCompound.speaking("亲爱的，"+carWashText.getText().toString());
            }
        });

        sportText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kqwSpeechCompound.speaking("亲爱的，"+sportText.getText().toString());
            }
        });



        if(bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }
        if(weatherString!=null){
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            //无缓存时去服务器查询天气
            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


    }


    //动态获取权限
    private void initPermissions(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int permission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(permission!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[]
                            {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.LOCATION_HARDWARE,Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.WRITE_SETTINGS,Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_CONTACTS},0x0010);
                }

                if(permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},0x0010);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //获取权限的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //  super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // 授权被允许
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("-------->", "授权请求被允许");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Log.e("-------->", "授权请求被拒绝");
                }
                return;
            }
        }
    }

    

    /**根据天气id请求城市天气信息*/
    public void requestWeather(final String weatherId){
        String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId+"&key=35f808c3e6f94294b9f7d51897e01c24";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null&&"ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            mWeatherId = weather.basic.weatherId;
                            showWeatherInfo(weather);

                        }else{
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }
    /**加载必应每日一图*/
    private void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
    /**处理并展示Weather实体类中的数据*/
    private void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature+"℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText("更新："+updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(Forecast forecast:weather.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max+"℃");
            minText.setText(forecast.temperature.min+"℃");
            forecastLayout.addView(view);
        }
        if(weather.aqi != null){
            apiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
            if(weather.aqi.city.qlty.length()<=2){
                qltyText.setTextSize(40);
                qltyText.setText(weather.aqi.city.qlty);
            }else{
                qltyText.setTextSize(25);
                qltyText.setText(weather.aqi.city.qlty);
            }
        }
        String comfort = "舒适度："+weather.suggestion.comfort.info;
        String carWash = "洗车指数："+weather.suggestion.carWash.info;
        String sport = "运动建议："+weather.suggestion.sport.info;
        /*String dress = "穿衣指数："+weather.suggestion.drsg.info;
        String flu = "感冒指数："+weather.suggestion.flu.info;
        String travel = "旅行指数："+weather.suggestion.trav.info;
        String uv = "紫外线指数："+weather.suggestion.uv.info;*/
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        /*dressText.setText(dress);
        fluText.setText(flu);
        travelText.setText(travel);
        uvText.setText(uv);*/

        weatherLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }


}
