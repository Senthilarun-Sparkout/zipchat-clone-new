package com.chat.zipchat.clone.Adapter.Payment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chat.zipchat.clone.Fragment.Payment.ReceiveTransFragment;
import com.chat.zipchat.clone.Model.ReceiveTransaction.ReceiveTransListPojo;
import com.chat.zipchat.clone.R;

import java.util.List;

import static com.chat.zipchat.clone.Common.BaseClass.DOLLAR_SYMBOL;
import static com.chat.zipchat.clone.Common.BaseClass.PaymentConvertedDateTime;

public class ReceiveTransAdapter extends RecyclerView.Adapter<ReceiveTransAdapter.ViewHolder> {


    Context mContext;
    List<ReceiveTransListPojo> arrayList;
    ReceiveTransFragment mReceiveTransFragment;

    public ReceiveTransAdapter(Context mContext, List<ReceiveTransListPojo> arrayList, ReceiveTransFragment receiveTransFragment) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.mReceiveTransFragment = receiveTransFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_transactions, viewGroup, false);
        return new ReceiveTransAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        viewHolder.mTransType.setText(mContext.getResources().getString(R.string.received_from));
        viewHolder.mTransDate.setText(PaymentConvertedDateTime(arrayList.get(i).getCreatedTs()));
        viewHolder.mTransAmount.setText(DOLLAR_SYMBOL + arrayList.get(i).getWalletAmount());
        viewHolder.mTransId.setText(arrayList.get(i).get_id());

        if (arrayList.get(i).getSender() != null) {
            viewHolder.mTransName.setText(arrayList.get(i).getSender().getFull_name());
        }

        viewHolder.mLlTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReceiveTransFragment.mPopupDone(arrayList.get(i));
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
