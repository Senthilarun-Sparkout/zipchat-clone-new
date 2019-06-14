package com.chat.zipchat.clone.Adapter.Payment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chat.zipchat.clone.Activity.Payments.SendXLMActivity;
import com.chat.zipchat.clone.Model.GetMobileNumber.GetMobileNumberResponse;
import com.chat.zipchat.clone.R;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.chat.zipchat.clone.Common.BaseClass.requestOptionsD;

public class MobileNumberAdapter extends RecyclerView.Adapter<MobileNumberAdapter.ViewHolder> {

    Context mContext;
    List<GetMobileNumberResponse> arrayList;

    public MobileNumberAdapter(Context mContext, List<GetMobileNumberResponse> arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_mobile_number, viewGroup, false);
        return new MobileNumberAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        Glide.with(mContext).setDefaultRequestOptions(requestOptionsD()).load(arrayList.get(position).getProfile_picture()).into(viewHolder.mListImgMobile);
        viewHolder.mListTxtMobileName.setText(arrayList.get(position).getFull_name());
        viewHolder.mListTxtMobileNumber.setText(arrayList.get(position).getMobile_number());

        viewHolder.mRlListMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("id", arrayList.get(position).get_id());
                hashMap.put("number", arrayList.get(position).getMobile_number());
                ((SendXLMActivity) mContext).mItemClick(hashMap);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout mRlListMobile;
        CircleImageView mListImgMobile;
        TextView mListTxtMobileName, mListTxtMobileNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mRlListMobile = itemView.findViewById(R.id.mRlListMobile);
            mListImgMobile = itemView.findViewById(R.id.mListImgMobile);
            mListTxtMobileName = itemView.findViewById(R.id.mListTxtMobileName);
            mListTxtMobileNumber = itemView.findViewById(R.id.mListTxtMobileNumber);

        }
    }
}
