package com.chat.zipchat.clone.Common;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.chat.zipchat.clone.Activity.AudioCallActivity;
import com.chat.zipchat.clone.Activity.AudioIncomingCallActivity;
import com.chat.zipchat.clone.Activity.MainActivity;
import com.chat.zipchat.clone.Activity.VideoCallActivity;
import com.chat.zipchat.clone.Activity.VideoIncomingCallActivity;
import com.chat.zipchat.clone.R;
import com.chat.zipchat.clone.Service.ApiClient;
import com.chat.zipchat.clone.Service.ApiInterFace;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.chat.zipchat.clone.Common.SessionManager.KEY_ID;
import static com.chat.zipchat.clone.Common.SessionManager.PREF_NAME;

public class BaseClass {

    public static final int RequestPermissionCode = 1;
    public static final int MY_REQUEST_CODE_IMAGE = 100;
    public static final int MY_REQUEST_CODE_DOCUMENT = 101;
    public static final int MY_REQUEST_CODE_VIDEO = 102;
    public static final int IMAGE_PICKER_SELECT = 103;
    public static final int DOCUMENT_PICKER_SELECT = 104;
    public static final int PLACE_PICKER_REQUEST = 105;
    public static final int PHOTO_EDIT_CODE = 106;


    public static final String NO_INTERNET = "Check your Internet Connection !";
    public static final String ACCESS_DENIED = "You are logged in from another device!";
    public static final String PUBLISHABLE_KEY = "pk_test_uzFnOtl3tNwStqKIi5Vflq61";

    public static final String GIPHY_API_KEY = "FvGC0dw5SVfgPlemmWoWrADegGqZTRgU";

    public static final String HOME_FRAGMENT = "HOME_FRAGMENT";
    public static final String OTHER_FRAGMENT = "OTHER_FRAGMENT";

    public static final String PhotoDirectoryPath = Environment.getExternalStorageDirectory() + "/WhatsApp Clone/Photos";
    public static final String VideoDirectoryPath = Environment.getExternalStorageDirectory() + "/WhatsApp Clone/Videos";
    public static final String DocumentDirectoryPath = Environment.getExternalStorageDirectory() + "/WhatsApp Clone/Documents";
    public static final String SendAudioDirectoryPath = Environment.getExternalStorageDirectory() + "/WhatsApp Clone/Voice";

    public static final String DOLLAR_SYMBOL = "$ ";

    public static Dialog mProgressDialog, mPopupDoneDialog;
    public static ApiInterFace apiInterface = ApiClient.getClient().create(ApiInterFace.class);
    public static ApiInterFace apiInterfaceConvertion = ApiClient.getClientConvertion().create(ApiInterFace.class);
    public static ApiInterFace apiInterfacePayment = ApiClient.getClientPayment().create(ApiInterFace.class);
    public static ApiInterFace apiInterfaceGif = ApiClient.getClientGif().create(ApiInterFace.class);

    private static AudioTrack mProgressTone;
    private final static int SAMPLE_RATE = 16000;
    public static final String NOTIFICATION_CHANNEL_ID = "1001";

    public static void ShowHideKeyboard(Activity activity, boolean isHide, EditText mTxtMessage) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            if (isHide) {
                imm.hideSoftInputFromWindow(mTxtMessage.getApplicationWindowToken(), 0);
            } else {
                mTxtMessage.requestFocus();
                imm.showSoftInput(mTxtMessage, 0);
            }
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static void myToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void myCenterToast(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void myLog(String hint, String msg) {

        boolean SHOW_LOG = true;

        if (SHOW_LOG) {
            Log.e("Arun", hint + " " + msg);
        }
    }

    public static SessionManager sessionManager(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        return sessionManager;
    }

    public static SharedPreferences sharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
    }

    public static SharedPreferences defaultSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String UserId(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String userId = sharedpreferences.getString(KEY_ID, null);
        return userId;
//        return "5cc6ac5956274702c74ebbbb";
    }

    public static String DeviceToken() {
        String device_token = FirebaseInstanceId.getInstance().getToken();
        return device_token;
    }

