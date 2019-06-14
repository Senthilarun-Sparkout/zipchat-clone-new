package com.chat.zipchat.clone.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chat.zipchat.clone.Activity.MainActivity;
import com.chat.zipchat.clone.Activity.Payments.AddMoneyActivity;
import com.chat.zipchat.clone.Activity.Payments.ReceiveMoneyActivity;
import com.chat.zipchat.clone.Activity.Payments.SendMoneyActivity;
import com.chat.zipchat.clone.Activity.Payments.TransHistoryActivity;
import com.chat.zipchat.clone.Activity.Payments.WithdrawMoneyActivity;
import com.chat.zipchat.clone.Adapter.Payment.SendTransAdapter;
import com.chat.zipchat.clone.Model.GetMobileNumber.GetMobileNumberResponse;
import com.chat.zipchat.clone.Model.Payments.GetAmountPojo;
import com.chat.zipchat.clone.Model.Payments.GetXtoUPojo;
import com.chat.zipchat.clone.Model.SentTransaction.SentTransListPojo;
import com.chat.zipchat.clone.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.chat.zipchat.clone.Common.BaseClass.DOLLAR_SYMBOL;
import static com.chat.zipchat.clone.Common.BaseClass.OTHER_FRAGMENT;
import static com.chat.zipchat.clone.Common.BaseClass.PopUpConvertedDateTime;
import static com.chat.zipchat.clone.Common.BaseClass.UserId;
import static com.chat.zipchat.clone.Common.BaseClass.apiInterfaceConvertion;
import static com.chat.zipchat.clone.Common.BaseClass.apiInterfacePayment;
import static com.chat.zipchat.clone.Common.BaseClass.isOnline;
import static com.chat.zipchat.clone.Common.BaseClass.myLog;
import static com.chat.zipchat.clone.Common.BaseClass.myToast;
import static com.chat.zipchat.clone.Common.BaseClass.removeProgressDialog;
import static com.chat.zipchat.clone.Common.BaseClass.showSimpleProgressDialog;

@SuppressLint("ValidFragment")
public class PaymentFragment extends Fragment implements View.OnClickListener {

    MainActivity mContext;
    TextView mAvailableBalance, mSeeAll, mTxtNoTransaction;
    LinearLayout mLlSend, mLlReceive, mLlAddMoney, mLlWithdraw;
    RecyclerView mRvTransaction;
    SendTransAdapter sendTransAdapter;

