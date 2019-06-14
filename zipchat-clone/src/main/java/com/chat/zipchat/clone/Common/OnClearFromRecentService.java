package com.chat.zipchat.clone.Common;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static com.chat.zipchat.clone.Common.BaseClass.ConvertedDateTime;
import static com.chat.zipchat.clone.Common.BaseClass.UserId;
import static com.chat.zipchat.clone.Common.BaseClass.sessionManager;
import static com.chat.zipchat.clone.Common.BaseClass.sharedPreferences;
import static com.chat.zipchat.clone.Common.SessionManager.KEY_PROFILE_PIC;
import static com.chat.zipchat.clone.Common.SessionManager.KEY_USERNAME;
import static com.chat.zipchat.clone.Common.SessionManager.PHONE;
import static com.chat.zipchat.clone.Common.SessionManager.STATUS;

public class OnClearFromRecentService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        DatabaseReference userLastOnlineRef = FirebaseDatabase.getInstance().getReference("user-details").child(UserId(this)).child("profile-details");

        HashMap<String, Object> map = new HashMap<>();
        map.put("isOnline", "0");
        map.put("offline-time", ConvertedDateTime());
        map.put("name", sharedPreferences(this).getString(KEY_USERNAME, null));
        map.put("mobile-number", sharedPreferences(this).getString(PHONE, null));
        map.put("profile-url", sharedPreferences(this).getString(KEY_PROFILE_PIC, null));
        map.put("status", sharedPreferences(this).getString(STATUS, null));

        if (sessionManager(this).isHideLastSeen()) {
            map.put("hide-last-seen", "1");
        } else {
            map.put("hide-last-seen", "0");
        }

        userLastOnlineRef.onDisconnect().updateChildren(map);

        stopSelf();
    }
}
