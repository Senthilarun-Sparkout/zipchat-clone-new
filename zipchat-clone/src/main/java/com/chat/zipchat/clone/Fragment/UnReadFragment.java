package com.chat.zipchat.clone.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chat.zipchat.clone.R;

@SuppressLint("ValidFragment")
public class UnReadFragment extends Fragment {

    Context mContext;

    public UnReadFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_un_read, container, false);
        return view;
    }
}
