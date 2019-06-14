package com.chat.zipchat.clone.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.chat.zipchat.clone.Activity.ChatActivity;
import com.chat.zipchat.clone.Model.ResultItem;
import com.chat.zipchat.clone.R;

import java.util.ArrayList;
import java.util.List;

import static com.chat.zipchat.clone.Common.BaseClass.requestOptionsD;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private List<ResultItem> chatAdapterList;
    public List<ResultItem> StudentListTemp;
    public ContactListAdapter.SubjectDataFilter studentDataFilter;


    public ContactListAdapter(Context context, List<ResultItem> chatAdapterlist) {
        mContext = context;
        this.chatAdapterList = chatAdapterlist;

        this.StudentListTemp = new ArrayList<ResultItem>();
        this.StudentListTemp.addAll(chatAdapterlist);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_contact, viewGroup, false);
        return new ContactListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {


        viewHolder.mTxtContactName.setText(StudentListTemp.get(position).getName());
        Glide.with(mContext).setDefaultRequestOptions(requestOptionsD()).load(StudentListTemp.get(position).getProfile_picture()).into(viewHolder.mImgUserContact);

        viewHolder.Rl_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("toId", StudentListTemp.get(position).getId());
                intent.putExtra("name", StudentListTemp.get(position).getName());
                mContext.startActivity(intent);
            }
        });

    }

    public Object getItem(int position) {
        return StudentListTemp.get(position);
    }

    @Override
    public int getItemCount() {
        return StudentListTemp.size();
    }

    @Override
    public Filter getFilter() {
        if (studentDataFilter == null) {

            studentDataFilter = new ContactListAdapter.SubjectDataFilter();
        }
        return studentDataFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout Rl_contact;
        ImageView mImgUserContact;
        TextView mTxtContactName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Rl_contact = itemView.findViewById(R.id.Rl_contact);
            mImgUserContact = itemView.findViewById(R.id.mImgUserContact);
            mTxtContactName = itemView.findViewById(R.id.mTxtContactName);
        }
    }

    private class SubjectDataFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            charSequence = charSequence.toString().toLowerCase();

            FilterResults filterResults = new FilterResults();

            if (charSequence != null && charSequence.toString().length() > 0) {

                List<ResultItem> arrayList1 = new ArrayList<ResultItem>();

                for (int i = 0, l = chatAdapterList.size(); i < l; i++) {
                    ResultItem subject = chatAdapterList.get(i);

                    if (subject.getName().toString().toLowerCase().contains(charSequence) || subject.getMobile_number().toString().contains(charSequence))

                        arrayList1.add(subject);
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

            StudentListTemp = (ArrayList<ResultItem>) filterResults.values;
            notifyDataSetChanged();
        }
    }

    public void updateContacttList(List<ResultItem> newlist) {

        this.chatAdapterList = newlist;
        this.StudentListTemp = new ArrayList<ResultItem>();
        this.StudentListTemp.addAll(newlist);
        notifyDataSetChanged();
    }


}
