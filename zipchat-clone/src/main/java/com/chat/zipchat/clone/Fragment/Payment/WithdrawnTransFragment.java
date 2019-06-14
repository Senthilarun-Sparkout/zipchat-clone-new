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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chat.zipchat.clone.Adapter.Payment.WithdrawnTransAdapter;
import com.chat.zipchat.clone.Model.Payments.WithdrawnTransListPojo;
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
public class WithdrawnTransFragment extends Fragment {

    Context mContext;
    TextView mTxtNoTrans;
    RecyclerView mRecyclerTrans;
    WithdrawnTransAdapter withdrawnTransAdapter;

    @SuppressLint("ValidFragment")
    public WithdrawnTransFragment(Context context) {
        this.mContext = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trans, container, false);

        mTxtNoTrans = view.findViewById(R.id.mTxtNoTrans);
        mRecyclerTrans = view.findViewById(R.id.mRecyclerTrans);
        mRecyclerTrans.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        WithdrawnTransService();

        return view;
    }


    private void WithdrawnTransService() {
        if (isOnline(mContext)) {

            final Call<List<WithdrawnTransListPojo>> transactionResponseCall = apiInterfacePayment.withdrawTransactionList(UserId(mContext));
            transactionResponseCall.enqueue(new Callback<List<WithdrawnTransListPojo>>() {
                @Override
                public void onResponse(Call<List<WithdrawnTransListPojo>> call, Response<List<WithdrawnTransListPojo>> response) {
                    if (response.isSuccessful()) {

                        if (response.body().size() > 0) {
                            mRecyclerTrans.setVisibility(View.VISIBLE);
                            mTxtNoTrans.setVisibility(View.GONE);
                            withdrawnTransAdapter = new WithdrawnTransAdapter(mContext, response.body(), WithdrawnTransFragment.this);
                            mRecyclerTrans.setAdapter(withdrawnTransAdapter);

                        } else {
                            mRecyclerTrans.setVisibility(View.GONE);
                            mTxtNoTrans.setVisibility(View.VISIBLE);
                        }

                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<List<WithdrawnTransListPojo>> call, Throwable t) {
                    myLog("OnFailure", t.toString());
                }
            });
        }
    }

    public void mPopupDone(WithdrawnTransListPojo withdrawnTransListPojo) {

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

        TextView mTxtPopupSendAmount = mPopupDoneDialog.findViewById(R.id.mTxtPopupSendAmount);
        TextView mPopupHeading = mPopupDoneDialog.findViewById(R.id.mPopupHeading);

        mPopupHeading.setText(getResources().getString(R.string.you_have_withdrawn));
        mTxtPopupSendAmount.setText(getResources().getString(R.string.amount_withdrawn));

        RelativeLayout mRlPopupUser = mPopupDoneDialog.findViewById(R.id.mRlPopupUser);
        mRlPopupUser.setVisibility(View.GONE);

        mPopupAmount.setText(DOLLAR_SYMBOL + withdrawnTransListPojo.getReceived());
        mPopupNumber.setText(withdrawnTransListPojo.getReceiver());
        mPopupSendAmount.setText(DOLLAR_SYMBOL + withdrawnTransListPojo.getReceived());
        mPopupTransactionFee.setText(DOLLAR_SYMBOL + withdrawnTransListPojo.getWalletFee());

        float total = Float.valueOf(withdrawnTransListPojo.getReceived()) + Float.valueOf(withdrawnTransListPojo.getWalletFee());
        mPopupTotalAmount.setText(DOLLAR_SYMBOL + String.valueOf(total));

        mPopupDateTime.setText(getResources().getString(R.string.withdrawn_on) + " "
                + PopUpConvertedDateTime(withdrawnTransListPojo.getCreatedTs()));

        mPopupDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupDoneDialog.dismiss();
            }
        });

        mPopupDoneDialog.show();

    }

}
