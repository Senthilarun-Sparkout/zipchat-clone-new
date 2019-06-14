package com.chat.zipchat.clone.Adapter.Payment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chat.zipchat.clone.Fragment.Payment.SentTransFragment;
import com.chat.zipchat.clone.Fragment.PaymentFragment;
import com.chat.zipchat.clone.Model.SentTransaction.SentTransListPojo;
import com.chat.zipchat.clone.R;

import java.util.List;

import static com.chat.zipchat.clone.Common.BaseClass.DOLLAR_SYMBOL;
import static com.chat.zipchat.clone.Common.BaseClass.PaymentConvertedDateTime;

public class SendTransAdapter extends RecyclerView.Adapter<SendTransAdapter.ViewHolder> {

    Context mContext;
    List<SentTransListPojo> arrayList;
    PaymentFragment mPaymentFragment;
    SentTransFragment mSentTransFragment;
    int mValue = 0;

    public SendTransAdapter(Context mContext, List<SentTransListPojo> arrayList, PaymentFragment paymentFragment) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.mPaymentFragment = paymentFragment;
    }

    public SendTransAdapter(Context mContext, List<SentTransListPojo> arrayList, int value, SentTransFragment sentTransFragment) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.mValue = value;
        this.mSentTransFragment = sentTransFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_transactions, viewGroup, false);
        return new SendTransAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        viewHolder.mTransType.setText(mContext.getResources().getString(R.string.sent_to));
        viewHolder.mTransDate.setText(PaymentConvertedDateTime(arrayList.get(i).getCreatedTs()));
        viewHolder.mTransAmount.setText(DOLLAR_SYMBOL + arrayList.get(i).getWalletAmount());

        if (arrayList.get(i).getReceiver() != null) {
            viewHolder.mTransName.setText(arrayList.get(i).getReceiver().getFull_name());
        }

        viewHolder.mTransId.setText(arrayList.get(i).get_id());

        viewHolder.mLlTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mValue == 0) {
                    mPaymentFragment.mPopupDone(arrayList.get(i));
                } else if (mValue == 1) {
                    mSentTransFragment.mPopupDone(arrayList.get(i));
                }
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mLlTrans = itemView.findViewById(R.id.mLlTrans);
            mTransType = itemView.findViewById(R.id.mTransType);
            mTransDate = itemView.findViewById(R.id.mTransDate);
            mTransName = itemView.findViewById(R.id.mTransName);
            mTransAmount = itemView.findViewById(R.id.mTransAmount);
            mTransId = itemView.findViewById(R.id.mTransId);
            mTransStatus = itemView.findViewById(R.id.mTransStatus);

        }
    }

}
