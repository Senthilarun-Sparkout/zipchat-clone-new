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
import com.chat.zipchat.clone.Model.GifStickers.GifResponse;
import com.chat.zipchat.clone.R;

import java.util.ArrayList;

public class GifAdapter extends RecyclerView.Adapter<GifAdapter.ViewHolder> {

    Context mContext;
    boolean mIsGroup;
    ArrayList<GifResponse.GifData> arrayList;

    public GifAdapter(Context mContext, ArrayList<GifResponse.GifData> arrayList, boolean isGroup) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.mIsGroup = isGroup;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gif, viewGroup, false);
        return new GifAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Glide.with(mContext).load(arrayList.get(i).getImages().getOriginal().getUrl()).error(R.drawable.thumbnail_photo).into(viewHolder.mGifImage);

        viewHolder.mGifImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mIsGroup) {
                    ((GroupChatActivity) mContext).UploadGifStickers(arrayList.get(i).getImages().getOriginal().getUrl(), "7");
                } else {
                    ((ChatActivity) mContext).UploadGifStickers(arrayList.get(i).getImages().getOriginal().getUrl(), "7");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mGifImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mGifImage = itemView.findViewById(R.id.mGifImage);
        }
    }
}
