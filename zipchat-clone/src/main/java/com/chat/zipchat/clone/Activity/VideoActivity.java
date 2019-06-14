package com.chat.zipchat.clone.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.chat.zipchat.clone.R;

import static com.chat.zipchat.clone.Common.BaseClass.myLog;

public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        myLog("URL", getIntent().getStringExtra("URL"));

        ImageView mVideoBack = findViewById(R.id.mVideoBack);

        mVideoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        VideoView videoView = (VideoView) findViewById(R.id.vdVw);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        Uri uri = Uri.parse(getIntent().getStringExtra("URL"));
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();


    }
}
