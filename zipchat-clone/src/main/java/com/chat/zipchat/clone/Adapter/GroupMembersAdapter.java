package com.chat.zipchat.clone.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chat.zipchat.clone.Activity.Group.GroupInfoActivity;
import com.chat.zipchat.clone.Common.App;
import com.chat.zipchat.clone.Model.Contact.ResultItem;
import com.chat.zipchat.clone.Model.Contact.ResultItemDao;
import com.chat.zipchat.clone.Model.Group.GroupMember;
import com.chat.zipchat.clone.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.chat.zipchat.clone.Common.BaseClass.UserId;

public class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.ViewHolder> {

    private Context mContext;
    private List<GroupMember> groupMemberList;

    public GroupMembersAdapter(Context mContext, List<GroupMember> groupMemberList) {
        this.mContext = mContext;
        this.groupMemberList = groupMemberList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_group_members, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        final GroupMember groupMember = groupMemberList.get(position);

        if (groupMember.getIsAdmin().equals("1")) {
            viewHolder.tvGrpAdmin.setVisibility(View.VISIBLE);
        }

        if (groupMember.getId().equals(UserId(mContext))) {
            viewHolder.tvGrpMemberName.setText(mContext.getResources().getString(R.string.you));
        } else {
            final List<ResultItem> resultItems = App.getmInstance().resultItemDao.queryBuilder().where(
                    ResultItemDao.Properties.Id.eq(groupMember.getId())).list();
            if (resultItems.size() > 0) {
                if (resultItems.get(0).getIsFromContact().equalsIgnoreCase("1")) {
                    viewHolder.tvGrpMemberName.setText(resultItems.get(0).getName());
                } else {
                    viewHolder.tvGrpMemberName.setText(resultItems.get(0).getMobile_number());
                }
            } else {
                viewHolder.tvGrpMemberName.setText(groupMember.getMobile_number());
            }
        }

        viewHolder.tvGrpMemberStatus.setText(groupMember.getStatus());
        Glide.with(mContext).load(groupMember.getProfile_picture()).error(R.drawable.defult_user).into(viewHolder.imgGroupMember);

        viewHolder.rlGroupMembers.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!groupMember.getId().equals(UserId(mContext))) {
                    GroupMembersAdapter.this.popupMenu(viewHolder.rlGroupMembers, groupMember);
                }
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return groupMemberList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rlGroupMembers;
        CircleImageView imgGroupMember;
        TextView tvGrpAdmin, tvGrpMemberName, tvGrpMemberStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rlGroupMembers = itemView.findViewById(R.id.rl_group_members);
            imgGroupMember = itemView.findViewById(R.id.img_group_member);
            tvGrpAdmin = itemView.findViewById(R.id.tv_grp_admin);
            tvGrpMemberName = itemView.findViewById(R.id.tv_grp_member_name);
            tvGrpMemberStatus = itemView.findViewById(R.id.tv_grp_member_status);
        }
    }

    private void popupMenu(RelativeLayout rlGroupMembers, final GroupMember groupMember) {
        PopupMenu popup = new PopupMenu(mContext, rlGroupMembers, Gravity.CENTER);
        popup.getMenuInflater().inflate(R.menu.group_member_popup, popup.getMenu());

        for (int i = 0; i < groupMemberList.size(); i++) {
            if (groupMemberList.get(i).getId().equals(UserId(mContext)) && groupMemberList.get(i).getIsAdmin().equals("1")) {
                popup.getMenu().findItem(R.id.nav_remove).setVisible(true);
                if (groupMember.getIsAdmin().equals("1")) {
                    popup.getMenu().findItem(R.id.nav_dismiss_admin).setVisible(true);
                } else {
                    popup.getMenu().findItem(R.id.nav_make_admin).setVisible(true);
                }
            }
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ((GroupInfoActivity) mContext).GroupMemberPopupAction(item, groupMember);
                return true;
            }
        });
        popup.show();
    }

}
