package com.chat.zipchat.clone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chat.zipchat.clone.Activity.Payments.TransHistoryActivity;
import com.chat.zipchat.clone.Activity.VideoActivity;
import com.chat.zipchat.clone.Activity.WebActivity;
import com.chat.zipchat.clone.Activity.ZoomActivity;
import com.chat.zipchat.clone.Common.App;
import com.chat.zipchat.clone.Common.AudioWife;
import com.chat.zipchat.clone.Model.ChatPojo;
import com.chat.zipchat.clone.Model.GroupMemberDao;
import com.chat.zipchat.clone.Model.ResultItem;
import com.chat.zipchat.clone.Model.GroupMember;
import com.chat.zipchat.clone.Model.ResultItemDao;
import com.chat.zipchat.clone.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.chat.zipchat.clone.Common.BaseClass.PhotoDirectoryPath;
import static com.chat.zipchat.clone.Common.BaseClass.UserId;
import static com.chat.zipchat.clone.Common.BaseClass.VideoDirectoryPath;
import static com.chat.zipchat.clone.Common.BaseClass.getStaticMap;
import static com.chat.zipchat.clone.Common.BaseClass.mLoadAudioPath;
import static com.chat.zipchat.clone.Common.BaseClass.requestOptionsT;
import static com.chat.zipchat.clone.Common.BaseClass.requestOptionsTv;

