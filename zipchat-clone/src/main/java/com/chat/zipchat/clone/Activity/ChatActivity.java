package com.chat.zipchat.clone.Activity;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chat.zipchat.clone.Activity.Payments.SendMoneyActivity;
import com.chat.zipchat.clone.Activity.PhotoEdit.PhotoEditActivity;
import com.chat.zipchat.clone.Adapter.ChatAdapter;
import com.chat.zipchat.clone.Adapter.GifAdapter;
import com.chat.zipchat.clone.Adapter.StickerAdapter;
import com.chat.zipchat.clone.Common.App;
import com.chat.zipchat.clone.Common.BaseClass;
import com.chat.zipchat.clone.Common.ImageFilePath;
import com.chat.zipchat.clone.Common.RecyclerItemClickListener;
import com.chat.zipchat.clone.Model.AcceptRejectPojo;
import com.chat.zipchat.clone.Model.AcceptRejectPojoDao;
import com.chat.zipchat.clone.Model.Chat.ChatPojo;
import com.chat.zipchat.clone.Model.Chat.ChatPojoDao;
import com.chat.zipchat.clone.Model.ChatList.ChatListPojo;
import com.chat.zipchat.clone.Model.ChatList.ChatListPojoDao;
import com.chat.zipchat.clone.Model.Contact.ResultItem;
import com.chat.zipchat.clone.Model.Contact.ResultItemDao;
import com.chat.zipchat.clone.Model.Download.LocalDataPojo;
import com.chat.zipchat.clone.Model.Download.LocalDataPojoDao;
import com.chat.zipchat.clone.Model.GifStickers.GifResponse;
import com.chat.zipchat.clone.Model.GifStickers.StickerResponse;
import com.chat.zipchat.clone.Model.Notification.NotificationRequest;
import com.chat.zipchat.clone.Model.Notification.NotificationResponse;
import com.chat.zipchat.clone.Model.ProfileImageUpdate.ProfileImageResponse;
import com.chat.zipchat.clone.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.commons.lang.StringUtils;
import org.greenrobot.greendao.query.DeleteQuery;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.chat.zipchat.clone.Common.BaseClass.ConvertedDateTime;
import static com.chat.zipchat.clone.Common.BaseClass.CurrentDateTime;
import static com.chat.zipchat.clone.Common.BaseClass.DOCUMENT_PICKER_SELECT;
import static com.chat.zipchat.clone.Common.BaseClass.GIPHY_API_KEY;
import static com.chat.zipchat.clone.Common.BaseClass.IMAGE_PICKER_SELECT;
import static com.chat.zipchat.clone.Common.BaseClass.MY_REQUEST_CODE_DOCUMENT;
import static com.chat.zipchat.clone.Common.BaseClass.MY_REQUEST_CODE_IMAGE;
import static com.chat.zipchat.clone.Common.BaseClass.MY_REQUEST_CODE_VIDEO;
import static com.chat.zipchat.clone.Common.BaseClass.PHOTO_EDIT_CODE;
import static com.chat.zipchat.clone.Common.BaseClass.PLACE_PICKER_REQUEST;
import static com.chat.zipchat.clone.Common.BaseClass.ShowHideKeyboard;
import static com.chat.zipchat.clone.Common.BaseClass.UserId;
import static com.chat.zipchat.clone.Common.BaseClass.VideoDirectoryPath;
import static com.chat.zipchat.clone.Common.BaseClass.apiInterface;
import static com.chat.zipchat.clone.Common.BaseClass.apiInterfaceGif;
import static com.chat.zipchat.clone.Common.BaseClass.apiInterfacePayment;
import static com.chat.zipchat.clone.Common.BaseClass.defaultSharedPreferences;
import static com.chat.zipchat.clone.Common.BaseClass.fileExist;
import static com.chat.zipchat.clone.Common.BaseClass.isOnline;
import static com.chat.zipchat.clone.Common.BaseClass.myLog;
import static com.chat.zipchat.clone.Common.BaseClass.myToast;
import static com.chat.zipchat.clone.Common.BaseClass.requestOptionsD;
import static com.chat.zipchat.clone.Common.BaseClass.sessionManager;
import static com.chat.zipchat.clone.Common.BaseClass.snackbar;
import static okhttp3.MediaType.parse;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar mToolbarChat;
    ImageView mImgBackChat, mImgEmoji, mImgSend, mImgAdd, mImgRecAudio, mImgRecVideo;
    CircleImageView mImgContact;
    TextView mTxtContactName, mTxtStatus;
    RecyclerView mRvChat;
    EditText mTxtMessage;
    LinearLayout Ll_text;
    RelativeLayout mLayoutChat, mRlToolbarChat, mPaymentsRl, mPhotosRl, mDocumentRl, mLocationRl, rlLocationGif, rlRecordVideo;
    List<ChatPojo> chatPojoList;
    DatabaseReference referenceUser, referenceMessage, referenceOnline;
    DatabaseReference referenceMessageInsert, referenceUserInsert;
    String toId;
    BottomSheetBehavior behavior;
    View mViewBg, bottomSheet;
    ChatAdapter chatAdapter;

    View mViewChat;
    AppBarLayout mAppBarLayoutChat;

    int value = -1;
    SearchView mSearchView;

    public static ArrayList<Integer> mListSearch;
    List<ResultItem> resultItems;

    int notifyIntent = 0;

    ActionMode mActionMode;
    MenuItem editChat, replyChat, forwardChat;
    boolean isMultiSelect = false;
    boolean isEditedMsg = false;

    ArrayList<ChatPojo> multiselect_list = new ArrayList<>();

    @Nullable
    private MediaRecorder myAudioRecorder;
    private String outputFile = "";
    private String time = "";

    View mKeyBoardGifStrikers;
    ImageView mImgIconGifStickers;
    ProgressBar mProgressGifStrikers;
    RecyclerView mRvGif, mRvStrikers;

    ImageView mImgBtnGif, mImgBtnStickers, mImgCancel;

    LinearLayout llChatCalls, llWaitingResponse;

    List<AcceptRejectPojo> acceptRejectPojoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mToolbarChat = findViewById(R.id.mToolbarChat);
        setSupportActionBar(mToolbarChat);

        mTxtContactName = findViewById(R.id.mTxtContactName);
        mTxtStatus = findViewById(R.id.mTxtStatus);
        mImgBackChat = findViewById(R.id.mImgBackChat);
        mImgEmoji = findViewById(R.id.mImgEmoji);
        mImgSend = findViewById(R.id.mImgSend);
        mImgContact = findViewById(R.id.mImgContact);
        mRvChat = findViewById(R.id.mRvChat);
        mTxtMessage = findViewById(R.id.mTxtMessage);
        Ll_text = findViewById(R.id.Ll_text);
        mRlToolbarChat = findViewById(R.id.mRlToolbarChat);
        mImgAdd = findViewById(R.id.mImgAdd);
        mImgRecAudio = findViewById(R.id.mImgRecAudio);
        mImgRecVideo = findViewById(R.id.mImgRecVideo);
        mImgCancel = findViewById(R.id.mImgCancel);

        mViewBg = findViewById(R.id.mViewBg);
        mPaymentsRl = findViewById(R.id.mPaymentsRl);
        mPhotosRl = findViewById(R.id.mPhotosRl);
        mDocumentRl = findViewById(R.id.mDocumentRl);
        mLocationRl = findViewById(R.id.mLocationRl);
        mLayoutChat = findViewById(R.id.mLayoutChat);
        mAppBarLayoutChat = findViewById(R.id.mAppBarLayoutChat);
        mViewChat = findViewById(R.id.mViewChat);
        llWaitingResponse = findViewById(R.id.ll_waiting_response);

        rlLocationGif = findViewById(R.id.rl_location_gif);
        rlRecordVideo = findViewById(R.id.rl_record_video);
        rlLocationGif.setOnClickListener(this);
        rlRecordVideo.setOnClickListener(this);

        mTxtStatus.setSelected(true);

        AppCompatImageView ivCall = findViewById(R.id.iv_call);
        AppCompatImageView ivVideoCall = findViewById(R.id.iv_video_call);
        llChatCalls = findViewById(R.id.ll_chat_calls);

        mImgBackChat.setOnClickListener(this);
        mImgEmoji.setOnClickListener(this);
        mImgSend.setOnClickListener(this);
        mRlToolbarChat.setOnClickListener(this);
        mTxtMessage.setOnClickListener(this);
        mImgAdd.setOnClickListener(this);
        mPaymentsRl.setOnClickListener(this);
        mPhotosRl.setOnClickListener(this);
        mDocumentRl.setOnClickListener(this);
        mLocationRl.setOnClickListener(this);
        mImgRecAudio.setOnClickListener(this);
        mImgRecVideo.setOnClickListener(this);
        ivCall.setOnClickListener(this);
        ivVideoCall.setOnClickListener(this);
        mImgCancel.setOnClickListener(this);

        mListSearch = new ArrayList<>();

        Intent intent = getIntent();
        HashMap<String, String> hashMap = (HashMap<String, String>) intent.getSerializableExtra("notifyHashMap");

        if (null != hashMap) {
            notifyIntent = 1;
            toId = hashMap.get("user");

        } else {
            toId = getIntent().getStringExtra("toId");
        }

        chatPojoList = App.getmInstance().chatPojoDao.queryBuilder().where(ChatPojoDao.Properties.FriendId.eq(toId)).orderAsc(ChatPojoDao.Properties.Timestamp).list();
        acceptRejectPojoList = App.getmInstance().acceptRejectPojoDao.queryBuilder().where(AcceptRejectPojoDao.Properties.FriendId.eq(toId)).list();


      /*  Collections.sort(chatPojoList, new Comparator<ChatPojo>() {
            @Override
            public int compare(ChatPojo o1, ChatPojo o2) {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }

        });*/

        resultItems = App.getmInstance().resultItemDao.queryBuilder().where(
                ResultItemDao.Properties.Id.eq(toId)).list();

        if (resultItems.size() > 0) {

            if (resultItems.get(0).getIsFromContact().equalsIgnoreCase("1")) {
                mTxtContactName.setText(resultItems.get(0).getName());
            } else {
                mTxtContactName.setText(resultItems.get(0).getMobile_number());
            }
            Glide.with(this).setDefaultRequestOptions(requestOptionsD()).load(resultItems.get(0).getProfile_picture()).into(mImgContact);
        } else {
            mTxtContactName.setText("Unknown");
        }


        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        mRvChat.setLayoutManager(mLayoutManager);

        chatAdapter = new ChatAdapter(ChatActivity.this, chatPojoList);
        mRvChat.setAdapter(chatAdapter);

        referenceUser = FirebaseDatabase.getInstance().getReference("user-messages").child(UserId(this));
        referenceUser.addValueEventListener(valueEventListenerUserChat);

        referenceOnline = FirebaseDatabase.getInstance().getReference("user-details").child(toId).child("profile-details");
        referenceOnline.addValueEventListener(valueEventListenerOnline);

        referenceMessageInsert = FirebaseDatabase.getInstance().getReference("messages");

        mTxtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditedMsg) {
                   /* if (mTxtMessage.getText().toString().trim().length() == 0) {
                        isEditedMsg = false;
                        mImgCancel.setVisibility(View.GONE);
                        mImgSend.setVisibility(View.GONE);
                        mImgRecAudio.setVisibility(View.VISIBLE);
                    } else*/
                    if (multiselect_list.get(0).getText().equals(mTxtMessage.getText().toString()) || mTxtMessage.getText().toString().trim().length() == 0) {
                        mImgCancel.setVisibility(View.VISIBLE);
                        mImgSend.setVisibility(View.GONE);
                        mImgRecAudio.setVisibility(View.GONE);
                    } else {
                        mImgCancel.setVisibility(View.GONE);
                        mImgSend.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mTxtMessage.getText().toString().trim().length() == 0) {
                        mImgSend.setVisibility(View.GONE);
                        mImgRecAudio.setVisibility(View.VISIBLE);
                        llChatCalls.setVisibility(View.VISIBLE);
                    } else {
                        mImgSend.setVisibility(View.VISIBLE);
                        mImgRecAudio.setVisibility(View.GONE);
                        llChatCalls.setVisibility(View.GONE);
                    }
                }
            }
        });

        bottomSheet = findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                    mViewBg.setVisibility(View.GONE);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                mViewBg.setVisibility(View.VISIBLE);
                mViewBg.setAlpha(slideOffset);
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (!sessionManager(this).isWallpaperSet()) {
            mRvChat.setBackground(getResources().getDrawable(R.drawable.chat_background));
        } else {
            String mImageUri = defaultSharedPreferences(this).getString("wallpaper", null);
            Glide.with(ChatActivity.this)
                    .load(Uri.parse(mImageUri))
                    .error(R.drawable.chat_background)
                    .into(new CustomTarget<Drawable>() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            mRvChat.setBackground(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });

        }

        mRvChat.addOnItemTouchListener(new RecyclerItemClickListener(this, mRvChat,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (isMultiSelect) {
                            multi_select(position);
                        }
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        if (!isMultiSelect) {
                            multiselect_list = new ArrayList<>();
                            isMultiSelect = true;

                            if (mActionMode == null) {
                                mActionMode = startActionMode(mActionModeCallback);
                            }
                        }
                        multi_select(position);
                    }
                }));

        mImgRecAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        myToast(ChatActivity.this, "Keep holding to Continue Recording.");
                        ChatActivity.this.startRecording();
                        return true;
                    case MotionEvent.ACTION_UP:
                        myToast(ChatActivity.this, "Posting your Recorded Voice Comment.");
                        ChatActivity.this.stopRecording();
                        break;
                }
                return false;
            }
        });

        mKeyBoardGifStrikers = findViewById(R.id.mKeyBoardGifStrikers);
        mImgIconGifStickers = findViewById(R.id.mImgIconGifStickers);
        mProgressGifStrikers = findViewById(R.id.mProgressGifStrikers);
        mImgIconGifStickers.setOnClickListener(this);

        mRvGif = findViewById(R.id.mRvGif);
        mRvGif.setLayoutManager(new GridLayoutManager(this, 2));

        mRvStrikers = findViewById(R.id.mRvStrikers);
        mRvStrikers.setLayoutManager(new GridLayoutManager(this, 4));

        mImgBtnGif = findViewById(R.id.mImgBtnGif);
        mImgBtnStickers = findViewById(R.id.mImgBtnStickers);

        mImgBtnGif.setOnClickListener(this);
        mImgBtnStickers.setOnClickListener(this);

        mTxtMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mKeyBoardGifStrikers.getVisibility() == View.VISIBLE) {
                    ShowHideKeyboard(ChatActivity.this, false, mTxtMessage);
//                    mImgIconGifStickers.setBackground(ChatActivity.this.getResources().getDrawable(R.drawable.ic_gif));
                    mKeyBoardGifStrikers.setVisibility(View.GONE);
                }
                return false;
            }
        });

        DatabaseReference referenceFriendList = FirebaseDatabase.getInstance().getReference("user-details").child(UserId(ChatActivity.this)).child("friend-list");
        referenceFriendList.child(toId).addValueEventListener(valueEventListenerFriendList);

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_chat, menu);

        ViewGroup.LayoutParams navButtonsParams = new ViewGroup.LayoutParams(mToolbarChat.getHeight() * 2 / 3, mToolbarChat.getHeight() * 2 / 3);
        Button btnNext = new Button(this);
        Button btnPrev = new Button(this);
        btnNext.setBackground(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down));
        btnPrev.setBackground(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_white));

        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));

        ((LinearLayout) mSearchView.getChildAt(0)).addView(btnPrev, navButtonsParams);
        ((LinearLayout) mSearchView.getChildAt(0)).addView(btnNext, navButtonsParams);
        ((LinearLayout) mSearchView.getChildAt(0)).setGravity(Gravity.BOTTOM);

        ImageView searchViewIcon = (ImageView) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        ViewGroup linearLayoutSearchView = (ViewGroup) searchViewIcon.getParent();
        linearLayoutSearchView.removeView(searchViewIcon);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        mSearchView.setIconifiedByDefault(false);
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                mListSearch.clear();
                if (chatPojoList.size() > 0) {
                    chatAdapter.getSearchValues(newText);
                    for (int i = 0; i < chatPojoList.size(); i++) {

                        if (!TextUtils.isEmpty(newText)) {
                            if (chatPojoList.get(i).getText().toLowerCase().contains(newText)) {
                                mListSearch.add(i);
                            }
                        } else {
                            mListSearch.clear();
                        }
                    }

                    mRvChat.setAdapter(chatAdapter);
                }
                return true;
            }

            public boolean onQueryTextSubmit(String query) {

                myLog("mListSearch", String.valueOf(mListSearch));
                return true;
            }

        };

        mSearchView.setOnQueryTextListener(queryTextListener);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Collections.sort(mListSearch, Collections.reverseOrder());

                for (int i = 0; i < mListSearch.size(); i++) {

                    if (value == -1) {
                        value = mListSearch.get(mListSearch.size() - 1);
                        mRvChat.scrollToPosition(value);
                        break;
                    } else if (mListSearch.get(i) > value) {
                        value = mListSearch.get(i);
                        mRvChat.scrollToPosition(mListSearch.get(i));
                        break;
                    } else if (mListSearch.get(i) == mListSearch.get(mListSearch.size() - 1)) {
                        myToast(ChatActivity.this, ChatActivity.this.getResources().getString(R.string.not_found));
                        break;
                    }
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Collections.sort(mListSearch);

                for (int i = 0; i < mListSearch.size(); i++) {

                    if (value == -1) {
                        value = mListSearch.get(mListSearch.size() - 1);
                        mRvChat.scrollToPosition(value);
                        break;
                    } else if (mListSearch.get(i) < value) {
                        value = mListSearch.get(i);
                        mRvChat.scrollToPosition(mListSearch.get(i));
                        break;
                    } else if (mListSearch.get(i) == mListSearch.get(0)) {
                        myToast(ChatActivity.this, ChatActivity.this.getResources().getString(R.string.not_found));
                        break;
                    }
                }
            }
        });


        // Detect SearchView icon clicks
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLog("Enabled", String.valueOf(mSearchView.isEnabled()));
//                Ll_text.setVisibility(View.GONE);
            }
        });

        // Detect SearchView close
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
//                value = -1;
                myLog("Disabled", String.valueOf(mSearchView.isEnabled()));
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_media:
                Intent mediaIntent = new Intent(this, MediaActivity.class);
                mediaIntent.putExtra("toId", toId);
                mediaIntent.putExtra("Name", mTxtContactName.getText().toString());
                startActivity(mediaIntent);
                break;
            case R.id.menu_search:
                break;
            case R.id.menu_clrchat:
                if (isOnline(this))
                    clearChat();
                break;
            case R.id.menu_wallpaper:
                showWallPaperDialog();
                break;
            case R.id.menu_block:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mImgBackChat:
                onBackPressed();
                break;
            case R.id.mImgEmoji:
                close_dialog();
                break;
            case R.id.mTxtMessage:
                close_dialog();
                break;
            case R.id.mPaymentsRl:
                close_dialog();

                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("id", toId);
                hashMap.put("number", resultItems.get(0).getMobile_number());
                hashMap.put("type", "chat");

                Intent mSentMoney = new Intent(this, SendMoneyActivity.class);
                mSentMoney.putExtra("userDetails", hashMap);
                startActivity(mSentMoney);
                break;
            case R.id.mPhotosRl:
                imagePicker();
                break;
            case R.id.mDocumentRl:
                documentPicker();
                break;
            case R.id.mLocationRl:
                ShareLocation();
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.mImgAdd:
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    ShowHideKeyboard(this, true, mTxtMessage);
                    if (mKeyBoardGifStrikers.getVisibility() == View.VISIBLE) {
                        mKeyBoardGifStrikers.setVisibility(View.GONE);
                    }
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;
            case R.id.mImgSend:
                if (TextUtils.isEmpty(mTxtMessage.getText().toString())) {
                    myToast(this, getResources().getString(R.string.enter_message));
                } else if (isEditedMsg) {
                    EditMessage(multiselect_list.get(0));
                } else {
                    sendMsg();
                }
                break;
            case R.id.mRlToolbarChat:
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra("toId", toId);
                startActivity(intent);
                break;
            case R.id.iv_call:
                if (App.getmInstance().sinchClient != null) {
                    com.sinch.android.rtc.calling.Call call = App.getmInstance().sinchClient.getCallClient().callUser(toId);
                    String strCallId = call.getCallId();
                    startActivity(new Intent(ChatActivity.this, AudioCallActivity.class)
                            .putExtra("call_id", strCallId));
                }
                break;
            case R.id.iv_video_call:
                if (App.getmInstance().sinchClient != null) {
                    com.sinch.android.rtc.calling.Call call = App.getmInstance().sinchClient.getCallClient().callUserVideo(toId);
                    String strCallId = call.getCallId();
                    startActivity(new Intent(ChatActivity.this, VideoCallActivity.class)
                            .putExtra("call_id", strCallId));
                }
                break;
            case R.id.mImgBtnGif:
                mProgressGifStrikers.setVisibility(View.VISIBLE);
                mRvGif.setVisibility(View.GONE);
                mRvStrikers.setVisibility(View.GONE);
                GetGif();
                break;
            case R.id.mImgBtnStickers:
                mProgressGifStrikers.setVisibility(View.VISIBLE);
                mRvGif.setVisibility(View.GONE);
                mRvStrikers.setVisibility(View.GONE);
                GetStickers();
                break;
           /* case R.id.mImgIconGifStickers:
                break;*/
            case R.id.rl_location_gif:
                if (mKeyBoardGifStrikers.getVisibility() == View.VISIBLE) {
                    ShowHideKeyboard(this, false, mTxtMessage);
//                    mImgIconGifStickers.setBackground(getResources().getDrawable(R.drawable.ic_gif));
                    mKeyBoardGifStrikers.setVisibility(View.GONE);

                } else {
                    ShowHideKeyboard(this, true, mTxtMessage);
//                    mImgIconGifStickers.setBackground(getResources().getDrawable(R.drawable.ic_keyboard));
                    mProgressGifStrikers.setVisibility(View.VISIBLE);
                    mKeyBoardGifStrikers.setVisibility(View.VISIBLE);
                    mRvGif.setVisibility(View.GONE);
                    mRvStrikers.setVisibility(View.GONE);
                    GetGif();
                }
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
           /* case R.id.mImgRecVideo:
                break;*/
            case R.id.rl_record_video:
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, MY_REQUEST_CODE_VIDEO);
                }
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.mImgCancel:
                isEditedMsg = false;
                mTxtMessage.setText("");
                mImgCancel.setVisibility(View.GONE);
                mImgSend.setVisibility(View.GONE);
                mImgRecAudio.setVisibility(View.VISIBLE);
                llChatCalls.setVisibility(View.VISIBLE);
                break;
        }
    }

    ValueEventListener valueEventListenerUserChat = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (ds.getKey().equals(toId)) {


                        chatPojoList.clear();

                        for (DataSnapshot dss : ds.getChildren()) {
                            referenceMessage = FirebaseDatabase.getInstance().getReference("messages").child(dss.getKey());
                            referenceMessage.addValueEventListener(valueEventListenerMessageChat);
                        }

                        if (chatPojoList.size() < ds.getChildrenCount()) {

                           /* final DeleteQuery<ChatPojo> tableDeleteQuery = App.getmInstance().chatPojoDao.queryBuilder()
                                    .where(ChatPojoDao.Properties.FriendId.eq(toId))
                                    .buildDelete();
                            tableDeleteQuery.executeDeleteWithoutDetachingEntities();*/

                        }
                    }
                }
            } else {
                final DeleteQuery<ChatPojo> tableDeleteQuery = App.getmInstance().chatPojoDao.queryBuilder()
                        .where(ChatPojoDao.Properties.FriendId.eq(toId))
                        .buildDelete();
                tableDeleteQuery.executeDeleteWithoutDetachingEntities();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            myLog("onCancelled: ", databaseError.getMessage());
        }
    };

    ValueEventListener valueEventListenerMessageChat = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {

                ChatPojo chatPojo = new ChatPojo();
                chatPojo.setMessageId(dataSnapshot.getKey().toString());
                chatPojo.setFriendId(toId);
                chatPojo.setIsMessageSend(1);


                int value = 0;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (ds.getKey().equals("fromId")) {
                        chatPojo.setFromId(ds.getValue().toString());
                    } else if (ds.getKey().equals("text")) {
                        chatPojo.setText(ds.getValue().toString());
                    } else if (ds.getKey().equals("timestamp")) {
                        chatPojo.setTimestamp(ds.getValue().toString());
                    } else if (ds.getKey().equals("toId")) {
                        chatPojo.setToId(ds.getValue().toString());
                    } else if (ds.getKey().equals("msgType")) {
                        chatPojo.setMsgType(ds.getValue().toString());
                    } else if (ds.getKey().equals("fileUrl")) {
                        chatPojo.setFileUrl(ds.getValue().toString());
                    } else if (ds.getKey().equals("isUpdate")) {
                        value = Integer.parseInt(ds.getValue().toString());
                    } else if (ds.getKey().equals("lat")) {
                        chatPojo.setLat(Double.parseDouble(ds.getValue().toString()));
                    } else if (ds.getKey().equals("lng")) {
                        chatPojo.setLng(Double.parseDouble(ds.getValue().toString()));
                    } else if (ds.getKey().equals("isRead")) {
                        chatPojo.setIsRead(ds.getValue().toString());
                    }
                }

                if (chatPojo.getFromId().equalsIgnoreCase(UserId(ChatActivity.this)) && chatPojo.getIsRead().equals("0")) {
                    chatPojo.setIsRead("1");
                } else if (!chatPojo.getFromId().equalsIgnoreCase(UserId(ChatActivity.this)) && !chatPojo.getIsRead().equals("3")) {
                    HashMap<String, Object> updateChatPojo = new HashMap<>();
                    updateChatPojo.put("isRead", "3");
                    referenceMessageInsert.child(dataSnapshot.getKey()).updateChildren(updateChatPojo);
                }

