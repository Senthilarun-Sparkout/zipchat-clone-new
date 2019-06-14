package com.chat.zipchat.clone.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.chat.zipchat.clone.R;

import static com.chat.zipchat.clone.Common.BaseClass.myLog;

public class WebActivity extends AppCompatActivity {

    ImageView image_back;
    WebView mWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        mWeb = findViewById(R.id.mWeb);


        String url = getIntent().getStringExtra("URL");
        myLog("URL", url);

        mWeb.getSettings().setJavaScriptEnabled(true);
        mWeb.setWebViewClient(new WebViewClient());
        mWeb.getSettings().setAppCacheEnabled(true);
        mWeb.getSettings().setAppCachePath(getCacheDir().getPath());
        mWeb.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWeb.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + url);


        image_back = findViewById(R.id.image_back);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    @Override
    public void onBackPressed() {
        if (mWeb.canGoBack()) {
            mWeb.goBack();
        } else {
            super.onBackPressed();
        }
    }

}
