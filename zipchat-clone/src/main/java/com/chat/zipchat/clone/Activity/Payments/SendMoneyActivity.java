package com.chat.zipchat.clone.Activity.Payments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chat.zipchat.clone.Model.Chat.ChatPojo;
import com.chat.zipchat.clone.Model.Payments.GetAmountPojo;
import com.chat.zipchat.clone.Model.GetMobileNumber.GetMobileNumberResponse;
import com.chat.zipchat.clone.Model.Payments.GetUtoXPojo;
import com.chat.zipchat.clone.Model.Payments.GetXtoUPojo;
import com.chat.zipchat.clone.Model.SendAmount.SendAmountRequest;
import com.chat.zipchat.clone.Model.SendAmount.SendAmountResponse;
import com.chat.zipchat.clone.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.chat.zipchat.clone.Common.BaseClass.ConvertedDateTime;
import static com.chat.zipchat.clone.Common.BaseClass.DOLLAR_SYMBOL;
import static com.chat.zipchat.clone.Common.BaseClass.DecimalConvertion;
import static com.chat.zipchat.clone.Common.BaseClass.PopUpConvertedDateTime;
import static com.chat.zipchat.clone.Common.BaseClass.UserId;
import static com.chat.zipchat.clone.Common.BaseClass.apiInterfaceConvertion;
import static com.chat.zipchat.clone.Common.BaseClass.apiInterfacePayment;
import static com.chat.zipchat.clone.Common.BaseClass.isOnline;
import static com.chat.zipchat.clone.Common.BaseClass.myLog;
import static com.chat.zipchat.clone.Common.BaseClass.myToast;
import static com.chat.zipchat.clone.Common.BaseClass.removeProgressDialog;
import static com.chat.zipchat.clone.Common.BaseClass.showSimpleProgressDialog;

