package com.chat.zipchat.clone.Activity.Group;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chat.zipchat.clone.Activity.VideoActivity;
import com.chat.zipchat.clone.Activity.WebActivity;
import com.chat.zipchat.clone.Activity.ZoomActivity;
import com.chat.zipchat.clone.Adapter.DeliverOrSeenAdapter;
import com.chat.zipchat.clone.Common.App;
import com.chat.zipchat.clone.Common.AudioWife;
import com.chat.zipchat.clone.Model.Chat.ChatPojo;
import com.chat.zipchat.clone.Model.Group.GroupMember;
import com.chat.zipchat.clone.Model.Group.GroupMemberDao;
import com.chat.zipchat.clone.Model.Group.Groups;
import com.chat.zipchat.clone.Model.Group.GroupsDao;
import com.chat.zipchat.clone.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.chat.zipchat.clone.Common.BaseClass.PhotoDirectoryPath;
import static com.chat.zipchat.clone.Common.BaseClass.VideoDirectoryPath;
import static com.chat.zipchat.clone.Common.BaseClass.defaultSharedPreferences;
import static com.chat.zipchat.clone.Common.BaseClass.getStaticMap;
import static com.chat.zipchat.clone.Common.BaseClass.mLoadAudioPath;
import static com.chat.zipchat.clone.Common.BaseClass.requestOptionsT;
import static com.chat.zipchat.clone.Common.BaseClass.requestOptionsTv;
import static com.chat.zipchat.clone.Common.BaseClass.sessionManager;

public class MessageInfoActivity extends AppCompatActivity {

    private static final int CHAT = 1;
    private static final int IMAGE = 2;
    private static final int AUDIO = 3;
    private static final int VIDEO = 4;
    private static final int DOCUMENT = 5;
    private static final int PAYMENT = 6;
    private static final int GIF = 7;
    private static final int STICKERS = 8;
    private static final int LOCATION = 9;