    public static void logout_User(Context context) {

        App.getmInstance().daoMaster.dropAllTables(App.getmInstance().daoSession.getDatabase(), true);
        App.getmInstance().daoMaster.createAllTables(App.getmInstance().daoSession.getDatabase(), true);

        SessionManager sessionManager = new SessionManager(context.getApplicationContext());
        sessionManager.logoutUser();
    }

    public static boolean eMailValidation(String emailstring) {
        if (null == emailstring || emailstring.length() == 0) {
            return false;
        }
        Pattern emailPattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher emailMatcher = emailPattern.matcher(emailstring);
        return emailMatcher.matches();
    }

    public static void snackbar(Context context, View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View sbview = snackbar.getView();
        sbview.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        TextView textView = (TextView) sbview.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(context.getResources().getColor(android.R.color.white));
        snackbar.show();
    }

    public static void showSimpleProgressDialog(Context context) {
        if (context != null) {


            mProgressDialog = new Dialog(context, R.style.DialogThemeforview_pop);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            mProgressDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setContentView(R.layout.animation_loading);
            mProgressDialog.show();
        }
    }

    public static void removeProgressDialog() {
        if (null != mProgressDialog)
            mProgressDialog.dismiss();
    }

    public static String ConvertedDateTime() {
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
        return mdformat.format(CurrentDateTime());
    }

    public static Date CurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static void Invitefriend(Context context) {

        String value = "Hey,\n" +
                "\n" +
                "ZipChat Messenger is a fast, simple and secure app that I use to message and make payment the people I care about.\n" +
                "\n" +
                "Get it for free at https://play.google.com/store/apps/details?id=com.chat.zipchat.clone";

        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(android.content.Intent.EXTRA_SUBJECT, "ZipChat");
        share.putExtra(android.content.Intent.EXTRA_TEXT, value);
        context.startActivity(Intent.createChooser(share, "Invite via"));
    }

