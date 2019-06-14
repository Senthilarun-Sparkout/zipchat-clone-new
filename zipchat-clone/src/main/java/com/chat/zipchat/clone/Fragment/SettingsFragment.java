package com.chat.zipchat.clone.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.chat.zipchat.clone.Activity.MainActivity;
import com.chat.zipchat.clone.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static com.chat.zipchat.clone.Common.BaseClass.ConvertedDateTime;
import static com.chat.zipchat.clone.Common.BaseClass.OTHER_FRAGMENT;
import static com.chat.zipchat.clone.Common.BaseClass.UserId;
import static com.chat.zipchat.clone.Common.BaseClass.isOnline;
import static com.chat.zipchat.clone.Common.BaseClass.logout_User;
import static com.chat.zipchat.clone.Common.BaseClass.sessionManager;
import static com.chat.zipchat.clone.Common.BaseClass.sharedPreferences;
import static com.chat.zipchat.clone.Common.SessionManager.KEY_PROFILE_PIC;
import static com.chat.zipchat.clone.Common.SessionManager.KEY_USERNAME;
import static com.chat.zipchat.clone.Common.SessionManager.PHONE;
import static com.chat.zipchat.clone.Common.SessionManager.STATUS;

@SuppressLint("ValidFragment")
public class SettingsFragment extends Fragment implements View.OnClickListener, Switch.OnCheckedChangeListener {

    MainActivity mContext;
    Switch mSwitchHideLastSeen, mSwitchDownloadOverWifi;
    TextView mDeleteAccount;

    public SettingsFragment(MainActivity mContext) {
        this.mContext = mContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mDeleteAccount = view.findViewById(R.id.mDeleteAccount);
        mSwitchHideLastSeen = view.findViewById(R.id.mSwitchHideLastSeen);
        mSwitchDownloadOverWifi = view.findViewById(R.id.mSwitchDownloadOverWifi);
        mDeleteAccount.setOnClickListener(this);

        mSwitchHideLastSeen.setOnCheckedChangeListener(this);
        mSwitchDownloadOverWifi.setOnCheckedChangeListener(this);

        EnableSetting();

        return view;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mDeleteAccount) {
            logout_User(getActivity());
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int i = buttonView.getId();
        if (i == R.id.mSwitchHideLastSeen) {
            DatabaseReference userLastOnlineRef = FirebaseDatabase.getInstance().getReference("user-details").child(UserId(getActivity())).child("profile-details");

            if (isOnline(getActivity())) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("isOnline", "1");
                map.put("offline-time", ConvertedDateTime());
                map.put("name", sharedPreferences(getActivity()).getString(KEY_USERNAME, null));
                map.put("mobile-number", sharedPreferences(getActivity()).getString(PHONE, null));
                map.put("profile-url", sharedPreferences(getActivity()).getString(KEY_PROFILE_PIC, null));
                map.put("status", sharedPreferences(getActivity()).getString(STATUS, null));

                sessionManager(getActivity()).HideLastSeen(mSwitchHideLastSeen.isChecked());

                if (sessionManager(getActivity()).isHideLastSeen()) {
                    map.put("hide-last-seen", "1");
                } else {
                    map.put("hide-last-seen", "0");
                }

                userLastOnlineRef.updateChildren(map);
            }
        } else if (i == R.id.mSwitchDownloadOverWifi) {
            sessionManager(getActivity()).setDownloadOnlyWifi(mSwitchDownloadOverWifi.isChecked());
        }
    }

    public void EnableSetting() {
        if (sessionManager(getActivity()).isHideLastSeen()) {
            mSwitchHideLastSeen.setChecked(true);
        } else {
            mSwitchHideLastSeen.setChecked(false);
        }

        if (sessionManager(getActivity()).isDownloadOnlyWifi()) {
            mSwitchDownloadOverWifi.setChecked(true);
        } else {
            mSwitchDownloadOverWifi.setChecked(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mContext.currentFragment = OTHER_FRAGMENT;
    }
}
