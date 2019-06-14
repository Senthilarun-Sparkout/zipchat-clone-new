package com.chat.zipchat.clone.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chat.zipchat.clone.R;

import static com.chat.zipchat.clone.Common.BaseClass.myLog;

public class ShareLocationActivity extends AppCompatActivity {

    Toolbar mToolbarShareLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);

        mToolbarShareLocation = findViewById(R.id.mToolbarShareLocation);
        setSupportActionBar(mToolbarShareLocation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.share_location));
        mToolbarShareLocation.setTitleTextColor(getResources().getColor(R.color.white));
        mToolbarShareLocation.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_share_location, menu);

        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_location_search));
        ImageView searchViewIcon = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        ViewGroup linearLayoutSearchView = (ViewGroup) searchViewIcon.getParent();
        linearLayoutSearchView.removeView(searchViewIcon);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                myLog("onQueryTextChange", newText);
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                myLog("onQueryTextSubmit", query);
                return true;
            }

        };
        mSearchView.setOnQueryTextListener(queryTextListener);
        return true;
    }

}
