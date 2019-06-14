package com.chat.zipchat.clone.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chat.zipchat.clone.Common.App;
import com.chat.zipchat.clone.Model.ResultItem;
import com.chat.zipchat.clone.Model.GroupMember;
import com.chat.zipchat.clone.Model.ResultItemDao;
import com.chat.zipchat.clone.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DeliverOrSeenAdapter extends RecyclerView.Adapter<DeliverOrSeenAdapter.ViewHolder> {

    private Context mContext;
    private List<GroupMember> groupMemberList;

    public DeliverOrSeenAdapter(Context mContext, List<GroupMember> groupMemberList) {
        this.mContext = mContext;
        this.groupMemberList = groupMemberList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_deliver_seen_members, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        GroupMember groupMember = groupMemberList.get(position);

        final List<ResultItem> resultItems = App.getmInstance().resultItemDao.queryBuilder().where(
                ResultItemDao.Properties.Id.eq(groupMember.getId())).list();
        if (resultItems.size() > 0) {
            if (resultItems.get(0).getIsFromContact().equalsIgnoreCase("1")) {
                viewHolder.tvDeliverSeenMember.setText(resultItems.get(0).getName());
            } else {
                viewHolder.tvDeliverSeenMember.setText(resultItems.get(0).getMobile_number());
                viewHolder.tvDeliverSeenName.setVisibility(View.VISIBLE);
                viewHolder.tvDeliverSeenName.setText(resultItems.get(0).getName());
            }
        } else {
            viewHolder.tvDeliverSeenMember.setText(groupMember.getMobile_number());
            viewHolder.tvDeliverSeenName.setVisibility(View.VISIBLE);
            viewHolder.tvDeliverSeenName.setText(groupMember.getName());
        }

        Glide.with(mContext).load(groupMember.getProfile_picture()).error(R.drawable.defult_user).into(viewHolder.imgDeliverSeenMember);

    }

    @Override
    public int getItemCount() {
        return groupMemberList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imgDeliverSeenMember;
        TextView tvDeliverSeenMember, tvDeliverSeenName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDeliverSeenMember = itemView.findViewById(R.id.tv_deliver_seen_member);
            imgDeliverSeenMember = itemView.findViewById(R.id.img_deliver_seen_member);
            tvDeliverSeenName = itemView.findViewById(R.id.tv_deliver_seen_name);
        }
    }


}
