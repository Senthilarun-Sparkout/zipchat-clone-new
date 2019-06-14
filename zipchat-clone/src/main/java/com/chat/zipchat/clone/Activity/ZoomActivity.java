package com.chat.zipchat.clone.Activity;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chat.zipchat.clone.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

import static com.chat.zipchat.clone.Common.BaseClass.removeProgressDialog;
import static com.chat.zipchat.clone.Common.BaseClass.requestOptionsT;
import static com.chat.zipchat.clone.Common.BaseClass.showSimpleProgressDialog;


public class ZoomActivity extends AppCompatActivity implements View.OnClickListener {

    PhotoView mPhotoView;
    ImageView mImageBack;
    String mPhoto;
    int value = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);

        mPhotoView = findViewById(R.id.photo_view);
        mPhotoView.setMaximumScale(5.0f);
        mImageBack = findViewById(R.id.mImageBack);
        mImageBack.setOnClickListener(this);

        value = getIntent().getIntExtra("Value", 0);

        if (value == 1) {
            mPhoto = getIntent().getStringExtra("PATH");
            File file = new File(mPhoto);
            Uri imageUri = Uri.fromFile(file);
            Glide.with(this).setDefaultRequestOptions(requestOptionsT()).load(imageUri).into(mPhotoView);

        } else if (value == 2) {
            showSimpleProgressDialog(this);
            mPhoto = getIntent().getStringExtra("URL");
            Glide.with(this).setDefaultRequestOptions(requestOptionsT()).load(mPhoto).
                    listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            removeProgressDialog();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            removeProgressDialog();
                            return false;
                        }
                    }).into(mPhotoView);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mImageBack:
                finish();
                break;
        }
    }

}
