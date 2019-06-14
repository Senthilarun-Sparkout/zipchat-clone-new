package com.chat.zipchat.clone.Activity.Group;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chat.zipchat.clone.Model.ResultItem;
import com.chat.zipchat.clone.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupContactListAdapter extends RecyclerView.Adapter<GroupContactListAdapter.ViewHolder> implements Filterable {

    Context mContext;
    List<ResultItem> arrayList;
    List<ResultItem> filterArrayList;
    public SubjectDataFilter contactDataFilter;

    public GroupContactListAdapter(Context mContext, List<ResultItem> arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;

        this.filterArrayList = new ArrayList<>();
        this.filterArrayList.addAll(arrayList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_group_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.tvContactName.setText(filterArrayList.get(position).getName());
        Glide.with(mContext).load(arrayList.get(position).getProfile_picture()).error(R.drawable.defult_user).into(holder.imgContact);

        if (filterArrayList.get(position).getIsSelected()) {
            holder.imgSelectedTick.setVisibility(View.VISIBLE);
        } else {
            holder.imgSelectedTick.setVisibility(View.GONE);
        }


        holder.rlContactList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (filterArrayList.get(position).getIsSelected()) {
                    ((SelectGroupMemberActivity) mContext).removeSelectedList(filterArrayList.get(position));
                } else {
                    ((SelectGroupMemberActivity) mContext).getSelectedList(filterArrayList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filterArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rlContactList;
        CircleImageView imgContact;
        ImageView imgSelectedTick;
        TextView tvContactName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rlContactList = itemView.findViewById(R.id.rl_contact_list);
            imgContact = itemView.findViewById(R.id.img_contact);
            tvContactName = itemView.findViewById(R.id.tv_contact_name);
            imgSelectedTick = itemView.findViewById(R.id.img_selected_tick);
        }
    }

    @Override
    public Filter getFilter() {
        if (contactDataFilter == null) {

            contactDataFilter = new SubjectDataFilter();
        }
        return contactDataFilter;
    }

    private class SubjectDataFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            charSequence = charSequence.toString().toLowerCase();

            FilterResults filterResults = new FilterResults();

            if (charSequence != null && charSequence.toString().length() > 0) {

                List<ResultItem> arrayList1 = new ArrayList<ResultItem>();

                for (int i = 0, l = arrayList.size(); i < l; i++) {
                    ResultItem subject = arrayList.get(i);

                    if (subject.getName().toString().toLowerCase().contains(charSequence) || subject.getMobile_number().toString().contains(charSequence))

                        arrayList1.add(subject);
                }
                filterResults.count = arrayList1.size();

                filterResults.values = arrayList1;
            } else {
                synchronized (this) {
                    filterResults.values = arrayList;

                    filterResults.count = arrayList.size();
                }
            }
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            filterArrayList = (ArrayList<ResultItem>) filterResults.values;
            notifyDataSetChanged();
        }
    }

}