public class SendMoneyActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar mToolbarSendMoney;

    EditText mAmountSendMoney;
    TextView mNumberSendMoney;
    TextView mAvailableBalance, mTxtSendAmount, mTxtTransactionFee, mTxtTotal, mTxtAddMoney, mTxtWithdraw;
    Button mBtnSendProceed;

    float sendTransactionFee = 2;
    float xlmAmount, Fee, TransactionFee, Total;
    private static final int GET_MOBILE_REQUEST_CODE = 0;
    HashMap<String, String> hashMap;

    float myXLMBalance = 0, myAvailableBalance = 0, myTotalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);

        mToolbarSendMoney = findViewById(R.id.mToolbarSendMoney);
        setSupportActionBar(mToolbarSendMoney);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.send_money));
        mToolbarSendMoney.setTitleTextColor(getResources().getColor(R.color.white));
        mToolbarSendMoney.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        mAmountSendMoney = findViewById(R.id.mAmountSendMoney);
        mNumberSendMoney = findViewById(R.id.mNumberSendMoney);
        mAvailableBalance = findViewById(R.id.mAvailableBalance);
        mTxtSendAmount = findViewById(R.id.mTxtSendAmount);
        mTxtTransactionFee = findViewById(R.id.mTxtTransactionFee);
        mTxtTotal = findViewById(R.id.mTxtTotal);
        mTxtAddMoney = findViewById(R.id.mTxtAddMoney);
        mTxtWithdraw = findViewById(R.id.mTxtWithdraw);

        mBtnSendProceed = findViewById(R.id.mBtnSendProceed);
        mBtnSendProceed.setOnClickListener(this);
        mTxtAddMoney.setOnClickListener(this);
        mTxtWithdraw.setOnClickListener(this);

        mAmountSendMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTxtSendAmount.setError(null);
                if (s.toString().trim().length() == 1 && s.toString().equals(".")) {

                } else {
                    if (s.toString().trim().length() > 0) {

                        float Amount = Float.valueOf(s.toString());
                        mTxtSendAmount.setText(DOLLAR_SYMBOL + s.toString());
                        GetUtoXService(Amount, 1);

                    } else {
                        mTxtSendAmount.setText(DOLLAR_SYMBOL + "0");
                        mTxtTransactionFee.setText(DOLLAR_SYMBOL + "0");
                        mTxtTotal.setText(DOLLAR_SYMBOL + "0");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mNumberSendMoney.setOnClickListener(this);

        Intent intent = getIntent();
        hashMap = (HashMap<String, String>) intent.getSerializableExtra("userDetails");

        if (null != hashMap) {
            String number = hashMap.get("number");
            mNumberSendMoney.setError(null);
            mNumberSendMoney.setText(number);

            String type = hashMap.get("type");

            if (!TextUtils.isEmpty(type)) {
                if (type.equalsIgnoreCase("chat"))
                    mNumberSendMoney.setEnabled(false);
            }
        }

        GetAmountService();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (null != hashMap) {
            String type = hashMap.get("type");

            if (!TextUtils.isEmpty(type)) {
                if (type.equalsIgnoreCase("chat")) {
                    return true;
                } else {
                    getMenuInflater().inflate(R.menu.activity_sendmoney, menu);
                    return true;
                }
            }
        } else {
            getMenuInflater().inflate(R.menu.activity_sendmoney, menu);

            return true;
        }

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int i = item.getItemId();
        if (i == android.R.id.home) {
            this.finish();
        } else if (i == R.id.mToolScanner) {
            Scanning(this);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.mBtnSendProceed) {
            if (validation()) {
                showSimpleProgressDialog(this);
                float Amount = Float.valueOf(mAmountSendMoney.getText().toString());
                GetUtoXService(Amount, 2);
            }
        } else if (i == R.id.mTxtAddMoney) {
            Intent IntentAddMoney = new Intent(this, AddMoneyActivity.class);
            startActivity(IntentAddMoney);
            finish();
        } else if (i == R.id.mTxtWithdraw) {
            Intent IntentWithdraw = new Intent(this, WithdrawMoneyActivity.class);
            startActivity(IntentWithdraw);
            finish();
        } else if (i == R.id.mNumberSendMoney) {
            Intent mIntentNumber = new Intent(this, SendXLMActivity.class);
            startActivityForResult(mIntentNumber, GET_MOBILE_REQUEST_CODE);
        }
    }

    private Boolean validation() {

        if (myXLMBalance < 1) {
            myToast(this, getResources().getString(R.string.insufficient_balance));
            return false;
        } else if (TextUtils.isEmpty(mAmountSendMoney.getText().toString())) {
            mAmountSendMoney.setError(getResources().getString(R.string.enter_your_amount));
            return false;
        } else if (TextUtils.isEmpty(mNumberSendMoney.getText().toString())) {
            mNumberSendMoney.setError(getResources().getString(R.string.recipient_mobile_number));
            return false;
        } else if (myAvailableBalance < myTotalAmount) {
            myToast(this, getResources().getString(R.string.low_amount));
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                myToast(this, getResources().getString(R.string.result_not_found));
            }
        }

        if (result != null) {
            if (result.getContents() == null) {
                myToast(this, getResources().getString(R.string.result_not_found));
            } else {
                String mResult = result.getContents();
                String newDisplayNumber = mResult.replaceAll("[^0-9]", "");
                GetMobileNumber(newDisplayNumber);
            }
        }

        if (requestCode == GET_MOBILE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                hashMap = (HashMap<String, String>) data.getSerializableExtra("userDetails");
                String number = hashMap.get("number");
                mNumberSendMoney.setError(null);
                mNumberSendMoney.setText(number);
            }
        }
    }

    private void GetUtoXService(final float Amount, final int value) {
        if (isOnline(this)) {

            final Call<GetUtoXPojo> transactionResponseCall = apiInterfaceConvertion.getUtoXConvertion();
            transactionResponseCall.enqueue(new Callback<GetUtoXPojo>() {
                @Override
                public void onResponse(Call<GetUtoXPojo> call, Response<GetUtoXPojo> response) {
                    if (response.isSuccessful()) {
                        float XLM = Float.valueOf(response.body().getXLM());

                        xlmAmount = Amount * XLM;
                        Fee = (xlmAmount * sendTransactionFee) / 100;
                        Total = xlmAmount + ((xlmAmount * sendTransactionFee) / 100);

                        GetXtoUService(Amount, Fee, value);

                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<GetUtoXPojo> call, Throwable t) {
                    myLog("OnFailure", t.toString());
                }
            });
        }
    }

    private void SendAmount() {
        if (isOnline(this)) {

            /**
             * Calculations
             *
             * xlmAmount = UserAmount * market rate (XLM)
             * Total = xlmAmount + ((xlmAmount * sendTransactionFee) /100)
             * Fee = (xlmAmount * sendTransactionFee) / 100
             */

            SendAmountRequest sendAmountRequest = new SendAmountRequest();
            sendAmountRequest.setSender(UserId(this));
            sendAmountRequest.setReceiver(hashMap.get("id"));
            sendAmountRequest.setAmount(String.valueOf(DecimalConvertion(Total)));
            sendAmountRequest.setFee(String.valueOf(DecimalConvertion(Fee)));
            sendAmountRequest.setWalletAmount(String.valueOf(DecimalConvertion(Float.valueOf(mAmountSendMoney.getText().toString()))));
            sendAmountRequest.setWalletFee(String.valueOf(DecimalConvertion(TransactionFee)));

            final Call<SendAmountResponse> transactionResponseCall = apiInterfacePayment.sendAmount(sendAmountRequest);
            transactionResponseCall.enqueue(new Callback<SendAmountResponse>() {
                @Override
                public void onResponse(Call<SendAmountResponse> call, Response<SendAmountResponse> response) {
                    removeProgressDialog();
                    if (response.isSuccessful()) {
                        SendMoneyPopup(response.body());
                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<SendAmountResponse> call, Throwable t) {
                    removeProgressDialog();
                    myLog("OnFailure", t.toString());
                }
            });
        }
    }

    private void GetAmountService() {
        if (isOnline(this)) {

            final Call<List<GetAmountPojo>> getAmountResponseCall = apiInterfacePayment.getAmount(UserId(this));
            getAmountResponseCall.enqueue(new Callback<List<GetAmountPojo>>() {
                @Override
                public void onResponse(Call<List<GetAmountPojo>> call, Response<List<GetAmountPojo>> response) {
                    if (response.isSuccessful()) {

                        if (response.body().size() > 0) {
                            myXLMBalance = Float.valueOf(response.body().get(0).getBalance());
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
        if (isOnline(this)) {

            final Call<GetXtoUPojo> transactionResponseCall = apiInterfaceConvertion.getXtoUConvertion();
            transactionResponseCall.enqueue(new Callback<GetXtoUPojo>() {
                @Override
                public void onResponse(Call<GetXtoUPojo> call, Response<GetXtoUPojo> response) {
                    if (response.isSuccessful()) {
                        float USD = Float.valueOf(response.body().getUSD());
                        myAvailableBalance = USD * Amount;
                        mAvailableBalance.setText(getResources().getString(R.string.available_balance) + ": $ " + (USD * Amount));

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

    private void GetXtoUService(final float Amount, final float Fee, final int value) {
        if (isOnline(this)) {

            final Call<GetXtoUPojo> transactionResponseCall = apiInterfaceConvertion.getXtoUConvertion();
            transactionResponseCall.enqueue(new Callback<GetXtoUPojo>() {
                @Override
                public void onResponse(Call<GetXtoUPojo> call, Response<GetXtoUPojo> response) {
                    if (response.isSuccessful()) {
                        float USD = Float.valueOf(response.body().getUSD());
                        TransactionFee = Fee * USD;
                        myTotalAmount = Amount + TransactionFee;

                        if (!TextUtils.isEmpty(mAmountSendMoney.getText().toString())) {
                            mTxtTransactionFee.setText(DOLLAR_SYMBOL + TransactionFee);
                            mTxtTotal.setText(DOLLAR_SYMBOL + myTotalAmount);

                            if (value == 2) {
                                SendAmount();
                            }

                        } else {
                            mTxtSendAmount.setText(DOLLAR_SYMBOL + "0");
                            mTxtTransactionFee.setText(DOLLAR_SYMBOL + "0");
                            mTxtTotal.setText(DOLLAR_SYMBOL + "0");
                        }

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

    public void SendMoneyPopup(SendAmountResponse sendAmountResponse) {

        ChatPojo chatPojo = new ChatPojo();
        chatPojo.setFromId(UserId(this));
        chatPojo.setText(sendAmountResponse.getWalletAmount());
        chatPojo.setTimestamp(ConvertedDateTime());
        chatPojo.setToId(hashMap.get("id"));
        chatPojo.setMsgType("6");
        chatPojo.setIsRead("0");
        chatPojo.setFileUrl("");

        DatabaseReference referenceMessageInsert = FirebaseDatabase.getInstance().getReference("messages");
        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("user-messages").child(UserId(this));

        if (isOnline(this)) {
            String mGroupId = referenceMessageInsert.push().getKey();
            referenceMessageInsert.child(mGroupId).setValue(chatPojo);

            referenceUser.child(hashMap.get("id")).child(mGroupId).setValue("1");
            DatabaseReference referenceUserInsert = FirebaseDatabase.getInstance().getReference("user-messages").child(hashMap.get("id")).child(UserId(this));
            referenceUserInsert.child(mGroupId).setValue("1");
        }


        final Dialog mPopupDoneDialog = new Dialog(SendMoneyActivity.this);
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

        mPopupAmount.setText(DOLLAR_SYMBOL + sendAmountResponse.getWalletAmount());
        mPopupNumber.setText(mNumberSendMoney.getText().toString());
        mPopupSendAmount.setText(DOLLAR_SYMBOL + sendAmountResponse.getWalletAmount());
        mPopupTransactionFee.setText(DOLLAR_SYMBOL + sendAmountResponse.getWalletFee());
        float total = Float.valueOf(sendAmountResponse.getWalletAmount()) + Float.valueOf(sendAmountResponse.getWalletFee());
        mPopupTotalAmount.setText(DOLLAR_SYMBOL + String.valueOf(total));
        mPopupDateTime.setText("Sent on " + PopUpConvertedDateTime(sendAmountResponse.getCreatedTs()));

        mPopupDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupDoneDialog.dismiss();
                finish();
            }
        });

        mPopupDoneDialog.show();
    }

    private void GetMobileNumber(String number) {
        if (isOnline(this)) {

            showSimpleProgressDialog(this);

            final Call<GetMobileNumberResponse> transactionResponseCall = apiInterfacePayment.getUserDetails(number);
            transactionResponseCall.enqueue(new Callback<GetMobileNumberResponse>() {
                @Override
                public void onResponse(Call<GetMobileNumberResponse> call, Response<GetMobileNumberResponse> response) {
                    removeProgressDialog();
                    if (response.isSuccessful()) {
                        hashMap = new HashMap<>();
                        hashMap.put("id", response.body().get_id().toString());
                        mNumberSendMoney.setError(null);
                        mNumberSendMoney.setText(response.body().getMobile_number());

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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * Scanning to send money
     *
     * @param activity-to access context
     */
    public static void Scanning(Activity activity) {
        IntentIntegrator qrScan = new IntentIntegrator(activity);
        qrScan.setPrompt(" Scan the Code and Transfer Money");
        qrScan.setOrientationLocked(true);
        qrScan.initiateScan();
    }

}
