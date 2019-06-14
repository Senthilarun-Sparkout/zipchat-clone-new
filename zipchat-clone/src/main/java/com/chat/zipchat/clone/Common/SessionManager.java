package com.chat.zipchat.clone.Common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.chat.zipchat.clone.Activity.MainActivity;
import com.chat.zipchat.clone.Activity.SigninActivity;

public class SessionManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    public static final String PREF_NAME = "Arun";
    private static final String IS_LOGIN = "IsLoggedIn";
    static final String KEY_ID = "id";
    private static final String ACCESS_TOKEN = "access_token";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PROFILE_PIC = "profile_pic";
    public static final String PHONE = "phone";
    public static final String PHONE_CODE = "phone_code";
    public static final String STATUS = "status";
    private static final String STELLAR_ADDRESS = "stellar_address";
    private static final String STELLAR_SEED = "stellar_seed";

    private static Boolean IS_WALLPAPER_SET = false;
    private static Boolean DOWNLOAD_ONLY_WIFI = false;
    private static Boolean HIDE_LAST_SEEN = false;


    SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, 0);
        editor = pref.edit();
    }

    public void createLoginSession(String id, String username, String profile_pic,
                                   String access_token, String phone, String phone_code, String status, String stellar_address, String stellar_seed) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PROFILE_PIC, profile_pic);
        editor.putString(ACCESS_TOKEN, access_token);
        editor.putString(PHONE, phone);
        editor.putString(PHONE_CODE, phone_code);
        editor.putString(STATUS, status);
        editor.putString(STELLAR_ADDRESS, stellar_address);
        editor.putString(STELLAR_SEED, stellar_seed);
        editor.commit();
    }

    public void checkLogin() {

        if (this.isLoggedIn()) {
            Intent i = new Intent(_context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    void logoutUser() {
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, SigninActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    private boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void setWallpaper(boolean follow) {
        IS_WALLPAPER_SET = follow;
        PreferenceManager.getDefaultSharedPreferences(_context).edit().putBoolean("IS_WALLPAPER_SET", IS_WALLPAPER_SET).apply();
    }

    public boolean isWallpaperSet() {
        IS_WALLPAPER_SET = PreferenceManager.getDefaultSharedPreferences(_context).getBoolean("IS_WALLPAPER_SET", IS_WALLPAPER_SET);
        return IS_WALLPAPER_SET;
    }

    public void setDownloadOnlyWifi(boolean follow) {
        DOWNLOAD_ONLY_WIFI = follow;
        PreferenceManager.getDefaultSharedPreferences(_context).edit().putBoolean("DOWNLOAD_ONLY_WIFI", DOWNLOAD_ONLY_WIFI).apply();
    }

    public boolean isDownloadOnlyWifi() {
        DOWNLOAD_ONLY_WIFI = PreferenceManager.getDefaultSharedPreferences(_context).getBoolean("DOWNLOAD_ONLY_WIFI", DOWNLOAD_ONLY_WIFI);
        return DOWNLOAD_ONLY_WIFI;
    }

    public void HideLastSeen(boolean hide_last_seen) {
        HIDE_LAST_SEEN = hide_last_seen;
        PreferenceManager.getDefaultSharedPreferences(_context).edit().putBoolean("HIDE_LAST_SEEN", HIDE_LAST_SEEN).apply();
    }

    public boolean isHideLastSeen() {
        HIDE_LAST_SEEN = PreferenceManager.getDefaultSharedPreferences(_context).getBoolean("HIDE_LAST_SEEN", HIDE_LAST_SEEN);
        return HIDE_LAST_SEEN;
    }

}