    public PaymentFragment(MainActivity mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        mAvailableBalance = view.findViewById(R.id.mAvailableBalance);
        mSeeAll = view.findViewById(R.id.mSeeAll);
        mTxtNoTransaction = view.findViewById(R.id.mTxtNoTransaction);
        mRvTransaction = view.findViewById(R.id.mRvTransaction);
        mRvTransaction.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        mLlSend = view.findViewById(R.id.mLlSend);
        mLlReceive = view.findViewById(R.id.mLlReceive);
        mLlAddMoney = view.findViewById(R.id.mLlAddMoney);
        mLlWithdraw = view.findViewById(R.id.mLlWithdraw);

        mLlSend.setOnClickListener(this);
        mLlReceive.setOnClickListener(this);
        mLlAddMoney.setOnClickListener(this);
        mLlWithdraw.setOnClickListener(this);
        mSeeAll.setOnClickListener(this);

        SendTransService();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_payments, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.mToolScanner:
                Scanning();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.mLlSend:
                Intent mSendMoney = new Intent(mContext, SendMoneyActivity.class);
                startActivity(mSendMoney);
                break;
            case R.id.mLlReceive:
                Intent mReceiveMoney = new Intent(mContext, ReceiveMoneyActivity.class);
                startActivity(mReceiveMoney);
                break;
            case R.id.mLlAddMoney:
                Intent mAddMoney = new Intent(mContext, AddMoneyActivity.class);
                startActivity(mAddMoney);
                break;
            case R.id.mLlWithdraw:
                Intent mWithdrawMoney = new Intent(mContext, WithdrawMoneyActivity.class);
                startActivity(mWithdrawMoney);
                break;
            case R.id.mSeeAll:
                Intent mTransHistory = new Intent(mContext, TransHistoryActivity.class);
                startActivity(mTransHistory);
                break;

        }
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
                            mRvTransaction.setVisibility(View.VISIBLE);
                            mTxtNoTransaction.setVisibility(View.GONE);
                            sendTransAdapter = new SendTransAdapter(mContext, response.body(), PaymentFragment.this);
                            mRvTransaction.setAdapter(sendTransAdapter);
                        } else {
                            mRvTransaction.setVisibility(View.GONE);
                            mTxtNoTransaction.setVisibility(View.VISIBLE);
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

    private void GetAmountService() {
        if (isOnline(mContext)) {

            final Call<List<GetAmountPojo>> getAmountResponseCall = apiInterfacePayment.getAmount(UserId(mContext));
            getAmountResponseCall.enqueue(new Callback<List<GetAmountPojo>>() {
                @Override
                public void onResponse(Call<List<GetAmountPojo>> call, Response<List<GetAmountPojo>> response) {
                    if (response.isSuccessful()) {
                        if (response.body().size() > 0) {
                            GetXtoUService(Float.valueOf(response.body().get(0).getBalance()));
                        }


                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<List<GetAmountPojo>> call, Throwable t) {
                    myLog("OnFailure", t.toString());
                }
            });
        }
    }

    private void GetXtoUService(final Float Amount) {
        if (isOnline(mContext)) {

            final Call<GetXtoUPojo> transactionResponseCall = apiInterfaceConvertion.getXtoUConvertion();
            transactionResponseCall.enqueue(new Callback<GetXtoUPojo>() {
                @Override
                public void onResponse(Call<GetXtoUPojo> call, Response<GetXtoUPojo> response) {
                    if (response.isSuccessful()) {
                        float USD = Float.valueOf(response.body().getUSD());
                        mAvailableBalance.setText("$ " + (USD * Amount));

                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<GetXtoUPojo> call, Throwable t) {
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

        mPopupDateTime.setText("Sent on " + PopUpConvertedDateTime(sentTransListPojo.getCreatedTs()));

        mPopupDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupDoneDialog.dismiss();
            }
        });

        mPopupDoneDialog.show();

    }

    @Override
    public void onResume() {
        super.onResume();
        GetAmountService();
        mContext.currentFragment = OTHER_FRAGMENT;
    }

    public void Scanning() {
        IntentIntegrator qrScan = new IntentIntegrator(getActivity());
        qrScan.setPrompt(getResources().getString(R.string.scan_code));
        qrScan.setOrientationLocked(true);
        qrScan.forSupportFragment(PaymentFragment.this).initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                myToast(mContext, getResources().getString(R.string.result_not_found));
            }
        }

        if (result != null) {
            if (result.getContents() == null) {
                myToast(mContext, getResources().getString(R.string.result_not_found));
            } else {
                String mResult = result.getContents();
                String newDisplayNumber = mResult.replaceAll("[^0-9]", "");
                GetMobileNumber(newDisplayNumber);
            }
        }
    }

    private void GetMobileNumber(String number) {
        if (isOnline(mContext)) {

            showSimpleProgressDialog(mContext);

            final Call<GetMobileNumberResponse> transactionResponseCall = apiInterfacePayment.getUserDetails(number);
            transactionResponseCall.enqueue(new Callback<GetMobileNumberResponse>() {
                @Override
                public void onResponse(Call<GetMobileNumberResponse> call, Response<GetMobileNumberResponse> response) {
                    removeProgressDialog();
                    if (response.isSuccessful()) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", response.body().get_id().toString());
                        hashMap.put("number", response.body().getMobile_number());

                        Intent mSentMoney = new Intent(mContext, SendMoneyActivity.class);
                        mSentMoney.putExtra("userDetails", hashMap);
                        startActivity(mSentMoney);

                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<GetMobileNumberResponse> call, Throwable t) {
                    removeProgressDialog();
                    myLog("OnFailure", t.toString());
                }
            });
        }
    }

}
