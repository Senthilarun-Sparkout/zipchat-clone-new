package com.chat.zipchat.clone.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chat.zipchat.clone.Adapter.FavouriteAdapter;
import com.chat.zipchat.clone.Common.App;
import com.chat.zipchat.clone.Model.FavouritePojo;
import com.chat.zipchat.clone.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    TabLayout mTabMain;
    ViewPager mViewPageMain;
    RecyclerView mRecyclerFavourite;
    LinearLayout mFavouriteLl;

    List<FavouritePojo> mFavouriteList = new ArrayList<>();
    FavouriteAdapter favouriteAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mTabMain = view.findViewById(R.id.mTabMain);
        mViewPageMain = view.findViewById(R.id.mViewPageMain);
        mFavouriteLl = view.findViewById(R.id.mFavouriteLl);

        setupViewPager(mViewPageMain);
        mTabMain.setupWithViewPager(mViewPageMain);

        mRecyclerFavourite = view.findViewById(R.id.mRecyclerFavourite);
        mRecyclerFavourite.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        //        String[] titles = new String[]{"All Chats", "Unread"};
        String[] titles = new String[]{"All Chats"};

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

            ChatFragment chatFragment = new ChatFragment(getActivity());
            UnReadFragment unReadFragment = new UnReadFragment(getActivity());

            switch (position) {
                case 0:
                    return chatFragment;
                /*case 1:
                    return unReadFragment;*/

            }
            return chatFragment; //This one is important
        }

        @Override
        public int getCount() {
            return titles.length; //This one is important too
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mFavouriteList.clear();
        mFavouriteList = App.getmInstance().favouritePojoDao.queryBuilder().list();

        if (mFavouriteList.size() > 0) {

            mFavouriteLl.setVisibility(View.VISIBLE);
            favouriteAdapter = new FavouriteAdapter(getActivity(), mFavouriteList);
            mRecyclerFavourite.setAdapter(favouriteAdapter);

        } else {
            mFavouriteLl.setVisibility(View.GONE);
        }

    }
}