public class GroupChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int EMPTY = 0;
    private static final int CHAT = 1;
    private static final int IMAGE = 2;
    private static final int AUDIO = 3;
    private static final int VIDEO = 4;
    private static final int DOCUMENT = 5;
    private static final int PAYMENT = 6;
    private static final int GIF = 7;
    private static final int STICKERS = 8;
    private static final int LOCATION = 9;

    private static final int GROUP_DESC = 10;
    private static final int GROUP_ICON_CHANGE = 12;

    private Context mContext;
    private List<ChatPojo> chatPojoArrayList;
    private List<ChatPojo> multiSelect;

    String mSearch;

    public GroupChatAdapter(Context mContext, List<ChatPojo> chatPojoArrayList) {
        this.mContext = mContext;
        this.chatPojoArrayList = chatPojoArrayList;
        multiSelect = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {

            case CHAT:
                view = inflater.inflate(R.layout.list_item_chat_message, parent, false);
                return new ChatItem(view);
            case EMPTY:
                view = inflater.inflate(R.layout.list_item_chat_message, parent, false);
                return new ChatItem(view);
            case IMAGE:
                view = inflater.inflate(R.layout.list_image, parent, false);
                return new ImageItem(view);
            case AUDIO:
                view = inflater.inflate(R.layout.list_audio, parent, false);
                return new AudioItem(view);
            case VIDEO:
                view = inflater.inflate(R.layout.list_video, parent, false);
                return new VideoItem(view);
            case DOCUMENT:
                view = inflater.inflate(R.layout.list_document, parent, false);
                return new DocumentItem(view);
            case PAYMENT:
                view = inflater.inflate(R.layout.list_payment, parent, false);
                return new PaymentItem(view);
            case GIF:
                view = inflater.inflate(R.layout.list_image, parent, false);
                return new ImageItem(view);
            case STICKERS:
                view = inflater.inflate(R.layout.list_stickers, parent, false);
                return new StickersItem(view);
            case LOCATION:
                view = inflater.inflate(R.layout.list_location, parent, false);
                return new LocationItem(view);
            case GROUP_DESC:
                view = inflater.inflate(R.layout.list_group_descriptions, parent, false);
                return new GroupDescItem(view);
            case GROUP_ICON_CHANGE:
                view = inflater.inflate(R.layout.list_group_icon_change, parent, false);
                return new GroupIconItem(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {

        List<GroupMember> groupMember = App.getmInstance().groupMemberDao.queryBuilder().where(GroupMemberDao.Properties.Id.eq(chatPojoArrayList.get(position).getFromId())).list();
        List<ResultItem> resultItems = App.getmInstance().resultItemDao.queryBuilder().where(ResultItemDao.Properties.Id.eq(chatPojoArrayList.get(position).getFromId())).list();

        switch (getItemViewType(position)) {

            case EMPTY:
                final ChatItem emptyItem = (ChatItem) viewHolder;

                if (chatPojoArrayList.get(position).getFromId().equalsIgnoreCase(UserId(mContext))) {
                    emptyItem.mOutMessageRl.setVisibility(View.VISIBLE);
                    emptyItem.txtOutMessage.setText(chatPojoArrayList.get(position).getText());
                    emptyItem.mOutMessageTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));

                    if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("0")) {
                        emptyItem.mOutMessageTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_not_deliver_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("1")) {
                        emptyItem.mOutMessageTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_send_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("2")) {
                        emptyItem.mOutMessageTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_delivered_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("3")) {
                        emptyItem.mOutMessageTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_read_new, 0);
                    }

                } else {
                    emptyItem.mInMessageRl.setVisibility(View.VISIBLE);
                    emptyItem.txtInMessage.setText(chatPojoArrayList.get(position).getText());

                    /*layout params for time*/
                    LinearLayout.LayoutParams layoutParamsTime = (LinearLayout.LayoutParams) emptyItem.mInMessageTime.getLayoutParams();
                    layoutParamsTime.gravity = Gravity.END;
                    emptyItem.mInMessageTime.setLayoutParams(layoutParamsTime);
                    emptyItem.mInMessageTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));


                    /*For group memberDetails*/
                    emptyItem.tvSenderName.setVisibility(View.VISIBLE);
                    if (resultItems.size() > 0) {
                        if (resultItems.get(0).getIsFromContact().equalsIgnoreCase("1")) {
                            emptyItem.tvSenderName.setText(resultItems.get(0).getName());
                        } else {
                            emptyItem.tvSenderName.setText(resultItems.get(0).getMobile_number());
                        }
                    } else {
                        emptyItem.tvSenderName.setText(groupMember.get(0).getMobile_number());
                    }


                }

                emptyItem.mListChatMessage.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return false;
                    }
                });

                if (multiSelect.contains(chatPojoArrayList.get(position))) {
                    emptyItem.mListChatMessage.setBackgroundColor(mContext.getResources().getColor(R.color.long_press_blue));
                } else {
                    emptyItem.mListChatMessage.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                }

                break;

            case CHAT:
                final ChatItem chatItem = (ChatItem) viewHolder;

                String mText = chatPojoArrayList.get(position).getText();

                //search
                Spannable spanText = null;
                if (!TextUtils.isEmpty(mSearch) && !mSearch.equalsIgnoreCase("null")) {
                    if (mText.contains(mSearch)) {
                        int startPos = mText.indexOf(mSearch);
                        int endPos = startPos + mSearch.length();

                        // StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                        spanText = Spannable.Factory.getInstance().newSpannable(chatPojoArrayList.get(position).getText());
                        spanText.setSpan(new ForegroundColorSpan(Color.BLACK), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spanText.setSpan(new BackgroundColorSpan(Color.YELLOW), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }

                if (chatPojoArrayList.get(position).getFromId().equalsIgnoreCase(UserId(mContext))) {
                    chatItem.mOutMessageRl.setVisibility(View.VISIBLE);
                    chatItem.txtOutMessage.setText(chatPojoArrayList.get(position).getText());
                    chatItem.mOutMessageTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));

                    if (spanText != null) {
                        chatItem.txtOutMessage.setText(spanText, TextView.BufferType.SPANNABLE);
                    }

                    if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("0")) {
                        chatItem.mOutMessageTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_not_deliver_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("1")) {
                        chatItem.mOutMessageTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_send_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("2")) {
                        chatItem.mOutMessageTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_delivered_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("3")) {
                        chatItem.mOutMessageTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_read_new, 0);
                    }

                } else {
                    chatItem.mInMessageRl.setVisibility(View.VISIBLE);
                    chatItem.txtInMessage.setText(chatPojoArrayList.get(position).getText());

                    /*layout params for time*/
                    LinearLayout.LayoutParams layoutParamsTime = (LinearLayout.LayoutParams) chatItem.mInMessageTime.getLayoutParams();
                    layoutParamsTime.gravity = Gravity.END;
                    chatItem.mInMessageTime.setLayoutParams(layoutParamsTime);
                    chatItem.mInMessageTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));

                    if (spanText != null) {
                        chatItem.txtInMessage.setText(spanText, TextView.BufferType.SPANNABLE);
                    }

                    /*For group memberDetails*/
                    chatItem.tvSenderName.setVisibility(View.VISIBLE);
                    if (resultItems.size() > 0) {
                        if (resultItems.get(0).getIsFromContact().equalsIgnoreCase("1")) {
                            chatItem.tvSenderName.setText(resultItems.get(0).getName());
                        } else {
                            chatItem.tvSenderName.setText(resultItems.get(0).getMobile_number());
                        }
                    } else {
                        chatItem.tvSenderName.setText(groupMember.get(0).getMobile_number());
                    }

                }

                chatItem.mListChatMessage.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return false;
                    }
                });


                if (multiSelect.contains(chatPojoArrayList.get(position))) {
                    chatItem.mListChatMessage.setBackgroundColor(mContext.getResources().getColor(R.color.long_press_blue));
                } else {
                    chatItem.mListChatMessage.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                }

                break;

            case IMAGE:

                final ImageItem imageItem = (ImageItem) viewHolder;

                if ("null".equalsIgnoreCase(chatPojoArrayList.get(position).getFileUrl())) {

                    imageItem.mProgressSendImage.setVisibility(View.VISIBLE);
                    imageItem.mProgressReceiveImage.setVisibility(View.VISIBLE);

                } else {

                    imageItem.mProgressSendImage.setVisibility(View.GONE);
                    imageItem.mProgressReceiveImage.setVisibility(View.GONE);
                }


                if (chatPojoArrayList.get(position).getFromId().equalsIgnoreCase(UserId(mContext))) {

                    imageItem.mSendImageRl.setVisibility(View.VISIBLE);
                    imageItem.mReceiveImageRl.setVisibility(View.GONE);

                    imageItem.mSendImageTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));
                    Glide.with(mContext).setDefaultRequestOptions(requestOptionsT()).load(chatPojoArrayList.get(position).getFileUrl())
                            .into(imageItem.mSendImage);

                    if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("0")) {
                        imageItem.mSendImageTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_not_deliver_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("1")) {
                        imageItem.mSendImageTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_send_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("2")) {
                        imageItem.mSendImageTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_delivered_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("3")) {
                        imageItem.mSendImageTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_read_new, 0);
                    }
                } else {

                    imageItem.mReceiveImageRl.setVisibility(View.VISIBLE);
                    imageItem.mSendImageRl.setVisibility(View.GONE);

                    imageItem.mReceiveImageTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));
                    Glide.with(mContext).setDefaultRequestOptions(requestOptionsT()).load(chatPojoArrayList.get(position).getFileUrl())
                            .into(imageItem.mReceiveImage);
                }


                final String getDirectoryPath = PhotoDirectoryPath + "/" + chatPojoArrayList.get(position).getMessageId() + ".jpg";
                imageItem.mImageLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if ("null" != chatPojoArrayList.get(position).getFileUrl()) {
                            Intent mImageIntent = new Intent(mContext, ZoomActivity.class);
                            mImageIntent.putExtra("Value", 1);
                            mImageIntent.putExtra("PATH", getDirectoryPath);
                            mContext.startActivity(mImageIntent);
                        }
                    }
                });

                imageItem.mImageLl.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return false;
                    }
                });

                if (multiSelect.contains(chatPojoArrayList.get(position))) {
                    imageItem.mImageLl.setBackgroundColor(mContext.getResources().getColor(R.color.long_press_blue));
                } else {
                    imageItem.mImageLl.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                }

                break;

            case AUDIO:
                final AudioItem audioView = (AudioItem) viewHolder;

                Uri uri;

                if (null == mLoadAudioPath(chatPojoArrayList.get(position).getMessageId())) {
                    uri = Uri.parse(chatPojoArrayList.get(position).getFileUrl());
                } else {
                    uri = mLoadAudioPath(chatPojoArrayList.get(position).getMessageId());
                }

                if (chatPojoArrayList.get(position).getFromId().equalsIgnoreCase(UserId(mContext))) {
                    audioView.mOutAudioRl.setVisibility(View.VISIBLE);

                    AudioWife.getInstance().init(mContext, uri)
                            .setPlayView(audioView.mPlayOut)
                            .setPauseView(audioView.mPauseOut)
                            .setSeekBar(audioView.mSeekbarOut)
                            .setRuntimeView(audioView.mRunTimeOut)
                            .setTotalTimeView(audioView.mTotalTimeOut);

                    audioView.mOutAudioTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));

                    if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("0")) {
                        audioView.mOutAudioTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_not_deliver_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("1")) {
                        audioView.mOutAudioTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_send_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("2")) {
                        audioView.mOutAudioTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_delivered_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("3")) {
                        audioView.mOutAudioTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_read_new, 0);
                    }

                } else {
                    audioView.mInAudioRl.setVisibility(View.VISIBLE);

                    AudioWife.getInstance()
                            .init(mContext, uri)
                            .setPlayView(audioView.mPlayIn)
                            .setPauseView(audioView.mPauseIn)
                            .setSeekBar(audioView.mSeekbarIn)
                            .setRuntimeView(audioView.mRunTimeIn)
                            .setTotalTimeView(audioView.mTotalTimeIn);

                    audioView.mInAudioTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));
                }

                if (multiSelect.contains(chatPojoArrayList.get(position))) {
                    audioView.ll_item_audio.setBackgroundColor(mContext.getResources().getColor(R.color.long_press_blue));
                } else {
                    audioView.ll_item_audio.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                }

                break;

            case VIDEO:
                final VideoItem videoView = (VideoItem) viewHolder;

                if ("null".equalsIgnoreCase(chatPojoArrayList.get(position).getFileUrl())) {
                    videoView.mProgressSendVideo.setVisibility(View.VISIBLE);
                    videoView.mProgressReceiveVideo.setVisibility(View.VISIBLE);
                } else {
                    videoView.mProgressSendVideo.setVisibility(View.GONE);
                    videoView.mProgressReceiveVideo.setVisibility(View.GONE);
                }

                if (chatPojoArrayList.get(position).getFromId().equalsIgnoreCase(UserId(mContext))) {
                    videoView.mSendVideoRl.setVisibility(View.VISIBLE);
                    videoView.mReceiveVideoRl.setVisibility(View.GONE);
                    videoView.mSendVideoTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));

                    Glide.with(mContext).setDefaultRequestOptions(requestOptionsTv()).load(chatPojoArrayList.get(position).getFileUrl())
                            .into(videoView.mSendVideo);

                    if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("0")) {
                        videoView.mSendVideoTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_not_deliver_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("1")) {
                        videoView.mSendVideoTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_send_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("2")) {
                        videoView.mSendVideoTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_delivered_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("3")) {
                        videoView.mSendVideoTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_read_new, 0);
                    }


                } else {
                    videoView.mReceiveVideoRl.setVisibility(View.VISIBLE);
                    videoView.mSendVideoRl.setVisibility(View.GONE);
                    videoView.mReceiveVideoTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));

                    Glide.with(mContext).setDefaultRequestOptions(requestOptionsTv()).load(chatPojoArrayList.get(position).getFileUrl())
                            .into(videoView.mReceiveVideo);
                }


                final String getDirectoryPathVideo = VideoDirectoryPath + "/" + chatPojoArrayList.get(position).getMessageId() + ".mp4";

                videoView.mVideoLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ("null" != chatPojoArrayList.get(position).getFileUrl()) {
                            Intent mVideoIntent = new Intent(mContext, VideoActivity.class);
//                            mVideoIntent.putExtra("URL", chatPojoArrayList.get(position).getFileUrl());
                            mVideoIntent.putExtra("URL", getDirectoryPathVideo);
                            mContext.startActivity(mVideoIntent);
                        }
                    }
                });

                videoView.mVideoLl.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return false;
                    }
                });

                if (multiSelect.contains(chatPojoArrayList.get(position))) {
                    videoView.mVideoLl.setBackgroundColor(mContext.getResources().getColor(R.color.long_press_blue));
                } else {
                    videoView.mVideoLl.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                }

                break;

            case DOCUMENT:
                final DocumentItem documentItem = (DocumentItem) viewHolder;

                if (chatPojoArrayList.get(position).getFromId().equalsIgnoreCase(UserId(mContext))) {

                    documentItem.mSendDocumentCl.setVisibility(View.VISIBLE);
                    documentItem.mSendDocumentTime.setVisibility(View.VISIBLE);
                    documentItem.mSendDocumentTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));


                    if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("0")) {
                        documentItem.mSendDocumentTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_not_deliver_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("1")) {
                        documentItem.mSendDocumentTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_send_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("2")) {
                        documentItem.mSendDocumentTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_delivered_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("3")) {
                        documentItem.mSendDocumentTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_read_new, 0);
                    }

                } else {

                    documentItem.mReceiveDocumentCl.setVisibility(View.VISIBLE);
                    documentItem.mReceiveDocumentTime.setVisibility(View.VISIBLE);
                    documentItem.mReceiveDocumentTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));
                }

                documentItem.mDocumentLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ("null" != chatPojoArrayList.get(position).getFileUrl()) {
                            Intent mDocumentIntent = new Intent(mContext, WebActivity.class);
                            mDocumentIntent.putExtra("URL", chatPojoArrayList.get(position).getFileUrl());
                            mContext.startActivity(mDocumentIntent);
                        }
                    }
                });

                documentItem.mDocumentLl.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return false;
                    }
                });

                if (multiSelect.contains(chatPojoArrayList.get(position))) {
                    documentItem.mDocumentLl.setBackgroundColor(mContext.getResources().getColor(R.color.long_press_blue));
                } else {
                    documentItem.mDocumentLl.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                }

                break;

            case PAYMENT:
                final PaymentItem paymentItem = (PaymentItem) viewHolder;

                double amount = Double.parseDouble(chatPojoArrayList.get(position).getText());
                double amountNew = Double.parseDouble(new DecimalFormat("##.#####").format(amount));

                if (chatPojoArrayList.get(position).getFromId().equalsIgnoreCase(UserId(mContext))) {
                    paymentItem.mSendPaymentRl.setVisibility(View.VISIBLE);
                    paymentItem.mSendPaymentAmount.setText(String.valueOf(amountNew));
                    paymentItem.mSendPaymentTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));

                    if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("0")) {
                        paymentItem.mSendPaymentTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_not_deliver_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("1")) {
                        paymentItem.mSendPaymentTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_send_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("2")) {
                        paymentItem.mSendPaymentTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_delivered_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("3")) {
                        paymentItem.mSendPaymentTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_read_new, 0);
                    }

                } else {
                    paymentItem.mReceivePaymentRl.setVisibility(View.VISIBLE);
                    paymentItem.mReceivePaymentAmount.setText(String.valueOf(amountNew));
                    paymentItem.mReceivePaymentTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));
                }

                paymentItem.mPaymentLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mTransHistory = new Intent(mContext, TransHistoryActivity.class);
                        mContext.startActivity(mTransHistory);
                    }
                });

                paymentItem.mPaymentLl.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return false;
                    }
                });

                if (multiSelect.contains(chatPojoArrayList.get(position))) {
                    paymentItem.mPaymentLl.setBackgroundColor(mContext.getResources().getColor(R.color.long_press_blue));
                } else {
                    paymentItem.mPaymentLl.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                }

                break;

            case GIF:

                final ImageItem gifItem = (ImageItem) viewHolder;

                if ("null".equalsIgnoreCase(chatPojoArrayList.get(position).getFileUrl())) {

                    gifItem.mProgressSendImage.setVisibility(View.VISIBLE);
                    gifItem.mProgressReceiveImage.setVisibility(View.VISIBLE);

                } else {

                    gifItem.mProgressSendImage.setVisibility(View.GONE);
                    gifItem.mProgressReceiveImage.setVisibility(View.GONE);
                }


                if (chatPojoArrayList.get(position).getFromId().equalsIgnoreCase(UserId(mContext))) {

                    gifItem.mSendImageRl.setVisibility(View.VISIBLE);
                    gifItem.mReceiveImageRl.setVisibility(View.GONE);

                    gifItem.mSendImageTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));
                    Glide.with(mContext).setDefaultRequestOptions(requestOptionsT()).asGif().load(chatPojoArrayList.get(position).getFileUrl())
                            .into(gifItem.mSendImage);


                    if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("0")) {
                        gifItem.mSendImageTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_not_deliver_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("1")) {
                        gifItem.mSendImageTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_send_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("2")) {
                        gifItem.mSendImageTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_delivered_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("3")) {
                        gifItem.mSendImageTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_read_new, 0);
                    }

                } else {

                    gifItem.mReceiveImageRl.setVisibility(View.VISIBLE);
                    gifItem.mSendImageRl.setVisibility(View.GONE);

                    gifItem.mReceiveImageTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));
                    Glide.with(mContext).setDefaultRequestOptions(requestOptionsT()).asGif().load(chatPojoArrayList.get(position).getFileUrl())
                            .into(gifItem.mReceiveImage);
                }


               /* final String getGifDirectoryPath = PhotoDirectoryPath + "/" + chatPojoArrayList.get(position).getMessageId() + ".gif";
                gifItem.mImageLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if ("null" != chatPojoArrayList.get(position).getFileUrl()) {
                            Intent mImageIntent = new Intent(mContext, ZoomActivity.class);
                            mImageIntent.putExtra("Value", 1);
                            mImageIntent.putExtra("PATH", getGifDirectoryPath);
                            mContext.startActivity(mImageIntent);
                        }
                    }
                });*/

                gifItem.mImageLl.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return false;
                    }
                });

                if (multiSelect.contains(chatPojoArrayList.get(position))) {
                    gifItem.mImageLl.setBackgroundColor(mContext.getResources().getColor(R.color.long_press_blue));
                } else {
                    gifItem.mImageLl.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                }
                break;

            case STICKERS:

                final StickersItem stickersItem = (StickersItem) viewHolder;

                if (chatPojoArrayList.get(position).getFromId().equalsIgnoreCase(UserId(mContext))) {
                    stickersItem.mSendStickersRl.setVisibility(View.VISIBLE);
                    stickersItem.mReceiveStickersRl.setVisibility(View.GONE);
                    stickersItem.mSendStickersTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));

                    Glide.with(mContext).setDefaultRequestOptions(requestOptionsTv()).load(chatPojoArrayList.get(position).getFileUrl())
                            .into(stickersItem.mSendStickers);

                    if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("0")) {
                        stickersItem.mSendStickersTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_not_deliver_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("1")) {
                        stickersItem.mSendStickersTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_send_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("2")) {
                        stickersItem.mSendStickersTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_delivered_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("3")) {
                        stickersItem.mSendStickersTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_read_new, 0);
                    }

                } else {
                    stickersItem.mReceiveStickersRl.setVisibility(View.VISIBLE);
                    stickersItem.mSendStickersRl.setVisibility(View.GONE);
                    stickersItem.mReceiveStickersTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));

                    Glide.with(mContext).setDefaultRequestOptions(requestOptionsTv()).load(chatPojoArrayList.get(position).getFileUrl())
                            .into(stickersItem.mReceiveStickers);
                }

                if (multiSelect.contains(chatPojoArrayList.get(position))) {
                    stickersItem.mStickersLl.setBackgroundColor(mContext.getResources().getColor(R.color.long_press_blue));
                } else {
                    stickersItem.mStickersLl.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                }

                break;

            case LOCATION:
                final LocationItem locationItem = (LocationItem) viewHolder;

                String MapUrl = getStaticMap(chatPojoArrayList.get(position).getLat() + "," + chatPojoArrayList.get(position).getLng());

                if (chatPojoArrayList.get(position).getFromId().equalsIgnoreCase(UserId(mContext))) {
                    locationItem.mCardSendLocation.setVisibility(View.VISIBLE);
                    locationItem.mCardReceiveLocation.setVisibility(View.GONE);
                    Glide.with(mContext).setDefaultRequestOptions(requestOptionsTv()).load(MapUrl).into(locationItem.mSendLocImg);
                    locationItem.mSendLocTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));


                    if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("0")) {
                        locationItem.mSendLocTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_not_deliver_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("1")) {
                        locationItem.mSendLocTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_send_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("2")) {
                        locationItem.mSendLocTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_delivered_new, 0);
                    } else if (chatPojoArrayList.get(position).getIsRead().equalsIgnoreCase("3")) {
                        locationItem.mSendLocTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_read_new, 0);
                    }
                } else {
                    locationItem.mCardReceiveLocation.setVisibility(View.VISIBLE);
                    locationItem.mCardSendLocation.setVisibility(View.GONE);
                    Glide.with(mContext).setDefaultRequestOptions(requestOptionsTv()).load(MapUrl).into(locationItem.mReceiveLocImg);
                    locationItem.mReceiveLocTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));
                }

                locationItem.mLayoutLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mapUri = String.format(Locale.ENGLISH, "geo:%f,%f", chatPojoArrayList.get(position).getLat(), chatPojoArrayList.get(position).getLng());
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUri));
                        mContext.startActivity(intent);
                    }
                });

                if (multiSelect.contains(chatPojoArrayList.get(position))) {
                    locationItem.mLayoutLocation.setBackgroundColor(mContext.getResources().getColor(R.color.long_press_blue));
                } else {
                    locationItem.mLayoutLocation.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                }

                break;

            case GROUP_DESC:
                final GroupDescItem groupDescItem = (GroupDescItem) viewHolder;

                String from;

                if (chatPojoArrayList.get(position).getFromId().equals(UserId(mContext))) {
                    from = "You";
                } else if (resultItems.size() > 0) {
                    if (resultItems.get(0).getIsFromContact().equalsIgnoreCase("1")) {
                        from = resultItems.get(0).getName();
                    } else {
                        from = resultItems.get(0).getMobile_number();
                    }
                } else {
                    from = groupMember.get(0).getMobile_number();
                }

                groupDescItem.tvListGroupDescription.setText(from + " " + chatPojoArrayList.get(position).getText());
                break;

            case GROUP_ICON_CHANGE:
                final GroupIconItem groupIconItem = (GroupIconItem) viewHolder;

                String mFrom;

                if (chatPojoArrayList.get(position).getFromId().equals(UserId(mContext))) {
                    mFrom = "You";
                } else if (resultItems.size() > 0) {
                    if (resultItems.get(0).getIsFromContact().equalsIgnoreCase("1")) {
                        mFrom = resultItems.get(0).getName();
                    } else {
                        mFrom = resultItems.get(0).getMobile_number();
                    }
                } else {
                    mFrom = groupMember.get(0).getMobile_number();
                }

                String[] arrOfGrpIcon = chatPojoArrayList.get(position).getText().split(" to ");
                Glide.with(mContext).setDefaultRequestOptions(requestOptionsTv()).load(arrOfGrpIcon[0]).into(groupIconItem.imgGroupIconBefore);
                Glide.with(mContext).setDefaultRequestOptions(requestOptionsTv()).load(arrOfGrpIcon[1]).into(groupIconItem.imgGroupIconAfter);

                groupIconItem.tvGroupIconChange.setText(mFrom + " changed this group's icon");
                break;
        }
    }

    public void getSearchValues(String search) {
        mSearch = search;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return chatPojoArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {

        int viewtype = Integer.parseInt(chatPojoArrayList.get(position).getMsgType());

        if (viewtype == 11 || viewtype == 13) {
            viewtype = 10;
        }

        return viewtype;

    }

    public class ChatItem extends RecyclerView.ViewHolder {
        TextView txtOutMessage, txtInMessage, mOutMessageTime, mInMessageTime;
        RelativeLayout mOutMessageRl;
        LinearLayout mInMessageRl, mListChatMessage;

        TextView tvSenderName;

        public ChatItem(View itemView) {
            super(itemView);
            mInMessageTime = itemView.findViewById(R.id.mInMessageTime);
            txtOutMessage = itemView.findViewById(R.id.txtOutMessage);
            txtInMessage = itemView.findViewById(R.id.txtInMessage);
            mOutMessageTime = itemView.findViewById(R.id.mOutMessageTime);
            mOutMessageRl = itemView.findViewById(R.id.mOutMessageRl);
            mInMessageRl = itemView.findViewById(R.id.mInMessageRl);
            mListChatMessage = itemView.findViewById(R.id.mListChatMessage);

            tvSenderName = itemView.findViewById(R.id.tv_sender_name);

            this.setIsRecyclable(false);
        }
    }

    public class ImageItem extends RecyclerView.ViewHolder {

        ImageView mSendImage, mReceiveImage;
        TextView mSendImageTime, mReceiveImageTime;
        LinearLayout mImageLl;
        RelativeLayout mSendImageRl, mReceiveImageRl;
        ProgressBar mProgressSendImage, mProgressReceiveImage;

        public ImageItem(View itemView) {
            super(itemView);

            mSendImage = itemView.findViewById(R.id.mSendImage);
            mReceiveImage = itemView.findViewById(R.id.mReceiveImage);
            mSendImageTime = itemView.findViewById(R.id.mSendImageTime);
            mReceiveImageTime = itemView.findViewById(R.id.mReceiveImageTime);
            mImageLl = itemView.findViewById(R.id.mImageLl);

            mSendImageRl = itemView.findViewById(R.id.mSendImageRl);
            mReceiveImageRl = itemView.findViewById(R.id.mReceiveImageRl);

            mProgressSendImage = itemView.findViewById(R.id.mProgressSendImage);
            mProgressReceiveImage = itemView.findViewById(R.id.mProgressReceiveImage);

            this.setIsRecyclable(false);

        }
    }

    public class VideoItem extends RecyclerView.ViewHolder {

        CardView mCardSendVideo, mCardReceiveVideo;
        ImageView mSendVideo, mReceiveVideo;
        TextView mSendVideoTime, mReceiveVideoTime;
        LinearLayout mVideoLl;
        RelativeLayout mSendVideoRl, mReceiveVideoRl;
        ProgressBar mProgressSendVideo, mProgressReceiveVideo;

        public VideoItem(View itemView) {
            super(itemView);

            mCardSendVideo = itemView.findViewById(R.id.mCardSendVideo);
            mCardReceiveVideo = itemView.findViewById(R.id.mCardReceiveVideo);
            mSendVideo = itemView.findViewById(R.id.mSendVideo);
            mReceiveVideo = itemView.findViewById(R.id.mReceiveVideo);
            mSendVideoTime = itemView.findViewById(R.id.mSendVideoTime);
            mReceiveVideoTime = itemView.findViewById(R.id.mReceiveVideoTime);
            mVideoLl = itemView.findViewById(R.id.mVideoLl);

            mSendVideoRl = itemView.findViewById(R.id.mSendVideoRl);
            mReceiveVideoRl = itemView.findViewById(R.id.mReceiveVideoRl);

            mProgressSendVideo = itemView.findViewById(R.id.mProgressSendVideo);
            mProgressReceiveVideo = itemView.findViewById(R.id.mProgressReceiveVideo);

            this.setIsRecyclable(false);
        }
    }

    public class AudioItem extends RecyclerView.ViewHolder {

        LinearLayout ll_item_audio;
        RelativeLayout mOutAudioRl, mInAudioRl;
        TextView mOutAudioTime, mInAudioTime;
        TextView mRunTimeIn, mRunTimeOut, mTotalTimeOut, mTotalTimeIn;
        SeekBar mSeekbarOut, mSeekbarIn;
        ImageView mPlayOut, mPauseOut;
        ImageView mPlayIn, mPauseIn;

        public AudioItem(View itemView) {
            super(itemView);

            ll_item_audio = itemView.findViewById(R.id.ll_item_audio);
            mOutAudioRl = itemView.findViewById(R.id.mOutAudioRl);
            mInAudioRl = itemView.findViewById(R.id.mInAudioRl);
            mOutAudioTime = itemView.findViewById(R.id.mOutAudioTime);
            mInAudioTime = itemView.findViewById(R.id.mInAudioTime);

            mRunTimeIn = itemView.findViewById(R.id.mRunTimeIn);
            mRunTimeOut = itemView.findViewById(R.id.mRunTimeOut);

            mSeekbarOut = itemView.findViewById(R.id.mSeekbarOut);
            mSeekbarIn = itemView.findViewById(R.id.mSeekbarIn);

            mPlayOut = itemView.findViewById(R.id.mPlayOut);
            mPauseOut = itemView.findViewById(R.id.mPauseOut);
            mTotalTimeOut = itemView.findViewById(R.id.mTotalTimeOut);

            mPlayIn = itemView.findViewById(R.id.mPlayIn);
            mPauseIn = itemView.findViewById(R.id.mPauseIn);
            mTotalTimeIn = itemView.findViewById(R.id.mTotalTimeIn);

            this.setIsRecyclable(false);
        }
    }

    public class DocumentItem extends RecyclerView.ViewHolder {

        RelativeLayout mDocumentLl;
        CardView mSendDocumentCl, mReceiveDocumentCl;
        TextView mSendDocumentTime, mReceiveDocumentTime;

        public DocumentItem(View itemView) {
            super(itemView);

            mDocumentLl = itemView.findViewById(R.id.mDocumentLl);
            mSendDocumentCl = itemView.findViewById(R.id.mSendDocumentCl);
            mReceiveDocumentCl = itemView.findViewById(R.id.mReceiveDocumentCl);
            mSendDocumentTime = itemView.findViewById(R.id.mSendDocumentTime);
            mReceiveDocumentTime = itemView.findViewById(R.id.mReceiveDocumentTime);

            this.setIsRecyclable(false);

        }
    }

    public class PaymentItem extends RecyclerView.ViewHolder {

        LinearLayout mPaymentLl;
        RelativeLayout mSendPaymentRl, mReceivePaymentRl;
        TextView mSendPaymentAmount, mReceivePaymentAmount;
        TextView mSendPaymentTime, mReceivePaymentTime;

        public PaymentItem(View itemView) {
            super(itemView);

            mPaymentLl = itemView.findViewById(R.id.mPaymentLl);
            mSendPaymentRl = itemView.findViewById(R.id.mSendPaymentRl);
            mReceivePaymentRl = itemView.findViewById(R.id.mReceivePaymentRl);
            mSendPaymentAmount = itemView.findViewById(R.id.mSendPaymentAmount);
            mReceivePaymentAmount = itemView.findViewById(R.id.mReceivePaymentAmount);
            mSendPaymentTime = itemView.findViewById(R.id.mSendPaymentTime);
            mReceivePaymentTime = itemView.findViewById(R.id.mReceivePaymentTime);

            this.setIsRecyclable(false);
        }
    }

    public class StickersItem extends RecyclerView.ViewHolder {

        LinearLayout mStickersLl;
        RelativeLayout mSendStickersRl, mReceiveStickersRl;
        ImageView mSendStickers, mReceiveStickers;
        TextView mSendStickersTime, mReceiveStickersTime;

        public StickersItem(View itemView) {
            super(itemView);

            mSendStickersRl = itemView.findViewById(R.id.mSendStickersRl);
            mReceiveStickersRl = itemView.findViewById(R.id.mReceiveStickersRl);

            mSendStickers = itemView.findViewById(R.id.mSendStickers);
            mReceiveStickers = itemView.findViewById(R.id.mReceiveStickers);
            mSendStickersTime = itemView.findViewById(R.id.mSendStickersTime);
            mReceiveStickersTime = itemView.findViewById(R.id.mReceiveStickersTime);
            mStickersLl = itemView.findViewById(R.id.mStickersLl);

            this.setIsRecyclable(false);
        }
    }

    public class LocationItem extends RecyclerView.ViewHolder {

        RelativeLayout mLayoutLocation;
        CardView mCardSendLocation, mCardReceiveLocation;
        ImageView mSendLocImg, mReceiveLocImg;
        TextView mSendLocTime, mReceiveLocTime;

        public LocationItem(View itemView) {
            super(itemView);

            mCardSendLocation = itemView.findViewById(R.id.mCardSendLocation);
            mCardReceiveLocation = itemView.findViewById(R.id.mCardReceiveLocation);
            mSendLocImg = itemView.findViewById(R.id.mSendLocImg);
            mReceiveLocImg = itemView.findViewById(R.id.mReceiveLocImg);
            mSendLocTime = itemView.findViewById(R.id.mSendLocTime);
            mReceiveLocTime = itemView.findViewById(R.id.mReceiveLocTime);
            mLayoutLocation = itemView.findViewById(R.id.mLayoutLocation);

            this.setIsRecyclable(false);
        }
    }

    public class GroupDescItem extends RecyclerView.ViewHolder {

        TextView tvListGroupDescription;

        public GroupDescItem(View itemView) {
            super(itemView);

            tvListGroupDescription = itemView.findViewById(R.id.tv_list_group_description);

            this.setIsRecyclable(false);
        }

    }

    public class GroupIconItem extends RecyclerView.ViewHolder {

        CircleImageView imgGroupIconBefore, imgGroupIconAfter;
        TextView tvGroupIconChange;

        public GroupIconItem(View itemView) {
            super(itemView);

            imgGroupIconBefore = itemView.findViewById(R.id.img_group_icon_before);
            imgGroupIconAfter = itemView.findViewById(R.id.img_group_icon_after);
            tvGroupIconChange = itemView.findViewById(R.id.tv_group_icon_change);

            this.setIsRecyclable(false);
        }

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

    public void updateChatList(List<ChatPojo> newlist) {
        this.chatPojoArrayList = newlist;
        notifyDataSetChanged();
    }

    public void multiSelect(List<ChatPojo> newlist, List<ChatPojo> multiSelect) {
        this.chatPojoArrayList = newlist;
        this.multiSelect = multiSelect;
        notifyDataSetChanged();
    }

}
