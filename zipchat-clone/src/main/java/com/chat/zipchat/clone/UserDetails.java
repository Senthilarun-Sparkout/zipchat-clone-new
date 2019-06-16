package com.chat.zipchat.clone;

import android.content.Context;

import com.chat.zipchat.clone.Common.App;
import com.chat.zipchat.clone.Model.ResultItem;
import com.chat.zipchat.clone.Model.ResultItemDao;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

import static com.chat.zipchat.clone.Common.BaseClass.ConvertedDateTime;
import static com.chat.zipchat.clone.Common.BaseClass.sessionManager;

public class UserDetails {

    public static void setUserDetails(Context context, UserRegister userRegister) {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", userRegister.getName());
        map.put("mobile-number", userRegister.getMobile_number());
        map.put("profile-url", userRegister.getProfile_url());
        map.put("isOnline", "1");
        map.put("offline-time", ConvertedDateTime());

        if (null == userRegister.getStatus()) {
            map.put("status", "Available");
        } else {
            map.put("status", userRegister.getStatus());
        }

        DatabaseReference referenceUserInsert = FirebaseDatabase.getInstance().getReference("user-details").child(userRegister.getId()).child("profile-details");
        referenceUserInsert.setValue(map);

        sessionManager(context).createLoginSession(
                userRegister.getId(),
                userRegister.getName(),
                userRegister.getProfile_url(),
                "",
                userRegister.getMobile_number(),
                "",
                userRegister.getStatus(),
                "",
                "");
    }

    public static ResultItem getUserDetails(String toId) {

        final List<ResultItem> resultItems = App.getmInstance().resultItemDao.queryBuilder().where(
                ResultItemDao.Properties.Id.eq(toId)).list();

        if (resultItems.size() > 0) {
            return resultItems.get(0);
        }

        return null;
    }

}
