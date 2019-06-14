package com.chat.zipchat.clone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chat.zipchat.clone.Activity.VideoActivity;
import com.chat.zipchat.clone.Activity.WebActivity;
import com.chat.zipchat.clone.Activity.ZoomActivity;
import com.chat.zipchat.clone.R;

import java.util.ArrayList;

import static com.chat.zipchat.clone.Common.BaseClass.requestOptionsD;
import static com.chat.zipchat.clone.Common.BaseClass.requestOptionsT;

public class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int IMAGE = 1;
    private static final int VIDEO = 2;
    private static final int DOCUMENT = 3;

    Context mContext;
    ArrayList<String> arrayList;
    int itemType = 0;

    public MediaAdapter(Context mContext, ArrayList<String> arrayList, int itemType) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.itemType = itemType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {

            case IMAGE:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case VIDEO:
                View viewPhotos = inflater.inflate(R.layout.list_media_video, parent, false);
                viewHolder = new MediaAdapter.VideosItem(viewPhotos);
                break;
            case DOCUMENT:
                View viewDocumnets = inflater.inflate(R.layout.list_media_document, parent, false);
                viewHolder = new MediaAdapter.DocumentsItem(viewDocumnets);
                break;
        }

        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.list_media_photos, parent, false);
        viewHolder = new MediaAdapter.PhotosItem(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {

        switch (getItemViewType(position)) {

            case IMAGE:
                final PhotosItem photosItem = (PhotosItem) viewHolder;
                Glide.with(mContext).setDefaultRequestOptions(requestOptionsT()).load(arrayList.get(position))
                        .into(photosItem.mListPhotos);

                photosItem.mListPhotos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent mImageIntent = new Intent(mContext, ZoomActivity.class);
                        mImageIntent.putExtra("Value", 1);
                        mImageIntent.putExtra("PATH", arrayList.get(position));
                        mContext.startActivity(mImageIntent);
                    }
                });

                break;

            case VIDEO:
                final VideosItem videosItem = (VideosItem) viewHolder;

                videosItem.mCardListVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mVideoIntent = new Intent(mContext, VideoActivity.class);
                        mVideoIntent.putExtra("URL", arrayList.get(position));
                        mContext.startActivity(mVideoIntent);
                    }
                });

                break;

            case DOCUMENT:
                final DocumentsItem documentsItem = (DocumentsItem) viewHolder;

                documentsItem.mMediaDocumentCv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mDocumentIntent = new Intent(mContext, WebActivity.class);
                        mDocumentIntent.putExtra("URL", arrayList.get(position));
                        mContext.startActivity(mDocumentIntent);

                    }
                });

                break;
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class PhotosItem extends RecyclerView.ViewHolder {

        ImageView mListPhotos;

        public PhotosItem(View itemView) {
            super(itemView);

            mListPhotos = itemView.findViewById(R.id.mListPhotos);
        }
    }

    public class VideosItem extends RecyclerView.ViewHolder {

        CardView mCardListVideo;
        ImageView mListVideo;

        public VideosItem(View itemView) {
            super(itemView);

            mCardListVideo = itemView.findViewById(R.id.mCardListVideo);
            mListVideo = itemView.findViewById(R.id.mListVideo);
        }
    }

    public class DocumentsItem extends RecyclerView.ViewHolder {

        CardView mMediaDocumentCv;

        public DocumentsItem(View itemView) {
            super(itemView);

            mMediaDocumentCv = itemView.findViewById(R.id.mMediaDocumentCv);

        }
    }

    @Override
    public int getItemViewType(int position) {
        return itemType;
    }

}
