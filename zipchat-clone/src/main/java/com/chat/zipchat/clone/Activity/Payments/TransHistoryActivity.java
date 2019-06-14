package com.chat.zipchat.clone.Activity.Payments;

import android.content.Context;
import android.graphics.PorterDuff;
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
import android.view.View;

import com.chat.zipchat.clone.Fragment.Payment.AddMoneyTransFragment;
import com.chat.zipchat.clone.Fragment.Payment.ReceiveTransFragment;
import com.chat.zipchat.clone.Fragment.Payment.SentTransFragment;
import com.chat.zipchat.clone.Fragment.Payment.WithdrawnTransFragment;
import com.chat.zipchat.clone.R;

public class TransHistoryActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar mToolbarTransHistory;
    TabLayout mTabTransaction;
    ViewPager mViewPageTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_history);

        mToolbarTransHistory = findViewById(R.id.mToolbarTransHistory);
        setSupportActionBar(mToolbarTransHistory);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.transaction_history));
        mToolbarTransHistory.setTitleTextColor(getResources().getColor(R.color.white));
        mToolbarTransHistory.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        mViewPageTransaction = findViewById(R.id.mViewPageTransaction);
        mTabTransaction = findViewById(R.id.mTabTransaction);
        mTabTransaction.setupWithViewPager(mViewPageTransaction);

        setupViewPager(mViewPageTransaction);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        Context mContext;
        String[] titles = new String[]{
                getResources().getString(R.string.sent),
                getResources().getString(R.string.receive),
                getResources().getString(R.string.add_money),
                getResources().getString(R.string.withdrawn)};

        public ViewPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.mContext = context;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {

            SentTransFragment mSentTrans = new SentTransFragment(mContext);
            ReceiveTransFragment mReceiveTrans = new ReceiveTransFragment(mContext);
            AddMoneyTransFragment mAddMoneyTrans = new AddMoneyTransFragment(mContext);
            WithdrawnTransFragment mWithdrawnTrans = new WithdrawnTransFragment(mContext);

            switch (position) {
                case 0:
                    return mSentTrans;
                case 1:
                    return mReceiveTrans;
                case 2:
                    return mAddMoneyTrans;
                case 3:
                    return mWithdrawnTrans;

            }
            return mSentTrans;
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
                finish();
                break;

        }
        return true;
    }

}
