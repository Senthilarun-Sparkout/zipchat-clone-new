package com.chat.zipchat.clone.Activity.Group;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chat.zipchat.clone.Model.Contact.ResultItem;
import com.chat.zipchat.clone.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectedContactListAdapter extends RecyclerView.Adapter<SelectedContactListAdapter.ViewHolder> {

    private Context mContext;
    private List<ResultItem> selectedList;
    private boolean mIsCreateGroup;

    public SelectedContactListAdapter(Context mContext, List<ResultItem> selectedList, boolean isCreateGroup) {
        this.mContext = mContext;
        this.selectedList = selectedList;
        this.mIsCreateGroup = isCreateGroup;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_selected_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        if (mIsCreateGroup) {
            holder.imgRemoveContact.setVisibility(View.GONE);
        }

        String[] splitted = selectedList.get(position).getName().split("\\s+");
        holder.tv_contact_name.setText(splitted[0]);
        Glide.with(mContext).load(selectedList.get(position).getProfile_picture()).error(R.drawable.defult_user).into(holder.imgSelectContact);
        holder.imgRemoveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SelectGroupMemberActivity) mContext).removeSelectedList(selectedList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return selectedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_contact_name;
        CircleImageView imgSelectContact;
        ImageView imgRemoveContact;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgSelectContact = itemView.findViewById(R.id.img_select_contact);
            imgRemoveContact = itemView.findViewById(R.id.img_remove_contact);
            tv_contact_name = itemView.findViewById(R.id.tv_contact_name);

        }
    }
}