    RecyclerView rvMessageSeen, rvMessageDelivered;
    TextView tvSeenRemaining, tvDeliveredRemaining;
    CardView cvMessageSeen, cvMessageDelivered;
    ChatPojo chatPojo;
    private DeliverOrSeenAdapter deliverOrSeenAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_info);

        Toolbar toolbarMessageInfo = findViewById(R.id.toolbar_message_info);
        setSupportActionBar(toolbarMessageInfo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarMessageInfo.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        rvMessageSeen = findViewById(R.id.rv_message_seen);
        rvMessageDelivered = findViewById(R.id.rv_message_delivered);
        tvSeenRemaining = findViewById(R.id.tv_seen_remaining);
        tvDeliveredRemaining = findViewById(R.id.tv_delivered_remaining);
        cvMessageSeen = findViewById(R.id.cv_message_seen);
        cvMessageDelivered = findViewById(R.id.cv_message_delivered);

        chatPojo = (ChatPojo) getIntent().getExtras().getSerializable("messageInfo");

        if (!chatPojo.getSeenId().equals("")) {


            List<String> seenList = Arrays.asList(chatPojo.getSeenId().split(","));
            List<GroupMember> mSeenMembers = new ArrayList<>();


            List<Groups> groupsList = App.getmInstance().groupsDao.queryBuilder().where(
                    GroupsDao.Properties.Group_id.eq(chatPojo.getToId())).list();

            for (int i = 0; i < seenList.size(); i++) {
                List<GroupMember> myListData = App.getmInstance().groupMemberDao.queryBuilder().where(
                        GroupMemberDao.Properties.Grp_id.eq(groupsList.get(0).getGrp_id()))
                        .where(GroupMemberDao.Properties.Id.eq(seenList.get(i))).list();
                mSeenMembers.add(myListData.get(0));
            }

            if (mSeenMembers.size() > 0) {
                deliverOrSeenAdapter = new DeliverOrSeenAdapter(this, mSeenMembers);
                rvMessageSeen.setAdapter(deliverOrSeenAdapter);
            }
        }

        ImageView imgGroupInfoBackground = findViewById(R.id.img_group_info_background);

        CollapsingToolbarLayout collapsingMessageInfo = findViewById(R.id.collapsing_message_info);
        collapsingMessageInfo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int availableHeight = collapsingMessageInfo.getMeasuredHeight();
                if (availableHeight > 0) {
                    collapsingMessageInfo.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    imgGroupInfoBackground.getLayoutParams().height = availableHeight;
                    imgGroupInfoBackground.setVisibility(View.VISIBLE);

                    if (!sessionManager(MessageInfoActivity.this).isWallpaperSet()) {
                        imgGroupInfoBackground.setBackground(getResources().getDrawable(R.drawable.chat_background));
                    } else {
                        String mImageUri = defaultSharedPreferences(MessageInfoActivity.this).getString("wallpaper", null);
                        Glide.with(MessageInfoActivity.this)
                                .load(Uri.parse(mImageUri))
                                .error(R.drawable.chat_background)
                                .into(new CustomTarget<Drawable>() {
                                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        imgGroupInfoBackground.setBackground(resource);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }
                                });

                    }
                }
            }
        });


        /**
         * Id's for text_msg
         */
        RelativeLayout rlMsgInfoText = findViewById(R.id.rl_msg_info_text);
        TextView txtMsginfoText = findViewById(R.id.txt_msginfo_text);
        TextView txtMsginfoTextTime = findViewById(R.id.txt_msginfo_text_time);

        /**
         * Id's for images
         */
        RelativeLayout rlMsgInfoImage = findViewById(R.id.rl_msg_info_image);
        TextView tvMsgInfoImageTime = findViewById(R.id.tv_msg_info_image_time);
        ImageView imgMsgInfoImage = findViewById(R.id.img_msg_info_image);

        /**
         * Id's for audio
         */
        RelativeLayout rlMsgInfoAudio = findViewById(R.id.rl_msg_info_audio);
        ImageView imgPlayMsgInfoAudio = findViewById(R.id.img_play_msg_info_audio);
        ImageView imgPauseMsgInfoAudio = findViewById(R.id.img_pause_msg_info_audio);
        SeekBar seekbarMsgInfoAudio = findViewById(R.id.seekbar_msg_info_audio);
        TextView tvMsgInfoAudioRuntime = findViewById(R.id.tv_msg_info_audio_runtime);
        TextView tvMsgInfoAudioTotaltime = findViewById(R.id.tv_msg_info_audio_totaltime);
        TextView tvMsgInfoAudioTime = findViewById(R.id.tv_msg_info_audio_time);

        /**
         * Id's for stickers
         */
        RelativeLayout rlMsgInfoStickers = findViewById(R.id.rl_msg_info_stickers);
        ImageView imgMsgInfoStickers = findViewById(R.id.img_msg_info_stickers);
        TextView tvMsgInfoStickersTime = findViewById(R.id.tv_msg_info_stickers_time);

        /**
         * Id's for document
         */
        RelativeLayout rlMsgInfoDocument = findViewById(R.id.rl_msg_info_document);
        TextView tvMsgInfoDocumentTime = findViewById(R.id.tv_msg_info_document_time);
        CardView cvMsgInfoDocument = findViewById(R.id.cv_msg_info_document);

        /**
         * Id's for Location
         */
        RelativeLayout rlMsgInfoLocation = findViewById(R.id.rl_msg_info_location);
        ImageView imgMsgInfoLocation = findViewById(R.id.img_msg_info_location);
        TextView tvMsgInfoLocationTime = findViewById(R.id.tv_msg_info_location_time);

        /**
         * Id's for Video
         */
        RelativeLayout rlMsgInfoVideo = findViewById(R.id.rl_msg_info_video);
        ImageView imgMsgInfoVideo = findViewById(R.id.img_msg_info_video);
        TextView tvMsgInfoVideoTime = findViewById(R.id.tv_msg_info_video_time);

        switch (Integer.parseInt(chatPojo.getMsgType())) {
            case CHAT:
                rlMsgInfoText.setVisibility(View.VISIBLE);
                txtMsginfoText.setText(chatPojo.getText());
                txtMsginfoTextTime.setText(ConvertDate(chatPojo.getTimestamp()));
                break;
            case IMAGE:
                rlMsgInfoImage.setVisibility(View.VISIBLE);
                tvMsgInfoImageTime.setText(ConvertDate(chatPojo.getTimestamp()));
                Glide.with(this).setDefaultRequestOptions(requestOptionsT()).load(chatPojo.getFileUrl()).into(imgMsgInfoImage);
                final String getDirectoryPath = PhotoDirectoryPath + "/" + chatPojo.getMessageId() + ".jpg";
                imgMsgInfoImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ("null" != chatPojo.getFileUrl()) {
                            Intent mImageIntent = new Intent(MessageInfoActivity.this, ZoomActivity.class);
                            mImageIntent.putExtra("Value", 1);
                            mImageIntent.putExtra("PATH", getDirectoryPath);
                            MessageInfoActivity.this.startActivity(mImageIntent);
                        }
                    }
                });

                break;
            case AUDIO:
                rlMsgInfoAudio.setVisibility(View.VISIBLE);
                tvMsgInfoAudioTime.setText(ConvertDate(chatPojo.getTimestamp()));

                Uri uri;

                if (null == mLoadAudioPath(chatPojo.getMessageId())) {
                    uri = Uri.parse(chatPojo.getFileUrl());
                } else {
                    uri = mLoadAudioPath(chatPojo.getMessageId());
                }

                AudioWife.getInstance().init(this, uri)
                        .setPlayView(imgPlayMsgInfoAudio)
                        .setPauseView(imgPauseMsgInfoAudio)
                        .setSeekBar(seekbarMsgInfoAudio)
                        .setRuntimeView(tvMsgInfoAudioRuntime)
                        .setTotalTimeView(tvMsgInfoAudioTotaltime);

                break;
            case VIDEO:
                rlMsgInfoVideo.setVisibility(View.VISIBLE);
                tvMsgInfoVideoTime.setText(ConvertDate(chatPojo.getTimestamp()));
                Glide.with(this).setDefaultRequestOptions(requestOptionsTv()).load(chatPojo.getFileUrl())
                        .into(imgMsgInfoVideo);

                final String getDirectoryPathVideo = VideoDirectoryPath + "/" + chatPojo.getMessageId() + ".mp4";

                imgMsgInfoVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ("null" != chatPojo.getFileUrl()) {
                            Intent mVideoIntent = new Intent(MessageInfoActivity.this, VideoActivity.class);
                            mVideoIntent.putExtra("URL", getDirectoryPathVideo);
                            MessageInfoActivity.this.startActivity(mVideoIntent);
                        }
                    }
                });

                break;
            case DOCUMENT:
                rlMsgInfoDocument.setVisibility(View.VISIBLE);
                tvMsgInfoDocumentTime.setText(ConvertDate(chatPojo.getTimestamp()));
                cvMsgInfoDocument.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ("null" != chatPojo.getFileUrl()) {
                            Intent mDocumentIntent = new Intent(MessageInfoActivity.this, WebActivity.class);
                            mDocumentIntent.putExtra("URL", chatPojo.getFileUrl());
                            MessageInfoActivity.this.startActivity(mDocumentIntent);
                        }
                    }
                });

                break;
            case PAYMENT:
                break;
            case GIF:
                rlMsgInfoImage.setVisibility(View.VISIBLE);
                tvMsgInfoImageTime.setText(ConvertDate(chatPojo.getTimestamp()));
                Glide.with(this).setDefaultRequestOptions(requestOptionsT()).load(chatPojo.getFileUrl()).into(imgMsgInfoImage);
                break;
            case STICKERS:
                rlMsgInfoStickers.setVisibility(View.VISIBLE);
                tvMsgInfoStickersTime.setText(ConvertDate(chatPojo.getTimestamp()));
                Glide.with(this).setDefaultRequestOptions(requestOptionsT()).load(chatPojo.getFileUrl()).into(imgMsgInfoStickers);
                break;
            case LOCATION:

                String MapUrl = getStaticMap(chatPojo.getLat() + "," + chatPojo.getLng());

                rlMsgInfoLocation.setVisibility(View.VISIBLE);
                tvMsgInfoLocationTime.setText(ConvertDate(chatPojo.getTimestamp()));
                Glide.with(this).setDefaultRequestOptions(requestOptionsTv()).load(MapUrl).into(imgMsgInfoLocation);
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }

    private String ConvertDate(String date) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
        Date testDate = null;
        try {
            testDate = sdf.parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.US);
        String newFormat = formatter.format(testDate);

        return newFormat;
    }

}
