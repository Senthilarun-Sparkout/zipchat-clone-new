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

import com.chat.zipchat.clone.Adapter.Payment.SendTransAdapter;
import com.chat.zipchat.clone.Model.SentTransaction.SentTransListPojo;
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
import static com.chat.zipchat.clone.Common.BaseClass.removeProgressDialog;
import static com.chat.zipchat.clone.Common.BaseClass.showSimpleProgressDialog;

@SuppressLint("ValidFragment")
public class SentTransFragment extends Fragment {

    Context mContext;
    TextView mTxtNoTrans;
    RecyclerView mRecyclerTrans;
    SendTransAdapter sendTransAdapter;

    @SuppressLint("ValidFragment")
    public SentTransFragment(Context context) {
        this.mContext = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trans, container, false);

        mTxtNoTrans = view.findViewById(R.id.mTxtNoTrans);
        mRecyclerTrans = view.findViewById(R.id.mRecyclerTrans);
        mRecyclerTrans.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        SendTransService();

        return view;
    }

    private void SendTransService() {
        if (isOnline(mContext)) {

            showSimpleProgressDialog(mContext);

            final Call<List<SentTransListPojo>> transactionResponseCall = apiInterfacePayment.sendTransactionList(UserId(mContext));
            transactionResponseCall.enqueue(new Callback<List<SentTransListPojo>>() {
                @Override
                public void onResponse(Call<List<SentTransListPojo>> call, Response<List<SentTransListPojo>> response) {
                    removeProgressDialog();
                    if (response.isSuccessful()) {


                        if (response.body().size() > 0) {
                            mRecyclerTrans.setVisibility(View.VISIBLE);
                            mTxtNoTrans.setVisibility(View.GONE);
                            sendTransAdapter = new SendTransAdapter(mContext, response.body(), 1, SentTransFragment.this);
                            mRecyclerTrans.setAdapter(sendTransAdapter);
                        } else {
                            mRecyclerTrans.setVisibility(View.GONE);
                            mTxtNoTrans.setVisibility(View.VISIBLE);
                        }


                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<List<SentTransListPojo>> call, Throwable t) {
                    removeProgressDialog();
                    myLog("OnFailure", t.toString());
                }
            });
        }
    }

    public void mPopupDone(SentTransListPojo sentTransListPojo) {

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

        mPopupAmount.setText(DOLLAR_SYMBOL + sentTransListPojo.getWalletAmount());
        mPopupSendAmount.setText(DOLLAR_SYMBOL + sentTransListPojo.getWalletAmount());
        mPopupTransactionFee.setText(DOLLAR_SYMBOL + sentTransListPojo.getWalletFee());


        if (sentTransListPojo.getReceiver() != null) {
            mPopupNumber.setText(sentTransListPojo.getReceiver().getFull_name());

            float total = Float.valueOf(sentTransListPojo.getWalletAmount()) + Float.valueOf(sentTransListPojo.getWalletFee());
            mPopupTotalAmount.setText(DOLLAR_SYMBOL + String.valueOf(total));
        }

        mPopupDateTime.setText(getResources().getString(R.string.sent_on) + " "
                + PopUpConvertedDateTime(sentTransListPojo.getCreatedTs()));

        mPopupDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupDoneDialog.dismiss();
            }
        });

        mPopupDoneDialog.show();

    }

}
