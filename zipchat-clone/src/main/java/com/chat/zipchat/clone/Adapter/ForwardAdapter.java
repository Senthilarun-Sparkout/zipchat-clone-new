package com.chat.zipchat.clone.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chat.zipchat.clone.Activity.ForwardActivity;
import com.chat.zipchat.clone.Common.App;
import com.chat.zipchat.clone.Model.ChatList.ChatListPojo;
import com.chat.zipchat.clone.Model.Contact.ResultItem;
import com.chat.zipchat.clone.Model.Contact.ResultItemDao;
import com.chat.zipchat.clone.Model.Group.GroupItems;
import com.chat.zipchat.clone.Model.Group.GroupItemsDao;
import com.chat.zipchat.clone.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.chat.zipchat.clone.Common.BaseClass.isOnline;
import static com.chat.zipchat.clone.Common.BaseClass.requestOptionsD;

public class ForwardAdapter extends RecyclerView.Adapter<ForwardAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private List<ChatListPojo> chatAdapterList;
    private List<ChatListPojo> chatAdapterFilterList;

    private SubjectForwardDataFilter subjectForwardDataFilter;

    public ForwardAdapter(Context mContext, List<ChatListPojo> chatAdapterList) {
        this.mContext = mContext;
        this.chatAdapterList = chatAdapterList;

        this.chatAdapterFilterList = new ArrayList<ChatListPojo>();
        this.chatAdapterFilterList.addAll(chatAdapterList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_forward_contact, viewGroup, false);
        return new ForwardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final ChatListPojo chatListPojo = chatAdapterFilterList.get(i);

        if (chatListPojo.getIsGroup()) {

            final List<GroupItems> groupItems = App.getmInstance().groupItemsDao.queryBuilder().where(
                    GroupItemsDao.Properties.Id.eq(chatListPojo.getToId())).list();

            if (groupItems.size() > 0) {

                viewHolder.tvForwardName.setText(groupItems.get(0).getName());
                Glide.with(mContext).setDefaultRequestOptions(requestOptionsD()).load(groupItems.get(0).getGroup_picture()).error(R.drawable.ic_group_icon).into(viewHolder.imgForwardContact);
            } else {
                viewHolder.tvForwardName.setText(mContext.getResources().getString(R.string.group));
            }


        } else if (null != chatListPojo.getToId()) {

            final List<ResultItem> resultItems = App.getmInstance().resultItemDao.queryBuilder().where(
                    ResultItemDao.Properties.Id.eq(chatListPojo.getToId())).list();

            if (resultItems.size() > 0) {

                if (resultItems.get(0).getIsFromContact().equalsIgnoreCase("1")) {
                    viewHolder.tvForwardName.setText(resultItems.get(0).getName());
                } else {
                    viewHolder.tvForwardName.setText(resultItems.get(0).getMobile_number());
                }
                viewHolder.tvForwardStatus.setText(resultItems.get(0).getStatus());
                Glide.with(mContext).setDefaultRequestOptions(requestOptionsD()).load(resultItems.get(0).getProfile_picture()).error(R.drawable.defult_user).into(viewHolder.imgForwardContact);
            } else {
                viewHolder.tvForwardName.setText("Unknown");
            }
        }

        viewHolder.rlForwardContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline(mContext)) {
                    ((ForwardActivity) mContext).selectedContact(chatListPojo);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatAdapterFilterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rlForwardContact;
        CircleImageView imgForwardContact;
        TextView tvForwardName, tvForwardStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rlForwardContact = itemView.findViewById(R.id.rl_forward_contact);
            imgForwardContact = itemView.findViewById(R.id.img_forward_contact);
            tvForwardName = itemView.findViewById(R.id.tv_forward_name);
            tvForwardStatus = itemView.findViewById(R.id.tv_forward_status);

        }
    }

    @Override
    public Filter getFilter() {
        if (subjectForwardDataFilter == null) {

            subjectForwardDataFilter = new SubjectForwardDataFilter();
        }
        return subjectForwardDataFilter;
    }

    private class SubjectForwardDataFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            charSequence = charSequence.toString().toLowerCase();
            FilterResults filterResults = new FilterResults();

            if (charSequence != null && charSequence.toString().length() > 0) {

                List<ChatListPojo> arrayList1 = new ArrayList<ChatListPojo>();

                for (int i = 0, l = chatAdapterList.size(); i < l; i++) {
                    ChatListPojo newChatListPojo = chatAdapterList.get(i);

                    String name = "", mobile_number = "";


                    if (newChatListPojo.getIsGroup()) {

                        final List<GroupItems> groupItems = App.getmInstance().groupItemsDao.queryBuilder().where(
                                GroupItemsDao.Properties.Id.eq(newChatListPojo.getToId())).list();

                        if (groupItems.size() > 0) {
                            name = groupItems.get(0).getName();
                        }


                    } else if (null != newChatListPojo.getToId()) {

                        final List<ResultItem> resultItems = App.getmInstance().resultItemDao.queryBuilder().where(
                                ResultItemDao.Properties.Id.eq(newChatListPojo.getToId())).list();

                        if (resultItems.size() > 0) {

                            name = resultItems.get(0).getName();
                            mobile_number = resultItems.get(0).getMobile_number();

                        }
                    }

                    if (name.toLowerCase().contains(charSequence) || mobile_number.contains(charSequence)) {
                        arrayList1.add(newChatListPojo);
                    }

                }
                filterResults.count = arrayList1.size();

                filterResults.values = arrayList1;
            } else {
                synchronized (this) {
                    filterResults.values = chatAdapterList;

                    filterResults.count = chatAdapterList.size();
                }
            }
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            chatAdapterFilterList = (ArrayList<ChatListPojo>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
