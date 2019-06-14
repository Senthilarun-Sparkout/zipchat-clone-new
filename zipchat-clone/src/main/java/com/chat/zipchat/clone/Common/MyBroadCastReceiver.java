package com.chat.zipchat.clone.Common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chat.zipchat.clone.Model.Chat.ChatPojo;
import com.chat.zipchat.clone.Model.Chat.ChatPojoDao;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

import static com.chat.zipchat.clone.Common.BaseClass.ConvertedDateTime;
import static com.chat.zipchat.clone.Common.BaseClass.UserId;
import static com.chat.zipchat.clone.Common.BaseClass.isOnline;
import static com.chat.zipchat.clone.Common.BaseClass.sessionManager;
import static com.chat.zipchat.clone.Common.BaseClass.sharedPreferences;
import static com.chat.zipchat.clone.Common.SessionManager.KEY_PROFILE_PIC;
import static com.chat.zipchat.clone.Common.SessionManager.KEY_USERNAME;
import static com.chat.zipchat.clone.Common.SessionManager.PHONE;
import static com.chat.zipchat.clone.Common.SessionManager.STATUS;

public class MyBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        DatabaseReference userLastOnlineRef = null;

        if (UserId(context) != null) {
            userLastOnlineRef = FirebaseDatabase.getInstance().getReference("user-details").child(UserId(context)).child("profile-details");
        }

        try {
            if (isOnline(context)) {

                HashMap<String, Object> map = new HashMap<>();
                map.put("isOnline", "1");
                map.put("offline-time", ConvertedDateTime());
                map.put("name", sharedPreferences(context).getString(KEY_USERNAME, null));
                map.put("mobile-number", sharedPreferences(context).getString(PHONE, null));
                map.put("profile-url", sharedPreferences(context).getString(KEY_PROFILE_PIC, null));
                map.put("status", sharedPreferences(context).getString(STATUS, null));

                if (sessionManager(context).isHideLastSeen()) {
                    map.put("hide-last-seen", "1");
                } else {
                    map.put("hide-last-seen", "0");
                }

                if (userLastOnlineRef != null) {
                    userLastOnlineRef.updateChildren(map);
                }

                List<ChatPojo> chatPojoList = App.getmInstance().chatPojoDao.queryBuilder().where(ChatPojoDao.Properties.IsMessageSend.eq(0)).list();

                for (int i = 0; i < chatPojoList.size(); i++) {

                    ChatPojo chatPojo = new ChatPojo();
                    chatPojo.setFromId(chatPojoList.get(i).getFromId());
                    chatPojo.setText(chatPojoList.get(i).getText());
                    chatPojo.setTimestamp(chatPojoList.get(i).getTimestamp());
                    chatPojo.setToId(chatPojoList.get(i).getToId());
                    chatPojo.setMsgType(chatPojoList.get(i).getMsgType());
                    chatPojo.setIsRead(chatPojoList.get(i).getIsRead());
                    chatPojo.setFileUrl(chatPojoList.get(i).getFileUrl());

                    DatabaseReference referenceMessageInsert = FirebaseDatabase.getInstance().getReference("messages");
                    referenceMessageInsert.child(chatPojoList.get(i).getMessageId()).setValue(chatPojo);

                    DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("user-messages").child(UserId(context));
                    referenceUser.child(chatPojoList.get(i).getToId()).child(chatPojoList.get(i).getMessageId()).setValue("1");

                    DatabaseReference referenceUserInsert = FirebaseDatabase.getInstance().getReference("user-messages").child(chatPojoList.get(i).getToId()).child(UserId(context));
                    referenceUserInsert.child(chatPojoList.get(i).getMessageId()).setValue("1");

                    ChatPojo chatPojos = App.getmInstance().chatPojoDao.queryBuilder().where(ChatPojoDao.Properties.MessageId.eq(chatPojoList.get(i).getMessageId())).list().get(0);
                    chatPojos.setIsMessageSend(1);
                    App.getmInstance().chatPojoDao.update(chatPojos);


                }

            } else {

                HashMap<String, Object> map = new HashMap<>();
                map.put("isOnline", "0");
                map.put("offline-time", ConvertedDateTime());
                map.put("name", sharedPreferences(context).getString(KEY_USERNAME, null));
                map.put("mobile-number", sharedPreferences(context).getString(PHONE, null));
                map.put("profile-url", sharedPreferences(context).getString(KEY_PROFILE_PIC, null));
                map.put("status", sharedPreferences(context).getString(STATUS, null));

                if (sessionManager(context).isHideLastSeen()) {
                    map.put("hide-last-seen", "1");
                } else {
                    map.put("hide-last-seen", "0");
                }

                if (userLastOnlineRef != null) {
                    userLastOnlineRef.onDisconnect().updateChildren(map);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}
