package com.chat.zipchat.clone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chat.zipchat.clone.Activity.ChatActivity;
import com.chat.zipchat.clone.Activity.Group.GroupChatActivity;
import com.chat.zipchat.clone.Common.App;
import com.chat.zipchat.clone.Model.ChatList.ChatListPojo;
import com.chat.zipchat.clone.Model.Contact.ResultItem;
import com.chat.zipchat.clone.Model.Contact.ResultItemDao;
import com.chat.zipchat.clone.Model.Group.GroupItems;
import com.chat.zipchat.clone.Model.Group.GroupItemsDao;
import com.chat.zipchat.clone.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.chat.zipchat.clone.Common.BaseClass.CurrentDateTime;
import static com.chat.zipchat.clone.Common.BaseClass.requestOptionsD;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private Context mContext;
    private List<ChatListPojo> chatAdapterList;


    public ChatListAdapter(Context context, List<ChatListPojo> chatAdapterlist) {
        mContext = context;
        this.chatAdapterList = chatAdapterlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_chat, viewGroup, false);
        return new ChatListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        if (chatAdapterList.get(position).getIsGroup()) {

            final List<GroupItems> groupItems = App.getmInstance().groupItemsDao.queryBuilder().where(
                    GroupItemsDao.Properties.Id.eq(chatAdapterList.get(position).getToId())).list();

            if (groupItems.size() > 0) {

                viewHolder.mTxtName.setText(groupItems.get(0).getName());
                Glide.with(mContext).setDefaultRequestOptions(requestOptionsD()).load(groupItems.get(0).getGroup_picture()).error(R.drawable.ic_group_icon).into(viewHolder.mImgContact);
            } else {
                viewHolder.mTxtName.setText(mContext.getResources().getString(R.string.group));
            }


        } else if (null != chatAdapterList.get(position).getToId()) {

            final List<ResultItem> resultItems = App.getmInstance().resultItemDao.queryBuilder().where(
                    ResultItemDao.Properties.Id.eq(chatAdapterList.get(position).getToId())).list();

            if (resultItems.size() > 0) {

                if (resultItems.get(0).getIsFromContact().equalsIgnoreCase("1")) {
                    viewHolder.mTxtName.setText(resultItems.get(0).getName());
                } else {
                    viewHolder.mTxtName.setText(resultItems.get(0).getMobile_number());
                }
                Glide.with(mContext).setDefaultRequestOptions(requestOptionsD()).load(resultItems.get(0).getProfile_picture()).error(R.drawable.defult_user).into(viewHolder.mImgContact);
            } else {
                viewHolder.mTxtName.setText("Unknown");
            }
        }

        viewHolder.mTxtMessage.setText(chatAdapterList.get(position).getText());

        if (null != chatAdapterList.get(position).getTimestamp() && !TextUtils.isEmpty(chatAdapterList.get(position).getText())) {

            String date1 = chatAdapterList.get(position).getTimestamp();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
            Date testDate = null;
            try {
                testDate = sdf.parse(date1);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yy", Locale.US);

            String strCurrentTime = formatDate.format(CurrentDateTime());
            String strCurrentDate = formatDate.format(testDate);

            if (strCurrentTime.equalsIgnoreCase(strCurrentDate)) {
                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.US);
                String newFormat = formatter.format(testDate);
                viewHolder.mTxtTime.setText(newFormat);
            } else {
                viewHolder.mTxtTime.setText(strCurrentDate);
            }

        }

        viewHolder.mRlChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chatAdapterList.get(position).getIsGroup()) {
                    Intent intent = new Intent(mContext, GroupChatActivity.class);
                    intent.putExtra("toId", chatAdapterList.get(position).getToId());
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra("toId", chatAdapterList.get(position).getToId());
                    mContext.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatAdapterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView mImgContact;
        TextView mTxtName, mTxtTime, mTxtMessage;
        ImageView imgTik;
        RelativeLayout mRlChat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mImgContact = itemView.findViewById(R.id.mImgContact);
            mTxtName = itemView.findViewById(R.id.mTxtName);
            mTxtTime = itemView.findViewById(R.id.mTxtTime);
            mTxtMessage = itemView.findViewById(R.id.mTxtMessage);
            imgTik = itemView.findViewById(R.id.imgTik);
            mRlChat = itemView.findViewById(R.id.mRlChat);

        }
    }

    public void updateFragChatList(List<ChatListPojo> newlist) {
        this.chatAdapterList = newlist;
        notifyDataSetChanged();
    }

}
