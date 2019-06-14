package com.chat.zipchat.clone.Adapter.Payment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chat.zipchat.clone.Fragment.Payment.WithdrawnTransFragment;
import com.chat.zipchat.clone.Model.Payments.WithdrawnTransListPojo;
import com.chat.zipchat.clone.R;

import java.util.List;

import static com.chat.zipchat.clone.Common.BaseClass.DOLLAR_SYMBOL;
import static com.chat.zipchat.clone.Common.BaseClass.PaymentConvertedDateTime;

public class WithdrawnTransAdapter extends RecyclerView.Adapter<WithdrawnTransAdapter.ViewHolder> {

    Context mContext;
    List<WithdrawnTransListPojo> arrayList;
    WithdrawnTransFragment mWithdrawnTransFragment;

    public WithdrawnTransAdapter(Context mContext, List<WithdrawnTransListPojo> arrayList, WithdrawnTransFragment withdrawnTransFragment) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.mWithdrawnTransFragment = withdrawnTransFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_transactions, viewGroup, false);
        return new WithdrawnTransAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        viewHolder.mTransName.setText(mContext.getResources().getString(R.string.usd_amount));
        viewHolder.mTransAmount.setText(DOLLAR_SYMBOL + arrayList.get(i).getReceived());
        viewHolder.mTransId.setText(PaymentConvertedDateTime(arrayList.get(i).getCreatedTs()));

        viewHolder.mLlTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWithdrawnTransFragment.mPopupDone(arrayList.get(i));
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mLlTrans;
        TextView mTransType, mTransDate, mTransName, mTransAmount, mTransId, mTransStatus;
        RelativeLayout mRlHeading;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mLlTrans = itemView.findViewById(R.id.mLlTrans);
            mTransType = itemView.findViewById(R.id.mTransType);
            mTransDate = itemView.findViewById(R.id.mTransDate);
            mTransName = itemView.findViewById(R.id.mTransName);
            mTransAmount = itemView.findViewById(R.id.mTransAmount);
            mTransId = itemView.findViewById(R.id.mTransId);
            mTransStatus = itemView.findViewById(R.id.mTransStatus);

            mRlHeading = itemView.findViewById(R.id.mRlHeading);
            mRlHeading.setVisibility(View.GONE);
            mLlTrans.setPadding(0, 20, 0, 20);
        }
    }

}
