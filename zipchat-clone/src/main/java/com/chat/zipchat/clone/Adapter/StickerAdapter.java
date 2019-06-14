package com.chat.zipchat.clone.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chat.zipchat.clone.Activity.ChatActivity;
import com.chat.zipchat.clone.Activity.Group.GroupChatActivity;
import com.chat.zipchat.clone.Model.GifStickers.StickerResponse;
import com.chat.zipchat.clone.R;

import java.util.ArrayList;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {

    Context mContext;
    ArrayList<StickerResponse.Result.Docs> arrayList;
    boolean mIsGroup;

    public StickerAdapter(Context mContext, ArrayList<StickerResponse.Result.Docs> arrayList, boolean isGroup) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.mIsGroup = isGroup;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sticker, viewGroup, false);
        return new StickerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Glide.with(mContext).load(arrayList.get(i).getStickers())
                .into(viewHolder.mStickerImage);

        viewHolder.mStickerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mIsGroup) {
                    ((GroupChatActivity) mContext).UploadGifStickers(arrayList.get(i).getStickers(), "8");
                } else {
                    ((ChatActivity) mContext).UploadGifStickers(arrayList.get(i).getStickers(), "8");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mStickerImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mStickerImage = itemView.findViewById(R.id.mStickerImage);
        }
    }
}