//                App.getmInstance().chatPojoDao.insertOrReplace(chatPojo);

                if (value == 1) {
                    App.getmInstance().chatPojoDao.update(chatPojo);
                } else {
                    App.getmInstance().chatPojoDao.insertOrReplace(chatPojo);
                }


                chatPojoList = App.getmInstance().chatPojoDao.queryBuilder().where(ChatPojoDao.Properties.FriendId.eq(toId)).orderAsc(ChatPojoDao.Properties.Timestamp).list();

              /*  Collections.sort(chatPojoList, new Comparator<ChatPojo>() {
                    @Override
                    public int compare(ChatPojo o1, ChatPojo o2) {
                        return o1.getTimestamp().compareTo(o2.getTimestamp());
                    }

                });*/

                if (chatPojoList.size() > 0) {

                    chatAdapter = new ChatAdapter(ChatActivity.this, chatPojoList);
                    mRvChat.setAdapter(chatAdapter);


//                    Hide by Arun on 09-01-2019
                    /*chatAdapter.updateChatList(chatPojoList);
                    mRvChat.scrollToPosition(chatPojoList.size() - 1);*/

                }

                if (Integer.parseInt(chatPojo.getMsgType()) != 1) {

                    if (Integer.parseInt(chatPojo.getMsgType()) == 2 || Integer.parseInt(chatPojo.getMsgType()) == 3) {
                        if (!chatPojo.getFromId().equals(UserId(ChatActivity.this))) {
                            List<LocalDataPojo> localDataPojos = App.getmInstance().localDataPojoDao.queryBuilder().where(LocalDataPojoDao.Properties.MessageId.eq(chatPojo.getMessageId())).list();
                            if (localDataPojos.size() == 0) {
                                download(chatPojo.getFileUrl(), Integer.parseInt(chatPojo.getMsgType()), chatPojo.getMessageId());
                            }
                        }

                    } else if (Integer.parseInt(chatPojo.getMsgType()) == 4) {
                        if (!fileExist(VideoDirectoryPath + "/" + chatPojo.getMessageId() + ".mp4")) {
                            List<LocalDataPojo> localDataPojos = App.getmInstance().localDataPojoDao.queryBuilder().where(LocalDataPojoDao.Properties.MessageId.eq(chatPojo.getMessageId())).list();
                            if (localDataPojos.size() == 0) {
                                download(chatPojo.getFileUrl(), Integer.parseInt(chatPojo.getMsgType()), chatPojo.getMessageId());
                            }
                        }
                    } else {
                        List<LocalDataPojo> localDataPojos = App.getmInstance().localDataPojoDao.queryBuilder().where(LocalDataPojoDao.Properties.MessageId.eq(chatPojo.getMessageId())).list();
                        if (localDataPojos.size() == 0) {
                            download(chatPojo.getFileUrl(), Integer.parseInt(chatPojo.getMsgType()), chatPojo.getMessageId());
                        }
                    }
                }

            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            myLog("onCancelled: ", databaseError.getMessage());
        }

    };

    ValueEventListener valueEventListenerOnline = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {

                String isOnline = "0";
                String offline_time = "";
                String hide_last_seen = "";

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (ds.getKey().equals("isOnline")) {
                        isOnline = ds.getValue().toString();
                    } else if (ds.getKey().equals("offline-time")) {
                        offline_time = ds.getValue().toString();
                    } else if (ds.getKey().equals("hide-last-seen")) {
                        hide_last_seen = ds.getValue().toString();
                    }
                }

                if (isOnline(ChatActivity.this)) {

                    if (isOnline.equalsIgnoreCase("1")) {
                        mTxtStatus.setVisibility(View.VISIBLE);
                        mTxtStatus.setText("Online");
                    } else if (sessionManager(ChatActivity.this).isHideLastSeen() || hide_last_seen.equals("1")) {
                        mTxtStatus.setVisibility(View.GONE);
                    } else {
                        mTxtStatus.setVisibility(View.VISIBLE);
                        mTxtStatus.setText(returnDate(offline_time));
                    }
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            myLog("onCancelled: ", databaseError.getMessage());
        }
    };

    private void sendMsg() {

        ChatPojo chatPojo = new ChatPojo();
        chatPojo.setFromId(UserId(this));
        chatPojo.setText(mTxtMessage.getText().toString());
        chatPojo.setTimestamp(ConvertedDateTime());
        chatPojo.setToId(toId);
        chatPojo.setMsgType("1");
        chatPojo.setIsRead("0");
        chatPojo.setFileUrl("null");

        mTxtMessage.setText("");

        if (isOnline(this)) {
            String mGroupId = referenceMessageInsert.push().getKey();
            referenceMessageInsert.child(mGroupId).setValue(chatPojo);

            referenceUser.child(toId).child(mGroupId).setValue("1");
            referenceUserInsert = FirebaseDatabase.getInstance().getReference("user-messages").child(toId).child(UserId(this));
            referenceUserInsert.child(mGroupId).setValue("1").addOnSuccessListener(aVoid -> updateFriendList());

            SendNotification();

        } else {
            String mGroupId = referenceMessageInsert.push().getKey();
            chatPojo.setFriendId(toId);
            chatPojo.setMessageId(mGroupId);
            chatPojo.setIsMessageSend(0);
            App.getmInstance().chatPojoDao.insertOrReplace(chatPojo);
            chatPojoList = App.getmInstance().chatPojoDao.queryBuilder().where(ChatPojoDao.Properties.FriendId.eq(toId)).orderAsc(ChatPojoDao.Properties.Timestamp).list();

           /* Collections.sort(chatPojoList, new Comparator<ChatPojo>() {
                @Override
                public int compare(ChatPojo o1, ChatPojo o2) {
                    return o1.getTimestamp().compareTo(o2.getTimestamp());
                }

            });*/

            if (chatPojoList.size() > 0 && chatAdapter != null) {

                chatAdapter = new ChatAdapter(ChatActivity.this, chatPojoList);
                mRvChat.setAdapter(chatAdapter);


//                    Hide by Arun on 07-01-2019
               /* chatAdapter.updateChatList(chatPojoList);
                mRvChat.scrollToPosition(chatPojoList.size() - 1);*/

            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void clearChat() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.exit))
                .setMessage(getResources().getString(R.string.clear_chat_content))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        App.getmInstance().daoSession.getChatPojoDao().deleteInTx(chatPojoList);
                        chatPojoList = App.getmInstance().chatPojoDao.queryBuilder().where(ChatPojoDao.Properties.FriendId.eq(toId)).orderAsc(ChatPojoDao.Properties.Timestamp).list();
                        chatAdapter.updateChatList(chatPojoList);

//                    App.getmInstance().daoSession.getChatListPojoDao().deleteInTx(App.getmInstance().chatListPojoDao.queryBuilder().where(ChatListPojoDao.Properties.ToId.eq(toId)).list());
                        List<ChatListPojo> chatList = App.getmInstance().chatListPojoDao.queryBuilder().where(ChatListPojoDao.Properties.ToId.eq(toId)).list();

                        ChatListPojo chatListPojo = chatList.get(0);
                        chatListPojo.setToId(chatListPojo.getToId());
                        chatListPojo.setText("");
                        chatListPojo.setTimestamp(chatListPojo.getTimestamp());
                        App.getmInstance().chatListPojoDao.insertOrReplace(chatListPojo);

                        referenceUser.child(toId).removeValue();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), null)
                .show();
    }

    private void imagePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_REQUEST_CODE_IMAGE);
            } else {
                getImageFromGallery();
            }
        } else {
            getImageFromGallery();
        }
    }

    private void getImageFromGallery() {


        String[] mimeTypes =
                {"video/*", "image/*"};

        close_dialog();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }

        startActivityForResult(intent, IMAGE_PICKER_SELECT);
    }

    private void documentPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_CALENDAR},
                        MY_REQUEST_CODE_DOCUMENT);
            } else {
                getDocument();
            }
        } else {
            getDocument();
        }
    }

    private void getDocument() {

        String[] mimeTypes =
                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        "text/plain",
                        "application/pdf",
                        "application/zip"};

        close_dialog();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }

        startActivityForResult(intent, DOCUMENT_PICKER_SELECT);
    }

    private void close_dialog() {
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            Uri resultUri = data.getData();

            if (requestCode == IMAGE_PICKER_SELECT) {
                try {

                    String filePath = ImageFilePath.getPath(this, resultUri);

                    if (getMimeType(filePath).contains("image")) {

                        Intent intent = new Intent(this, PhotoEditActivity.class);
                        intent.putExtra("imageUri", data.getData().toString());
                        startActivityForResult(intent, PHOTO_EDIT_CODE);

                    } else if (getMimeType(filePath).contains("video")) {
                        UpdateImageDocument(filePath, "4");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == DOCUMENT_PICKER_SELECT) {
                try {

                    String filePath = ImageFilePath.getPath(this, resultUri);
                    if (null != getMimeType(filePath)) {

                        if (getMimeType(filePath).contains("image")) {

                            Intent intent = new Intent(this, PhotoEditActivity.class);
                            intent.putExtra("imageUri", data.getData().toString());
                            startActivityForResult(intent, PHOTO_EDIT_CODE);

                        } else if (getMimeType(filePath).contains("video")) {
                            UpdateImageDocument(filePath, "4");
                        } else {
                            UpdateImageDocument(filePath, "5");
                        }
                    } else {
                        UpdateImageDocument(filePath, "5");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == MY_REQUEST_CODE_VIDEO) {
                try {

                    String filePath = ImageFilePath.getPath(this, resultUri);
                    try {

                        final String mGroupId = referenceMessageInsert.push().getKey();
                        File folder = new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp Clone/Videos");
                        if (!folder.isDirectory()) {
                            folder.mkdirs();
                        }

                        File sourceLocation = new File(filePath);
                        File targetLocation = new File(VideoDirectoryPath + "/" + mGroupId + ".mp4");
                        if (sourceLocation.renameTo(targetLocation)) {
                            UpdateImageDocument(targetLocation.getPath(), "6");
                        } else {
                            Log.e("Arun", "Move file failed.");
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                try {
                    Glide.with(ChatActivity.this)
                            .load(result.getUri())
                            .into(new CustomTarget<Drawable>() {
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    mRvChat.setBackground(resource);
                                    SharedPreferences.Editor editor = defaultSharedPreferences(ChatActivity.this).edit();
                                    editor.putString("wallpaper", String.valueOf(result.getUri()));
                                    editor.commit();
                                    sessionManager(ChatActivity.this).setWallpaper(true);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == PLACE_PICKER_REQUEST) {
                Place place = PlacePicker.getPlace(data, this);

                String mGroupId = referenceMessageInsert.push().getKey();

                ChatPojo chatPojo = new ChatPojo();
                chatPojo.setFromId(UserId(this));
                chatPojo.setText("");
                chatPojo.setTimestamp(ConvertedDateTime());
                chatPojo.setToId(toId);
                chatPojo.setMsgType("9");
                chatPojo.setIsRead("0");
                chatPojo.setLat(place.getLatLng().latitude);
                chatPojo.setLng(place.getLatLng().longitude);
                chatPojo.setFileUrl("null");

                if (isOnline(ChatActivity.this)) {

                    referenceMessageInsert.child(mGroupId).setValue(chatPojo);
                    referenceUser.child(toId).child(mGroupId).setValue("1");
                    referenceUserInsert = FirebaseDatabase.getInstance().getReference("user-messages").child(toId).child(UserId(ChatActivity.this));
                    referenceUserInsert.child(mGroupId).setValue("1").addOnSuccessListener(aVoid -> updateFriendList());;

                    SendNotification();
                }
            } else if (requestCode == PHOTO_EDIT_CODE) {
                String filePath = data.getStringExtra("imagePath");
                UpdateImageDocument(filePath, "2");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case MY_REQUEST_CODE_IMAGE:
                getImageFromGallery();
                break;

            case MY_REQUEST_CODE_DOCUMENT:
                getDocument();
                break;
        }
    }

    private void UpdateImageDocument(String filePath, String type) {

        if (isOnline(this)) {

            final String mGroupId;

            if (type.equals("2")) {
                mGroupId = StringUtils.substringBetween(filePath, "Photos/", ".jpg");
            } else if (type.equals("3")) {
                mGroupId = StringUtils.substringBetween(filePath, "Voice/", ".mp3");
            } else if (type.equals("6")) {
                type = "4";
                mGroupId = StringUtils.substringBetween(filePath, "Videos/", ".mp4");
            } else {
                mGroupId = referenceMessageInsert.push().getKey();
            }

            if (!type.equalsIgnoreCase("5")) {

            /*ChatPojo chatPojo = new ChatPojo();
            chatPojo.setMessageId(mGroupId);
            chatPojo.setFromId(UserId(this));
            chatPojo.setText("");
            chatPojo.setTimestamp(ConvertedDateTime());
            chatPojo.setToId(toId);
            chatPojo.setFriendId(toId);
            chatPojo.setMsgType(type);
            chatPojo.setIsRead("0");
            chatPojo.setFileUrl("null");


            App.getmInstance().chatPojoDao.insertOrReplace(chatPojo);
            chatPojoList = App.getmInstance().chatPojoDao.queryBuilder().where(ChatPojoDao.Properties.FriendId.eq(toId)).list();*/


                HashMap<String, String> map = new HashMap<>();
                map.put("fromId", UserId(ChatActivity.this));
                map.put("text", "");
                map.put("timestamp", ConvertedDateTime());
                map.put("toId", toId);
                map.put("msgType", type);
                map.put("isRead", "0");
                map.put("fileUrl", "null");
                map.put("isUpdate", "0");
                mTxtMessage.setText("");

                if (isOnline(ChatActivity.this)) {
                    referenceMessageInsert.child(mGroupId).setValue(map);
                    referenceUser.child(toId).child(mGroupId).setValue("1").addOnSuccessListener(aVoid -> updateFriendList());;

                }


      /*  Collections.sort(chatPojoList, new Comparator<ChatPojo>() {
            @Override
            public int compare(ChatPojo o1, ChatPojo o2) {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }

        });*/

                if (chatPojoList.size() > 0 && chatAdapter != null) {

                    chatAdapter = new ChatAdapter(ChatActivity.this, chatPojoList);
                    mRvChat.setAdapter(chatAdapter);


//                    Hide by Arun on 07-01-2019
            /*chatAdapter.updateChatList(chatPojoList);
            mRvChat.scrollToPosition(chatPojoList.size() - 1);*/

                }

            }


            File file = new File(filePath);
            RequestBody requestBody = RequestBody.create(parse("*/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("media", file.getName(), requestBody);

            final Call<ProfileImageResponse> registerResponseCall = apiInterface.updateProfileImage(fileToUpload);
            String finalType = type;
            registerResponseCall.enqueue(new Callback<ProfileImageResponse>() {
                @Override
                public void onResponse(Call<ProfileImageResponse> call, Response<ProfileImageResponse> response) {

                    if (response.isSuccessful()) {
                        String responce = response.body().getResult().getUrl();
                        String response_type = response.body().getResult().getType();

                        HashMap<String, String> map = new HashMap<>();
                        map.put("fromId", UserId(ChatActivity.this));
                        map.put("text", "");
                        map.put("timestamp", ConvertedDateTime());
                        map.put("toId", toId);
                        map.put("msgType", finalType);
                        map.put("isRead", "0");
                        map.put("fileUrl", responce);

                        mTxtMessage.setText("");

                        if (isOnline(ChatActivity.this)) {

                            referenceMessageInsert.child(mGroupId).setValue(map);
                            referenceUser.child(toId).child(mGroupId).setValue("1");
                            referenceUserInsert = FirebaseDatabase.getInstance().getReference("user-messages").child(toId).child(UserId(ChatActivity.this));
                            referenceUserInsert.child(mGroupId).setValue("1").addOnSuccessListener(aVoid -> updateFriendList());;

                            SendNotification();
                        }


                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<ProfileImageResponse> call, Throwable t) {
                    myLog("OnFailure", t.toString());
                }
            });
        } else {
            snackbar(this, mRvChat, BaseClass.NO_INTERNET);
        }
    }

    private String returnDate(String Date) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
        Date testDate = null;
        try {
            testDate = sdf.parse(Date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yy", Locale.US);
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.US);

        String strCurrentTime = formatDate.format(CurrentDateTime());
        String strCurrentDate = formatDate.format(testDate);

        String date = "";
        String time = "";

        if (strCurrentTime.equalsIgnoreCase(strCurrentDate)) {
            date = "today";
            time = formatter.format(testDate);
        } else {
            date = strCurrentDate;
            time = formatter.format(testDate);
        }


        String ruternDate = "last seen " + date + " at " + time;

        return ruternDate;
    }

    /**
     * @param download_url - download_url
     * @param i            - message_type
     * @param messageId    - messages_id
     */
    private void download(String download_url, int i, String messageId) {

        LocalDataPojo localDataPojo = new LocalDataPojo();
        localDataPojo.setMessageId(messageId);
        localDataPojo.setUserId(toId);


        if (!TextUtils.isEmpty(download_url) && !download_url.equalsIgnoreCase("null")) {
            String mPath = "";
            String mTextName = "";
            String mMimeType = getMimeType(download_url);

            if (i == 2) {
                mTextName = messageId + ".jpg";
                mPath = "WhatsApp Clone/Photos";
            } else if (i == 3) {
                mTextName = messageId + ".mp3";
                mPath = "WhatsApp Clone/Voice";
            } else if (i == 4) {
                mTextName = messageId + ".mp4";
                mPath = "WhatsApp Clone/Videos";
            } else if (i == 5) {
                mTextName = messageId;
                mPath = "WhatsApp Clone/Documents";
            } else if (i == 7) {
                mTextName = messageId + ".gif";
                mPath = "WhatsApp Clone/Gif";
            } else if (i == 8) {
                mTextName = messageId + ".webp";
                mPath = "WhatsApp Clone/Stickers";
            }

            try {

                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(download_url));

                if (sessionManager(this).isDownloadOnlyWifi()) {
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                } else {
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                }

                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                request.setDestinationInExternalPublicDir(mPath, mTextName);
                manager.enqueue(request);
                localDataPojo.setIsDownloaded(1);
                localDataPojo.setStoragePath(Environment.getExternalStorageDirectory() + "/" + mPath + "/" + mTextName);

            } catch (Exception e) {

                localDataPojo.setIsDownloaded(1);
                localDataPojo.setStoragePath(Environment.getExternalStorageDirectory() + "/" + mPath + "/" + mTextName);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(download_url)));
            } finally {
                App.getmInstance().localDataPojoDao.insertOrReplace(localDataPojo);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {

                Rect outRect = new Rect();
                bottomSheet.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (notifyIntent == 1) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (mKeyBoardGifStrikers.getVisibility() == View.VISIBLE) {
            mKeyBoardGifStrikers.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
        }
    }

    /**
     * Push notification api
     */
    private void SendNotification() {
        if (isOnline(this)) {

            NotificationRequest notificationRequest = new NotificationRequest();
            notificationRequest.setSender(UserId(this));
            notificationRequest.setReceiver(toId);

            final Call<NotificationResponse> transactionResponseCall = apiInterfacePayment.sendNotification(notificationRequest);
            transactionResponseCall.enqueue(new Callback<NotificationResponse>() {
                @Override
                public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                    if (response.isSuccessful()) {
                    }
                }

                @Override
                public void onFailure(Call<NotificationResponse> call, Throwable t) {
                    myLog("OnFailure", t.toString());
                }
            });
        }
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    /**
     * Method dialog call for set wallpaper
     */
    private void showWallPaperDialog() {
        final Dialog mWallpaperDialog = new Dialog(this, R.style.WideDialog);
        mWallpaperDialog.getWindow().setGravity(Gravity.BOTTOM);
        mWallpaperDialog.setContentView(R.layout.layout_wallpaper);
        mWallpaperDialog.setCancelable(true);

        LinearLayout mWallpaperGallery = mWallpaperDialog.findViewById(R.id.mWallpaperGallery);
        LinearLayout mWallpaperDefault = mWallpaperDialog.findViewById(R.id.mWallpaperDefault);

        mWallpaperGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWallpaperDialog.dismiss();
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(ChatActivity.this);
            }
        });

        mWallpaperDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWallpaperDialog.dismiss();
                sessionManager(ChatActivity.this).setWallpaper(false);
                mRvChat.setBackground(ChatActivity.this.getResources().getDrawable(R.drawable.chat_background));
            }
        });

        mWallpaperDialog.show();
    }

    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(chatPojoList.get(position)))
                multiselect_list.remove(chatPojoList.get(position));
            else
                multiselect_list.add(chatPojoList.get(position));

            if (multiselect_list.size() > 0) {
                mAppBarLayoutChat.setVisibility(View.GONE);
                mViewChat.setVisibility(View.GONE);
                mActionMode.setTitle("" + multiselect_list.size());

                if (multiselect_list.size() == 1 && multiselect_list.get(0).getFromId().equals(UserId(this))) {
                    editChat.setVisible(true);
                    replyChat.setVisible(true);
                    forwardChat.setVisible(true);
                } else {
                    editChat.setVisible(false);
                    replyChat.setVisible(false);
                    forwardChat.setVisible(false);
                }

                refreshAdapter();

            } else {
                mActionMode.setTitle("");
                mActionMode.finish();
            }
        }
    }

    /**
     * Toolbar while select multiple items to delete
     * ActionMode are used to set the the title and delete icon
     */
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items

            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.chat_delete_menu, menu);

            editChat = menu.findItem(R.id.mEditChat);
            replyChat = menu.findItem(R.id.mReplyChat);
            forwardChat = menu.findItem(R.id.mForwardChat);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {

                case R.id.mEditChat:
                    llChatCalls.setVisibility(View.GONE);
                    isEditedMsg = true;
                    mTxtMessage.setText(multiselect_list.get(0).getText());
                    ShowHideKeyboard(ChatActivity.this, false, mTxtMessage);
                    mode.finish();
                    return true;

                case R.id.mDeleteChat:
                    for (int i = 0; i < multiselect_list.size(); i++) {
                        List<ChatPojo> chatPojos = App.getmInstance().chatPojoDao.queryBuilder().where(ChatPojoDao.Properties.MessageId.eq(multiselect_list.get(i).getMessageId())).list();
                        App.getmInstance().daoSession.getChatPojoDao().deleteInTx(chatPojos);
                        referenceUser.child(toId).child(String.valueOf(multiselect_list.get(i).getMessageId())).removeValue();
                    }
                    mode.finish();
                    return true;
                case R.id.mReplyChat:
                    mode.finish();
                    return true;
                case R.id.mForwardChat:
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;

            if (!isEditedMsg) {
                multiselect_list = new ArrayList<>();
            }

            mAppBarLayoutChat.setVisibility(View.VISIBLE);
            mViewChat.setVisibility(View.VISIBLE);

            refreshAdapter();
        }
    };

    /**
     * select or unselect multiple items for delete
     */
    public void refreshAdapter() {
        chatPojoList = App.getmInstance().chatPojoDao.queryBuilder().where(ChatPojoDao.Properties.FriendId.eq(toId)).orderAsc(ChatPojoDao.Properties.Timestamp).list();

        if (!isEditedMsg) {
            chatAdapter.multiSelect(chatPojoList, multiselect_list);
        } else {
            ArrayList<ChatPojo> multiselect_list = new ArrayList<>();
            chatAdapter.multiSelect(chatPojoList, multiselect_list);
        }
    }

    /**
     * initialize media audio recorder & media source and starts the audio recording
     */
    private void startRecording() {

        final String mGroupId = referenceMessageInsert.push().getKey();
        File folder = new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp Clone/Voice");
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }

        String extStorageDirectory = folder.toString();
        outputFile = extStorageDirectory + "/" + mGroupId + ".mp3";

        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);
        myAudioRecorder.setOnErrorListener(errorListener);
        myAudioRecorder.setOnInfoListener(infoListener);

        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            myToast(ChatActivity.this, "Tap and hold to Record Audio.");
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            myToast(ChatActivity.this, "Tap and hold to Record Audio.");
        }
    };

    /**
     * method to stop audio recorder and post to DB
     */
    private void stopRecording() {
        if (null != myAudioRecorder) {
            try {
                myAudioRecorder.stop();
                myAudioRecorder.reset();
                myAudioRecorder.release();

                myAudioRecorder = null;
            } catch (RuntimeException stopException) {
                //handle cleanup here
            }

            if (!TextUtils.isEmpty(outputFile)) {
                MediaPlayer mp = MediaPlayer.create(ChatActivity.this, Uri.parse(outputFile));

                if (mp != null) {
                    int duration = mp.getDuration();
                    time = getHumanTimeText(duration);

                    if (isOnline(ChatActivity.this)) {
                        View view = getCurrentFocus();
                        if (view == null) {
                            view = new View(ChatActivity.this);
                        }
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        UpdateImageDocument(outputFile, "3");
                    }
                } else {
                    Log.e("Arun", "stopRecording: mp is null");
                }
            }
        }
    }

    /**
     * method to get the timeformat 00:00 from milliseconds
     */
    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    /**
     * Get Gif's from Giphy Api
     */
    private void GetGif() {
        if (isOnline(this)) {

            final Call<GifResponse> getGifResponseCall = apiInterfaceGif.getGif(GIPHY_API_KEY);
            getGifResponseCall.enqueue(new Callback<GifResponse>() {
                @Override
                public void onResponse(Call<GifResponse> call, Response<GifResponse> response) {
                    if (response.isSuccessful()) {

                        if (response.body().getData().size() > 0) {

                            mRvGif.setVisibility(View.VISIBLE);
                            mProgressGifStrikers.setVisibility(View.GONE);

                            GifAdapter gifAdapter = new GifAdapter(ChatActivity.this, response.body().getData(), false);
                            mRvGif.setAdapter(gifAdapter);
                        }
                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<GifResponse> call, Throwable t) {
                    myLog("OnFailure", t.toString());
                }
            });
        }
    }

    /**
     * Method Call from ChatAdapter to push a message to firebase
     *
     * @param url     - Gif or Sticker url
     * @param msgType - To differentiate Gif or Sticker
     *                msgType -> 7 Gif and 8 Sticker
     */
    public void UploadGifStickers(String url, String msgType) {

        mKeyBoardGifStrikers.setVisibility(View.GONE);

        String mGroupId = referenceMessageInsert.push().getKey();

        HashMap<String, String> map = new HashMap<>();
        map.put("fromId", UserId(ChatActivity.this));
        map.put("text", "");
        map.put("timestamp", ConvertedDateTime());
        map.put("toId", toId);
        map.put("msgType", msgType);
        map.put("isRead", "0");
        map.put("fileUrl", url);
        map.put("isUpdate", "0");
        mTxtMessage.setText("");

        if (isOnline(ChatActivity.this)) {

            referenceMessageInsert.child(mGroupId).setValue(map);
            referenceUser.child(toId).child(mGroupId).setValue("1");
            referenceUserInsert = FirebaseDatabase.getInstance().getReference("user-messages").child(toId).child(UserId(ChatActivity.this));
            referenceUserInsert.child(mGroupId).setValue("1").addOnSuccessListener(aVoid -> updateFriendList());;

            SendNotification();
        }
    }

    /**
     * Get Stickers from local api
     */
    private void GetStickers() {
        if (isOnline(this)) {

            final Call<StickerResponse> getStickerResponseCall = apiInterface.getStickers();
            getStickerResponseCall.enqueue(new Callback<StickerResponse>() {
                @Override
                public void onResponse(Call<StickerResponse> call, Response<StickerResponse> response) {
                    if (response.isSuccessful()) {

                        if (response.body().getResult().size() > 0) {

                            mRvStrikers.setVisibility(View.VISIBLE);
                            mProgressGifStrikers.setVisibility(View.GONE);

                            StickerAdapter stickerAdapter = new StickerAdapter(ChatActivity.this, response.body().getResult().get(0).getDocs(), false);
                            mRvStrikers.setAdapter(stickerAdapter);

                        }
                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<StickerResponse> call, Throwable t) {
                    myLog("OnFailure", t.toString());
                }
            });
        }
    }

    /**
     * Method call for PlacePicker to shareLocation
     */
    private void ShareLocation() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * EditMsg
     */
    private void EditMessage(ChatPojo chatPojo) {

        isEditedMsg = false;
        multiselect_list = new ArrayList<>();

        chatPojo.setText(mTxtMessage.getText().toString());
        mTxtMessage.setText("");

        if (isOnline(this)) {

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("text", chatPojo.getText());

            referenceMessageInsert.child(chatPojo.getMessageId()).updateChildren(hashMap);
        } else {
            chatPojo.setMessageId(chatPojo.getMessageId());
            chatPojo.setIsMessageSend(0);
            App.getmInstance().chatPojoDao.insertOrReplace(chatPojo);
            chatPojoList = App.getmInstance().chatPojoDao.queryBuilder().where(ChatPojoDao.Properties.FriendId.eq(toId)).orderAsc(ChatPojoDao.Properties.Timestamp).list();

            if (chatPojoList.size() > 0 && chatAdapter != null) {

                chatAdapter = new ChatAdapter(this, chatPojoList);
                mRvChat.setAdapter(chatAdapter);

            }
        }
    }

    /**
     * update FriendList
     */
    private void updateFriendList() {

        ChatPojo chatPojo = new ChatPojo();
        chatPojo.setFromId(UserId(this));
        chatPojo.setText("Friend request sent");
        chatPojo.setTimestamp(ConvertedDateTime());
        chatPojo.setToId(toId);
        chatPojo.setMsgType("21");
        chatPojo.setIsRead("0");
        chatPojo.setFileUrl("null");

        if (acceptRejectPojoList.size() == 0) {
            AcceptRejectPojo acceptRejectPojo = new AcceptRejectPojo();
            acceptRejectPojo.setStatus("0");
            acceptRejectPojo.setSend_request_count("1");
            acceptRejectPojo.setReceive_request_count("0");

            DatabaseReference referenceMyFriendList = FirebaseDatabase.getInstance().getReference("user-details").child(UserId(this)).child("friend-list");
            referenceMyFriendList.child(toId).setValue(acceptRejectPojo);

            AcceptRejectPojo acceptRejectPojo1 = new AcceptRejectPojo();
            acceptRejectPojo1.setStatus("0");
            acceptRejectPojo1.setSend_request_count("0");
            acceptRejectPojo1.setReceive_request_count("1");

            DatabaseReference referenceFriendsList = FirebaseDatabase.getInstance().getReference("user-details").child(toId).child("friend-list");
            referenceFriendsList.child(UserId(this)).setValue(acceptRejectPojo1);

            String mGroupId = referenceMessageInsert.push().getKey();
            referenceMessageInsert.child(mGroupId).setValue(chatPojo);

            referenceUser.child(toId).child(mGroupId).setValue("1");
            referenceUserInsert = FirebaseDatabase.getInstance().getReference("user-messages").child(toId).child(UserId(this));
            referenceUserInsert.child(mGroupId).setValue("1");


        } else if (!acceptRejectPojoList.get(0).getStatus().equals("1")) {

            int my_send_count = Integer.parseInt(acceptRejectPojoList.get(0).getSend_request_count()) + 1;

            AcceptRejectPojo acceptRejectPojo = new AcceptRejectPojo();
            acceptRejectPojo.setStatus("0");
            acceptRejectPojo.setSend_request_count(String.valueOf(my_send_count));
            acceptRejectPojo.setReceive_request_count(acceptRejectPojoList.get(0).getReceive_request_count());

            DatabaseReference referenceMyFriendList = FirebaseDatabase.getInstance().getReference("user-details").child(UserId(this)).child("friend-list");
            referenceMyFriendList.child(toId).setValue(acceptRejectPojo);

            AcceptRejectPojo acceptRejectPojo1 = new AcceptRejectPojo();
            acceptRejectPojo1.setStatus("0");
            acceptRejectPojo1.setSend_request_count(acceptRejectPojoList.get(0).getReceive_request_count());
            acceptRejectPojo1.setReceive_request_count(String.valueOf(my_send_count));

            DatabaseReference referenceFriendsList = FirebaseDatabase.getInstance().getReference("user-details").child(toId).child("friend-list");
            referenceFriendsList.child(UserId(this)).setValue(acceptRejectPojo1);

            String mGroupId = referenceMessageInsert.push().getKey();
            referenceMessageInsert.child(mGroupId).setValue(chatPojo);

            referenceUser.child(toId).child(mGroupId).setValue("1");
            referenceUserInsert = FirebaseDatabase.getInstance().getReference("user-messages").child(toId).child(UserId(this));
            referenceUserInsert.child(mGroupId).setValue("1");

        }
    }

    /**
     * update accept/reject status
     *
     * @param chatPojo - accept/reject list
     */
    public void acceptRejectStatus(ChatPojo chatPojo, boolean accept) {

        if (accept) {
            AcceptRejectPojo acceptRejectPojo = new AcceptRejectPojo();
            acceptRejectPojo.setStatus("1");
            acceptRejectPojo.setSend_request_count(acceptRejectPojoList.get(0).getSend_request_count());
            acceptRejectPojo.setReceive_request_count(acceptRejectPojoList.get(0).getReceive_request_count());

            DatabaseReference referenceMyFriendList = FirebaseDatabase.getInstance().getReference("user-details").child(UserId(this)).child("friend-list");
            referenceMyFriendList.child(toId).setValue(acceptRejectPojo);

            AcceptRejectPojo acceptRejectPojo1 = new AcceptRejectPojo();
            acceptRejectPojo1.setStatus("1");
            acceptRejectPojo1.setSend_request_count(acceptRejectPojoList.get(0).getReceive_request_count());
            acceptRejectPojo1.setReceive_request_count(acceptRejectPojoList.get(0).getSend_request_count());

            DatabaseReference referenceFriendsList = FirebaseDatabase.getInstance().getReference("user-details").child(toId).child("friend-list");
            referenceFriendsList.child(UserId(this)).setValue(acceptRejectPojo1);


            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("fromId", chatPojo.getFromId());
            hashMap.put("text", "You sent friend request to");
            hashMap.put("msgType", "23");
            hashMap.put("toId", chatPojo.getToId());
            hashMap.put("isRead", "0");
            hashMap.put("fileUrl", "null");
            hashMap.put("timestamp", chatPojo.getTimestamp());

            referenceMessageInsert.child(chatPojo.getMessageId()).updateChildren(hashMap);


        } else {
            AcceptRejectPojo acceptRejectPojo = new AcceptRejectPojo();
            acceptRejectPojo.setStatus("2");
            acceptRejectPojo.setSend_request_count(acceptRejectPojoList.get(0).getSend_request_count());
            acceptRejectPojo.setReceive_request_count(acceptRejectPojoList.get(0).getReceive_request_count());

            DatabaseReference referenceMyFriendList = FirebaseDatabase.getInstance().getReference("user-details").child(UserId(this)).child("friend-list");
            referenceMyFriendList.child(toId).setValue(acceptRejectPojo);

            AcceptRejectPojo acceptRejectPojo1 = new AcceptRejectPojo();
            acceptRejectPojo1.setStatus("2");
            acceptRejectPojo1.setSend_request_count(acceptRejectPojoList.get(0).getReceive_request_count());
            acceptRejectPojo1.setReceive_request_count(acceptRejectPojoList.get(0).getSend_request_count());

            DatabaseReference referenceFriendsList = FirebaseDatabase.getInstance().getReference("user-details").child(toId).child("friend-list");
            referenceFriendsList.child(UserId(this)).setValue(acceptRejectPojo1);

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("fromId", chatPojo.getFromId());
            hashMap.put("text", "You sent friend request to");
            hashMap.put("msgType", "22");
            hashMap.put("toId", chatPojo.getToId());
            hashMap.put("isRead", "0");
            hashMap.put("fileUrl", "null");
            hashMap.put("timestamp", chatPojo.getTimestamp());

            referenceMessageInsert.child(chatPojo.getMessageId()).updateChildren(hashMap);
        }

    }

    ValueEventListener valueEventListenerFriendList = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {

                AcceptRejectPojo acceptRejectPojo = new AcceptRejectPojo();
                acceptRejectPojo.setFriendId(dataSnapshot.getKey());

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals("status")) {
                        acceptRejectPojo.setStatus(String.valueOf(ds.getValue()));
                    } else if (ds.getKey().equals("send_request_count")) {
                        acceptRejectPojo.setSend_request_count(String.valueOf(ds.getValue()));
                    } else if (ds.getKey().equals("receive_request_count")) {
                        acceptRejectPojo.setReceive_request_count(String.valueOf(ds.getValue()));
                    }
                }
                App.getmInstance().acceptRejectPojoDao.insertOrReplace(acceptRejectPojo);

                acceptRejectPojoList = App.getmInstance().acceptRejectPojoDao.queryBuilder().where(AcceptRejectPojoDao.Properties.FriendId.eq(toId)).list();

                if (acceptRejectPojoList.size() > 0) {
                    if (acceptRejectPojoList.get(0).getStatus().equals("0")) {
                        llWaitingResponse.setVisibility(View.VISIBLE);
                        Ll_text.setVisibility(View.GONE);
                        ShowHideKeyboard(ChatActivity.this, true, mTxtMessage);
                    } else if (acceptRejectPojoList.get(0).getStatus().equals("1")) {
                        llWaitingResponse.setVisibility(View.GONE);
                        Ll_text.setVisibility(View.VISIBLE);
                    } else if (acceptRejectPojoList.get(0).getStatus().equals("2")) {
                        llWaitingResponse.setVisibility(View.GONE);
                        Ll_text.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

}
