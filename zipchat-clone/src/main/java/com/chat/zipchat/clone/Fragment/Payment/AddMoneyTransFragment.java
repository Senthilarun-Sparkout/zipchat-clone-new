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

import com.chat.zipchat.clone.Adapter.Payment.AddMoneyTransAdapter;
import com.chat.zipchat.clone.Model.Payments.AddMoneyTransListPojo;
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
public class AddMoneyTransFragment extends Fragment {

    Context mContext;
    TextView mTxtNoTrans;
    RecyclerView mRecyclerTrans;
    AddMoneyTransAdapter addMoneyTransAdapter;

    @SuppressLint("ValidFragment")
    public AddMoneyTransFragment(Context context) {
        this.mContext = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trans, container, false);

        mTxtNoTrans = view.findViewById(R.id.mTxtNoTrans);
        mRecyclerTrans = view.findViewById(R.id.mRecyclerTrans);
        mRecyclerTrans.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        AddMoneyTransService();

        return view;
    }

    private void AddMoneyTransService() {
        if (isOnline(mContext)) {

            final Call<List<AddMoneyTransListPojo>> transactionResponseCall = apiInterfacePayment.depositTransactionList(UserId(mContext));
            transactionResponseCall.enqueue(new Callback<List<AddMoneyTransListPojo>>() {
                @Override
                public void onResponse(Call<List<AddMoneyTransListPojo>> call, Response<List<AddMoneyTransListPojo>> response) {
                    if (response.isSuccessful()) {

                        if (response.body().size() > 0) {
                            mRecyclerTrans.setVisibility(View.VISIBLE);
                            mTxtNoTrans.setVisibility(View.GONE);
                            addMoneyTransAdapter = new AddMoneyTransAdapter(mContext, response.body(), AddMoneyTransFragment.this);
                            mRecyclerTrans.setAdapter(addMoneyTransAdapter);
                        } else {
                            mRecyclerTrans.setVisibility(View.GONE);
                            mTxtNoTrans.setVisibility(View.VISIBLE);
                        }

                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<List<AddMoneyTransListPojo>> call, Throwable t) {
                    myLog("OnFailure", t.toString());
                }
            });
        }
    }

    public void mPopupDone(AddMoneyTransListPojo addMoneyTransListPojo) {

        final Dialog mPopupDoneDialog = new Dialog(getContext());
        mPopupDoneDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mPopupDoneDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mPopupDoneDialog.setContentView(R.layout.popup_transaction);
        mPopupDoneDialog.setCancelable(false);

        TextView mPopupAmount = mPopupDoneDialog.findViewById(R.id.mPopupAmount);
        TextView mPopupSendAmount = mPopupDoneDialog.findViewById(R.id.mPopupSendAmount);
        TextView mPopupTotalAmount = mPopupDoneDialog.findViewById(R.id.mPopupTotalAmount);
        TextView mPopupDateTime = mPopupDoneDialog.findViewById(R.id.mPopupDateTime);
        Button mPopupDone = mPopupDoneDialog.findViewById(R.id.mPopupDone);


        mPopupAmount.setText(DOLLAR_SYMBOL + addMoneyTransListPojo.getAmount());
        mPopupSendAmount.setText(DOLLAR_SYMBOL + addMoneyTransListPojo.getAmount());
        mPopupTotalAmount.setText(DOLLAR_SYMBOL + addMoneyTransListPojo.getAmount());

        TextView mTxtPopupSendAmount = mPopupDoneDialog.findViewById(R.id.mTxtPopupSendAmount);
        TextView mPopupHeading = mPopupDoneDialog.findViewById(R.id.mPopupHeading);
        RelativeLayout mRlPopupUser = mPopupDoneDialog.findViewById(R.id.mRlPopupUser);
        RelativeLayout mRlTransactionFee = mPopupDoneDialog.findViewById(R.id.mRlTransactionFee);

        mPopupHeading.setText(getResources().getString(R.string.you_have_deposited));
        mTxtPopupSendAmount.setText(getResources().getString(R.string.amount_deposited));
        mRlTransactionFee.setVisibility(View.GONE);
        mRlPopupUser.setVisibility(View.GONE);
        mPopupDateTime.setText(getResources().getString(R.string.deposited_on) + " "
                + PopUpConvertedDateTime(addMoneyTransListPojo.getCreatedTs()));

        mPopupDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupDoneDialog.dismiss();
            }
        });

        mPopupDoneDialog.show();

    }

}
