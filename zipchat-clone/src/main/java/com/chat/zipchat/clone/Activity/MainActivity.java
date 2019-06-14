package com.chat.zipchat.clone.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chat.zipchat.clone.Activity.Group.SelectGroupMemberActivity;
import com.chat.zipchat.clone.Common.App;
import com.chat.zipchat.clone.Common.OnClearFromRecentService;
import com.chat.zipchat.clone.Fragment.ContactsFragment;
import com.chat.zipchat.clone.Fragment.HomeFragment;
import com.chat.zipchat.clone.Fragment.PaymentFragment;
import com.chat.zipchat.clone.Fragment.ProfileFragment;
import com.chat.zipchat.clone.Fragment.SettingsFragment;
import com.chat.zipchat.clone.Model.Contact.ContactItemRequest;
import com.chat.zipchat.clone.Model.Contact.ContactRequest;
import com.chat.zipchat.clone.Model.Contact.ContactResponse;
import com.chat.zipchat.clone.Model.Contact.ResultItem;
import com.chat.zipchat.clone.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.reflect.TypeToken;
import com.sinch.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.chat.zipchat.clone.Common.BaseClass.ConvertedDateTime;
import static com.chat.zipchat.clone.Common.BaseClass.HOME_FRAGMENT;
import static com.chat.zipchat.clone.Common.BaseClass.Invitefriend;
import static com.chat.zipchat.clone.Common.BaseClass.OTHER_FRAGMENT;
import static com.chat.zipchat.clone.Common.BaseClass.UserId;
import static com.chat.zipchat.clone.Common.BaseClass.apiInterface;
import static com.chat.zipchat.clone.Common.BaseClass.isOnline;
import static com.chat.zipchat.clone.Common.BaseClass.myLog;
import static com.chat.zipchat.clone.Common.BaseClass.requestOptionsD;
import static com.chat.zipchat.clone.Common.BaseClass.sessionManager;
import static com.chat.zipchat.clone.Common.BaseClass.sharedPreferences;
import static com.chat.zipchat.clone.Common.MarshmallowPermission.PERMISSIONS;
import static com.chat.zipchat.clone.Common.MarshmallowPermission.PERMISSION_ALL;
import static com.chat.zipchat.clone.Common.MarshmallowPermission.hasPermissions;
import static com.chat.zipchat.clone.Common.SessionManager.KEY_PROFILE_PIC;
import static com.chat.zipchat.clone.Common.SessionManager.KEY_USERNAME;
import static com.chat.zipchat.clone.Common.SessionManager.PHONE;
import static com.chat.zipchat.clone.Common.SessionManager.STATUS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private TextView mToolbarTitle;
    private DrawerLayout drawer;
    private ArrayList<ContactItemRequest> mListContact;
    public String currentFragment = HOME_FRAGMENT;
    public ImageView mImgUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (!hasPermissions(this)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        mToolbarTitle = findViewById(R.id.mToolbarTitle);
        drawer = findViewById(R.id.drawer_layout);
        ImageView imgCreateGroup = findViewById(R.id.img_create_group);
        imgCreateGroup.setOnClickListener(this);

        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        App.getmInstance().startSinch(this, sharedPreferences(this).getString(KEY_USERNAME, null));

        if (null != getIntent().getStringExtra("push_data")) {
            String strPushData = getIntent().getStringExtra("push_data");
            Map<String, String> mapPushData = new Gson().fromJson(strPushData, new TypeToken<Map<String, String>>() {
            }.getType());
            try {
                App.getmInstance().sinchClient.relayRemotePushNotificationPayload(mapPushData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.menu_icon, getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

        View mView = navigationView.getHeaderView(0);
        TextView mTxtUserName = mView.findViewById(R.id.mTxtUserName);
        mImgUser = mView.findViewById(R.id.mImgUser);
        mImgUser.setOnClickListener(this);

        Glide.with(this).setDefaultRequestOptions(requestOptionsD()).load(sharedPreferences(this).getString(KEY_PROFILE_PIC, null)).into(mImgUser);
        mTxtUserName.setText(sharedPreferences(this).getString(KEY_USERNAME, null));
        addFragment(new HomeFragment());
        mListContact = new ArrayList<>();

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        item.setChecked(true);

        if (id == R.id.nav_chats) {
            mToolbarTitle.setText(getResources().getText(R.string.chats));
            addFragment(new HomeFragment());
        } else if (id == R.id.nav_contacts) {
            mToolbarTitle.setText(getResources().getText(R.string.contacts));
            addFragment(new ContactsFragment(this));
        } else if (id == R.id.nav_profile) {
            mToolbarTitle.setText(getResources().getText(R.string.profile));
            addFragment(new ProfileFragment(this));
        } else if (id == R.id.nav_settings) {
            mToolbarTitle.setText(getResources().getText(R.string.settings));
            addFragment(new SettingsFragment(this));
        } else if (id == R.id.nav_payments) {
            mToolbarTitle.setText(getResources().getText(R.string.payments));
            addFragment(new PaymentFragment(this));
        } else if (id == R.id.nav_invite_friend) {
            Invitefriend(this);
        } else if (id == R.id.nav_help) {
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        DatabaseReference userLastOnlineRef = FirebaseDatabase.getInstance().getReference("user-details").child(UserId(this)).child("profile-details");

        if (isOnline(this)) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("isOnline", "1");
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

            userLastOnlineRef.updateChildren(map);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isOnline(this)) {
            if (!hasPermissions(this)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            } else {
                GetContactsIntoArrayList();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.mImgUser) {
            mToolbarTitle.setText(getResources().getText(R.string.profile));
            addFragment(new ProfileFragment(this));
            drawer.closeDrawer(GravityCompat.START);
        } else if (i == R.id.img_create_group) {
            startActivity(new Intent(this, SelectGroupMemberActivity.class).putExtra("isFromMain", true));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < permissions.length; i++) {
            if (permissions[i].equals(Manifest.permission.READ_CONTACTS)) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    GetContactsIntoArrayList();
                    return;
                }
            }
        }
    }

    public void GetContactsIntoArrayList() {

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                //plus any other properties you wish to query
        };

        Cursor cursor = null;
        try {
            cursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        } catch (SecurityException e) {
            //SecurityException can be thrown if we don't have the right permissions
        }


        if (cursor != null) {
            try {
                HashSet<String> normalizedNumbersAlreadyFound = new HashSet<>();
                int indexOfNormalizedNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER);
                int indexOfDisplayName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int indexOfDisplayNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                mListContact.clear();
                while (cursor.moveToNext()) {

                    String normalizedNumber = cursor.getString(indexOfNormalizedNumber);

                    if (normalizedNumbersAlreadyFound.add(normalizedNumber)) {

                        String displayName = cursor.getString(indexOfDisplayName);
                        String displayNumber = cursor.getString(indexOfDisplayNumber);
                        String newDisplayNumber = displayNumber.replaceAll("[^0-9]", "");

                        ContactItemRequest contactItem = new ContactItemRequest();
                        contactItem.setName(displayName);
                        contactItem.setMobileNumber(newDisplayNumber);
                        mListContact.add(contactItem);

                        //haven't seen this number yet: do something with this contact!
                    } else {
                        //don't do anything with this contact because we've already found this number
                    }
                }
                SyncContact();

            } finally {
                cursor.close();
            }
        }
    }

    private void SyncContact() {
        if (isOnline(this)) {
            ContactRequest contactRequest = new ContactRequest();
            contactRequest.setContact(mListContact);
            contactRequest.setDeviceType("ANDROID");

            Call<ContactResponse> contactResponseCall = apiInterface.contactDetails(contactRequest);
            contactResponseCall.enqueue(new Callback<ContactResponse>() {
                @Override
                public void onResponse(Call<ContactResponse> call, final Response<ContactResponse> response) {
                    if (response.isSuccessful()) {
                        App.getmInstance().contactResponseDao.deleteAll();
                        App.getmInstance().resultItemDao.deleteAll();
                        if (response.body() != null) {
                            ContactResponse contactResponse = response.body();
                            contactResponse.contact_id = 1L;
                            App.getmInstance().contactResponseDao.insertOrReplace(contactResponse);
                            if (response.body().getResult().size() > 0) {
                                for (ResultItem con : contactResponse.getResult()) {
                                    if (!con.getId().equalsIgnoreCase(UserId(MainActivity.this))) {
                                        con.setContact_id(contactResponse.contact_id);
                                        con.setIsFromContact("1");
                                        con.setIsSelected(false);
                                        App.getmInstance().resultItemDao.insertOrReplace(con);
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ContactResponse> call, Throwable t) {
                    myLog("onFailure: ", t.toString());
                }
            });

        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (currentFragment.equals(OTHER_FRAGMENT)) {
            currentFragment = HOME_FRAGMENT;
            addFragment(new HomeFragment());
        } else {
            super.onBackPressed();
            setResult(Activity.RESULT_CANCELED);
            finishAffinity();
        }
    }

    public void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.frame, fragment);
        ft.commitAllowingStateLoss();
    }

}
