package com.chat.zipchat.clone.Fragment.Payment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.chat.zipchat.clone.Adapter.Payment.ReceiveTransAdapter;
import com.chat.zipchat.clone.Model.ReceiveTransaction.ReceiveTransListPojo;
import com.chat.zipchat.clone.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.chat.zipchat.clone.Common.BaseClass.DOLLAR_SYMBOL;
import static com.chat.zipchat.clone.Common.BaseClass.PopUpConvertedDateTime;
import static com.chat.zipchat.clone.Common.BaseClass.UserId;
import static com.chat.zipchat.clone.Common.BaseClass.apiInterfacePayment;
import static com.chat.zipchat.clone.Common.BaseClass.isOnline;
import static com.chat.zipchat.clone.Common.BaseClass.myLog;

@SuppressLint("ValidFragment")
public class ReceiveTransFragment extends Fragment {

    Context mContext;
    TextView mTxtNoTrans;
    RecyclerView mRecyclerTrans;
    ReceiveTransAdapter receiveTransAdapter;

    @SuppressLint("ValidFragment")
    public ReceiveTransFragment(Context context) {
        this.mContext = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trans, container, false);

        mTxtNoTrans = view.findViewById(R.id.mTxtNoTrans);
        mRecyclerTrans = view.findViewById(R.id.mRecyclerTrans);
        mRecyclerTrans.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        ReceiveTransService();

        return view;
    }

    private void ReceiveTransService() {
        if (isOnline(mContext)) {

            final Call<List<ReceiveTransListPojo>> transactionResponseCall = apiInterfacePayment.receiveTransactionList(UserId(mContext));
            transactionResponseCall.enqueue(new Callback<List<ReceiveTransListPojo>>() {
                @Override
                public void onResponse(Call<List<ReceiveTransListPojo>> call, Response<List<ReceiveTransListPojo>> response) {
                    if (response.isSuccessful()) {


                        if (response.body().size() > 0) {
                            mRecyclerTrans.setVisibility(View.VISIBLE);
                            mTxtNoTrans.setVisibility(View.GONE);
                            receiveTransAdapter = new ReceiveTransAdapter(mContext, response.body(), ReceiveTransFragment.this);
                            mRecyclerTrans.setAdapter(receiveTransAdapter);

                        } else {
                            mRecyclerTrans.setVisibility(View.GONE);
                            mTxtNoTrans.setVisibility(View.VISIBLE);
                        }


                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<List<ReceiveTransListPojo>> call, Throwable t) {
                    myLog("OnFailure", t.toString());
                }
            });
        }
    }

    public void mPopupDone(ReceiveTransListPojo receiveTransListPojo) {

        final Dialog mPopupDoneDialog = new Dialog(getContext());
        mPopupDoneDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mPopupDoneDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mPopupDoneDialog.setContentView(R.layout.popup_transaction);
        mPopupDoneDialog.setCancelable(false);

        TextView mPopupAmount = mPopupDoneDialog.findViewById(R.id.mPopupAmount);
        TextView mPopupNumber = mPopupDoneDialog.findViewById(R.id.mPopupNumber);
        TextView mPopupSendAmount = mPopupDoneDialog.findViewById(R.id.mPopupSendAmount);
        TextView mPopupTransactionFee = mPopupDoneDialog.findViewById(R.id.mPopupTransactionFee);
        TextView mPopupTotalAmount = mPopupDoneDialog.findViewById(R.id.mPopupTotalAmount);
        TextView mPopupDateTime = mPopupDoneDialog.findViewById(R.id.mPopupDateTime);
        Button mPopupDone = mPopupDoneDialog.findViewById(R.id.mPopupDone);

        mPopupAmount.setText(DOLLAR_SYMBOL + receiveTransListPojo.getWalletAmount());
        mPopupSendAmount.setText(DOLLAR_SYMBOL + receiveTransListPojo.getWalletAmount());
        mPopupTransactionFee.setText(DOLLAR_SYMBOL + receiveTransListPojo.getWalletFee());

        float total = Float.valueOf(receiveTransListPojo.getWalletAmount()) + Float.valueOf(receiveTransListPojo.getWalletFee());
        mPopupTotalAmount.setText(DOLLAR_SYMBOL + String.valueOf(total));

        if (receiveTransListPojo.getSender() != null) {
            mPopupNumber.setText(receiveTransListPojo.getSender().getFull_name());
        }

        TextView mTxtPopupSendAmount = mPopupDoneDialog.findViewById(R.id.mTxtPopupSendAmount);
        TextView mPopupHeading = mPopupDoneDialog.findViewById(R.id.mPopupHeading);
        TextView mTxtPopupUser = mPopupDoneDialog.findViewById(R.id.mTxtPopupUser);

        mPopupHeading.setText(getResources().getString(R.string.you_have_received));
        mTxtPopupSendAmount.setText(getResources().getString(R.string.amount_received));
        mTxtPopupUser.setText(getResources().getString(R.string.received_from));
        mPopupDateTime.setText(getResources().getString(R.string.received_on) + " "
                + PopUpConvertedDateTime(receiveTransListPojo.getCreatedTs()));

        mPopupDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupDoneDialog.dismiss();
            }
        });

        mPopupDoneDialog.show();

    }

}
