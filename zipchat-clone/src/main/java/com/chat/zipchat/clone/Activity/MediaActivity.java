package com.chat.zipchat.clone.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.chat.zipchat.clone.Fragment.DocumentFragment;
import com.chat.zipchat.clone.Fragment.PhotosFragment;
import com.chat.zipchat.clone.Fragment.VideoFragment;
import com.chat.zipchat.clone.R;

public class MediaActivity extends AppCompatActivity {

    TabLayout mTabMedia;
    Toolbar toolbar_media;
    ViewPager mViewPageMedia;
    String toId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        toolbar_media = findViewById(R.id.toolbar_media);
        mViewPageMedia = findViewById(R.id.mViewPageMedia);
        mTabMedia = findViewById(R.id.mTabMedia);
        mTabMedia.setupWithViewPager(mViewPageMedia);

        setSupportActionBar(toolbar_media);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("Name"));
        toId = getIntent().getStringExtra("toId");

        setupViewPager(mViewPageMedia);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        String[] titles = new String[]{"Photos", "Videos", "Documents"};

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {

            PhotosFragment photosFragment = new PhotosFragment(toId);
            VideoFragment videoFragment = new VideoFragment(toId);
            DocumentFragment documentFragment = new DocumentFragment(toId);

            switch (position) {
                case 0:
                    return photosFragment;
                case 1:
                    return videoFragment;
                case 2:
                    return documentFragment;

            }
            return photosFragment;
        }

        @Override
        public int getCount() {
            return titles.length; //This one is important too
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
        }

        return true;
    }

}
