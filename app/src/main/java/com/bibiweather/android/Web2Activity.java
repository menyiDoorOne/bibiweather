package com.bibiweather.android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Web2Activity extends AppCompatActivity {

    //跳转到我的微博主页，这次APP中没有用到

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web2);
        WebView webView = findViewById(R.id.web_view2);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://weibo.com/my459198833?refer_flag=1001030101_&is_hot=1");
    }
}
