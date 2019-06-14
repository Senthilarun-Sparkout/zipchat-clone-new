package com.chat.zipchat.clone.Fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.chat.zipchat.clone.Activity.ChatActivity;
import com.chat.zipchat.clone.Activity.MainActivity;
import com.chat.zipchat.clone.Common.App;
import com.chat.zipchat.clone.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sinch.android.rtc.NotificationResult;
import com.sinch.android.rtc.SinchHelpers;
import com.sinch.android.rtc.calling.CallNotificationResult;
import com.sinch.gson.Gson;

import java.io.Serializable;
import java.util.HashMap;

import static com.chat.zipchat.clone.Common.BaseClass.isAppOnForeground;
import static com.chat.zipchat.clone.Common.BaseClass.myLog;


public class MyMessagingService extends FirebaseMessagingService {

    public static final String NOTIFICATION_CHANNEL_ID = "1001";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        myLog("onMessageReceived: ", remoteMessage.getData().toString());
        if (!isAppOnForeground(this)) {
            if (remoteMessage.getData().size() > 0) {
                try {

                    sendNotification(remoteMessage);

                } catch (Exception e) {
                    myLog("Exception: ", e.getMessage());
                }
            }

        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {

        String message = "";
        Intent intent = null;

        if (SinchHelpers.isSinchPushPayload(remoteMessage.getData())) {
            try {
                Intent mIntent = new Intent(this, MainActivity.class);
                mIntent.setAction(Intent.ACTION_MAIN);
                mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mIntent.putExtra("push_data", new Gson().toJson(remoteMessage.getData()));
                this.getApplicationContext().startActivity(mIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            message = remoteMessage.getData().get("message");
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("type", remoteMessage.getData().get("type"));
            hashMap.put("user", remoteMessage.getData().get("user"));

            intent = new Intent(this, ChatActivity.class);
            intent.putExtra("notifyHashMap", hashMap);
            intent.addFlags(Intent.FLAG_FROM_BACKGROUND | Intent.FLAG_ACTIVITY_NEW_TASK);
        }


        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification.Builder mBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setStyle(new Notification.BigTextStyle()
                                .bigText(message))
                        .setVibrate(new long[]{100, 500})
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setAutoCancel(true)
                        .setContentText(message);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert notificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        assert notificationManager != null;

        mBuilder.setContentIntent(pIntent);
        notificationManager.notify(0, mBuilder.build());


    }

}