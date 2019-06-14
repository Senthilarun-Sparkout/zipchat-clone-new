package com.chat.zipchat.clone.Common;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.chat.zipchat.clone.Activity.AudioIncomingCallActivity;
import com.chat.zipchat.clone.Activity.VideoIncomingCallActivity;
import com.chat.zipchat.clone.Model.AcceptRejectPojoDao;
import com.chat.zipchat.clone.Model.Chat.ChatPojoDao;
import com.chat.zipchat.clone.Model.ChatList.ChatListPojoDao;
import com.chat.zipchat.clone.Model.Contact.ContactResponseDao;
import com.chat.zipchat.clone.Model.Contact.ResultItemDao;
import com.chat.zipchat.clone.Model.DaoMaster;
import com.chat.zipchat.clone.Model.DaoSession;
import com.chat.zipchat.clone.Model.Download.LocalDataPojoDao;
import com.chat.zipchat.clone.Model.Favourites.FavouritePojoDao;
import com.chat.zipchat.clone.Model.Group.GroupItemsDao;
import com.chat.zipchat.clone.Model.Group.GroupMemberDao;
import com.chat.zipchat.clone.Model.Group.GroupsDao;
import com.crashlytics.android.Crashlytics;
import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.video.VideoScalingType;

import io.fabric.sdk.android.Fabric;

import static com.chat.zipchat.clone.Common.BaseClass.UserId;
import static com.chat.zipchat.clone.Common.BaseClass.myLog;


public class App extends Application {

    public static App mInstance;
    private DaoMaster.DevOpenHelper helper;
    private SQLiteDatabase db;
    public DaoMaster daoMaster;
    public DaoSession daoSession;
    public ContactResponseDao contactResponseDao;
    public ResultItemDao resultItemDao;

    public ChatPojoDao chatPojoDao;
    public ChatListPojoDao chatListPojoDao;
    public FavouritePojoDao favouritePojoDao;
    public LocalDataPojoDao localDataPojoDao;
    public GroupsDao groupsDao;
    public GroupMemberDao groupMemberDao;
    public GroupItemsDao groupItemsDao;
    public AcceptRejectPojoDao acceptRejectPojoDao;

    private static final String APP_KEY = "a8cdc0e3-bdd3-44a8-8294-3d6b709668bc";
    private static final String APP_SECRET = "0nUyaX6Y5Em07WpMOmJwsQ==";
    private static final String ENVIRONMENT = "sandbox.sinch.com";
    public SinchClient sinchClient = null;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        helper = new DaoMaster.DevOpenHelper(this, "gks-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

        contactResponseDao = daoSession.getContactResponseDao();
        resultItemDao = daoSession.getResultItemDao();

        chatPojoDao = daoSession.getChatPojoDao();
        chatListPojoDao = daoSession.getChatListPojoDao();
        favouritePojoDao = daoSession.getFavouritePojoDao();
        localDataPojoDao = daoSession.getLocalDataPojoDao();
        groupsDao = daoSession.getGroupsDao();
        groupMemberDao = daoSession.getGroupMemberDao();
        groupItemsDao = daoSession.getGroupItemsDao();
        acceptRejectPojoDao = daoSession.getAcceptRejectPojoDao();

        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(this);
        Fabric.with(this, new Crashlytics());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private static class MySinchClientListener implements SinchClientListener {

        @Override
        public void onClientStarted(SinchClient sinchClient) {
        }

        @Override
        public void onClientStopped(SinchClient sinchClient) {
            myLog("Arun: ", "onClientStopped");
        }

        @Override
        public void onClientFailed(SinchClient sinchClient, SinchError sinchError) {
            myLog("Arun: ", sinchError.getMessage());
        }

        @Override
        public void onRegistrationCredentialsRequired(SinchClient sinchClient, ClientRegistration clientRegistration) {

        }

        @Override
        public void onLogMessage(int i, String s, String s1) {

        }
    }

    public static App getmInstance() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void startSinch(Context mContext, String strUsername) {
        android.content.Context context = mContext.getApplicationContext();
        sinchClient = Sinch.getSinchClientBuilder().context(context)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .userId(UserId(mContext))
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.setSupportManagedPush(true);
        sinchClient.setSupportActiveConnectionInBackground(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.addSinchClientListener(new MySinchClientListener());
        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener(mContext));
        sinchClient.getVideoController().setResizeBehaviour(VideoScalingType.ASPECT_FILL);
        sinchClient.setPushNotificationDisplayName(strUsername);
        sinchClient.start();
    }

    private static class SinchCallClientListener implements CallClientListener {
        Context mContext;

        public SinchCallClientListener(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public void onIncomingCall(CallClient callClient, Call call) {
            if (call.getDetails().isVideoOffered()) {
                //Video
                mContext.startActivity(new Intent(mContext, VideoIncomingCallActivity.class)
                        .putExtra("call_id", call.getCallId()));
            } else {
                //Audio
                mContext.startActivity(new Intent(mContext, AudioIncomingCallActivity.class)
                        .putExtra("call_id", call.getCallId()));
            }
        }
    }

}