    public static String getRealPathFromURI(Context mContent, Uri contentURI) {
        String result;
        Cursor cursor = mContent.getContentResolver().query(contentURI, null,
                null, null, null);

        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static void PopupDone(Context context) {
        mPopupDoneDialog = new Dialog(context);
        mPopupDoneDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mPopupDoneDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mPopupDoneDialog.setContentView(R.layout.popup_transaction);
//        mPopupDoneDialog.setCancelable(false);
        mPopupDoneDialog.show();
    }

    public static String PaymentConvertedDateTime(String date) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date testDate = null;
        try {
            testDate = format.parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy & hh:mm a", Locale.US);
        formatter.setTimeZone(TimeZone.getDefault());
        String newFormat = formatter.format(testDate);

        return newFormat;
    }

    public static String PopUpConvertedDateTime(String date) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date testDate = null;
        try {
            testDate = format.parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy 'at' hh:mm a", Locale.US);
        formatter.setTimeZone(TimeZone.getDefault());
        String newFormat = formatter.format(testDate);

        return newFormat;
    }

    public static float DecimalConvertion(float value) {
        return Float.valueOf(new DecimalFormat("##.#######").format(value));
    }

    public static String mImageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imagByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imagByte, Base64.DEFAULT);
    }

    public static RequestOptions requestOptionsD() {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.defult_user);

        return requestOptions;
    }

    public static RequestOptions requestOptionsT() {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.thumbnail_photo);

        return requestOptions;
    }

    public static RequestOptions requestOptionsTv() {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.isMemoryCacheable();
        requestOptions.error(R.drawable.thumbnail_video);

        return requestOptions;
    }

    public static void playProgressTone(Context mContext) {
        stopProgressTone();
        try {
            mProgressTone = createProgressTone(mContext);
            mProgressTone.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopProgressTone() {
        if (mProgressTone != null) {
            mProgressTone.stop();
            mProgressTone.release();
            mProgressTone = null;
        }
    }

    private static AudioTrack createProgressTone(Context context) throws IOException {
        AssetFileDescriptor fd = context.getResources().openRawResourceFd(R.raw.progress_tone);
        int length = (int) fd.getLength();

        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, length, AudioTrack.MODE_STATIC);

        byte[] data = new byte[length];
        readFileToBytes(fd, data);

        audioTrack.write(data, 0, data.length);
        audioTrack.setLoopPoints(0, data.length / 2, 30);

        return audioTrack;
    }

    private static void readFileToBytes(AssetFileDescriptor fd, byte[] data) throws IOException {
        FileInputStream inputStream = fd.createInputStream();

        int bytesRead = 0;
        while (bytesRead < data.length) {
            int res = inputStream.read(data, bytesRead, (data.length - bytesRead));
            if (res == -1) {
                break;
            }
            bytesRead += res;
        }
    }

    /**
     * TODO
     *
     * @param context - to access the default functions
     * @param type    - returns type of audio/video call
     * @param strName - name of the recipient
     * @param callId  - returns the call id of current call
     * @apiNote - function to create custom notification for missed call alert
     */
    public static void customNotification(Context context, String type, String strName, String callId) {
        Intent intent = null;
        if (type.contains("Calling")) {
            type = "Calling..";
            intent = new Intent(context, AudioIncomingCallActivity.class);
            intent.putExtra("call_id", callId);
            intent.addFlags(Intent.FLAG_FROM_BACKGROUND | Intent.FLAG_ACTIVITY_NEW_TASK);
        } else if (type.contains("Incoming voice")) {
            intent = new Intent(context, AudioIncomingCallActivity.class);
            intent.putExtra("call_id", callId);
            intent.addFlags(Intent.FLAG_FROM_BACKGROUND | Intent.FLAG_ACTIVITY_NEW_TASK);
        } else if (type.contains("Outgoing voice")) {
            type = "Ringing..";
            intent = new Intent(context, AudioCallActivity.class);
            intent.putExtra("call_id", callId);
            intent.addFlags(Intent.FLAG_FROM_BACKGROUND | Intent.FLAG_ACTIVITY_NEW_TASK);
        } else if (type.contains("Incoming video")) {
            intent = new Intent(context, VideoIncomingCallActivity.class);
            intent.putExtra("call_id", callId);
            intent.addFlags(Intent.FLAG_FROM_BACKGROUND | Intent.FLAG_ACTIVITY_NEW_TASK);
        } else if (type.contains("Outgoing video")) {
            type = "Ringing..";
            intent = new Intent(context, VideoCallActivity.class);
            intent.putExtra("call_id", callId);
            intent.addFlags(Intent.FLAG_FROM_BACKGROUND | Intent.FLAG_ACTIVITY_NEW_TASK);
        } else if (type.contains("Ongoing voice")) {
            type = "Ongoing voice call..";
            intent = new Intent(context, AudioCallActivity.class);
            intent.putExtra("call_id", callId);
            intent.addFlags(Intent.FLAG_FROM_BACKGROUND | Intent.FLAG_ACTIVITY_NEW_TASK);
        } else if (type.contains("Ongoing video")) {
            type = "Ongoing video call..";
            intent = new Intent(context, VideoCallActivity.class);
            intent.putExtra("call_id", callId);
            intent.addFlags(Intent.FLAG_FROM_BACKGROUND | Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_FROM_BACKGROUND | Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        Notification.Builder mBuilder = null;
        if (type.contains("Ongoing")) {
            mBuilder = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_call_missed)
                    .setContentTitle(strName)
                    .setStyle(new Notification.BigTextStyle()
                            .bigText(type))
                    .setVibrate(new long[]{100, 500})
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setContentText(type);
        } else {
            mBuilder = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_call_missed)
                    .setContentTitle(strName)
                    .setStyle(new Notification.BigTextStyle()
                            .bigText(type))
                    .setVibrate(new long[]{100, 500})
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setAutoCancel(true)
                    .setContentText(type);
        }

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

    /**
     * TODO
     *
     * @param context - to get default function using activity context
     */
    public static void cancelNotification(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static Uri mLoadAudioPath(String mAudioUrl) {

        File dir = new File(SendAudioDirectoryPath);
        File[] listFile = dir.listFiles();

        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                if (mAudioUrl.contains(listFile[i].getName())) {
                    return Uri.parse(listFile[0].getAbsolutePath());
                }
            }
        }
        return null;
    }

    public static boolean fileExist(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static String getStaticMap(String mLatLng) {
        String location = "http://maps.googleapis.com/maps/api/staticmap?center="
                + mLatLng
                + "&zoom=18"
                + "&maptype=roadmap&size=600x300&sensor=false&key=AIzaSyAwi5mHIaHg9nEkMS2CnFo6dBv4Ty7Rgq8";
        return location;
    }

}
