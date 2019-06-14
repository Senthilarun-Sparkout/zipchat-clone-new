package com.chat.zipchat.clone.Activity.Payments;

import android.app.Dialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chat.zipchat.clone.Adapter.ViewPagerAdapter.AddmoneySavedCardPagerAdapter;
import com.chat.zipchat.clone.Model.DepositTrans.CardDetails;
import com.chat.zipchat.clone.Model.DepositTrans.DepositTransRequest;
import com.chat.zipchat.clone.Model.DepositTrans.DepositTranssResponce;
import com.chat.zipchat.clone.Model.Payments.GetUserSavedCards;
import com.chat.zipchat.clone.Model.Payments.GetUtoXPojo;
import com.chat.zipchat.clone.R;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.VISIBLE;
import static com.chat.zipchat.clone.Common.BaseClass.DOLLAR_SYMBOL;
import static com.chat.zipchat.clone.Common.BaseClass.DecimalConvertion;
import static com.chat.zipchat.clone.Common.BaseClass.PUBLISHABLE_KEY;
import static com.chat.zipchat.clone.Common.BaseClass.PopUpConvertedDateTime;
import static com.chat.zipchat.clone.Common.BaseClass.UserId;
import static com.chat.zipchat.clone.Common.BaseClass.apiInterfaceConvertion;
import static com.chat.zipchat.clone.Common.BaseClass.apiInterfacePayment;
import static com.chat.zipchat.clone.Common.BaseClass.isOnline;
import static com.chat.zipchat.clone.Common.BaseClass.myLog;
import static com.chat.zipchat.clone.Common.BaseClass.myToast;
import static com.chat.zipchat.clone.Common.BaseClass.removeProgressDialog;
import static com.chat.zipchat.clone.Common.BaseClass.showSimpleProgressDialog;

public class AddMoneyActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar mToolbarAddMoney;

    EditText mAddMoneyAmount;
    EditText mAddMoneyNumber, mAddMoneyName, mAddMoneyMm, mAddMoneyYy, mAddMoneyCvv;
    CheckBox mCbAddMoneySaveCard;
    Button mBtnAddMoneyProceed;
    TextView mTxtAddMoneyCardDetail;
    LinearLayout mLlAddMoneyNewCard;

    private TextView[] dots;
    LinearLayout mLlSavedCards, mSavedCardLayoutDots;
    ViewPager mSavedCardViewPager;
    AddmoneySavedCardPagerAdapter addmoneySavedCardPagerAdapter;
    List<GetUserSavedCards> mSavedCardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

        mToolbarAddMoney = findViewById(R.id.mToolbarAddMoney);
        setSupportActionBar(mToolbarAddMoney);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.add_money));
        mToolbarAddMoney.setTitleTextColor(getResources().getColor(R.color.white));
        mToolbarAddMoney.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        mAddMoneyAmount = findViewById(R.id.mAddMoneyAmount);
        mTxtAddMoneyCardDetail = findViewById(R.id.mTxtAddMoneyCardDetail);

        mAddMoneyNumber = findViewById(R.id.mAddMoneyNumber);
        mAddMoneyName = findViewById(R.id.mAddMoneyName);
        mAddMoneyMm = findViewById(R.id.mAddMoneyMm);
        mAddMoneyYy = findViewById(R.id.mAddMoneyYy);
        mAddMoneyCvv = findViewById(R.id.mAddMoneyCvv);

        mLlAddMoneyNewCard = findViewById(R.id.mLlAddMoneyNewCard);
        mCbAddMoneySaveCard = findViewById(R.id.mCbAddMoneySaveCard);
        mBtnAddMoneyProceed = findViewById(R.id.mBtnAddMoneyProceed);
        mBtnAddMoneyProceed.setOnClickListener(this);
        mTxtAddMoneyCardDetail.setOnClickListener(this);

        mLlSavedCards = findViewById(R.id.mLlSavedCards);
        mSavedCardLayoutDots = findViewById(R.id.mSavedCardLayoutDots);
        mSavedCardViewPager = findViewById(R.id.mSavedCardViewPager);
        mSavedCardViewPager.getLayoutParams().height = (int) getResources().getDimension(R.dimen.addmoney_saved_card);
        mSavedCardList = new ArrayList<>();

        SavedCardService();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.mBtnAddMoneyProceed) {
            if (validation()) {
                GetStripeToken();
            }
        } else if (i == R.id.mTxtAddMoneyCardDetail) {
            if (mLlAddMoneyNewCard.getVisibility() == VISIBLE) {
                mLlAddMoneyNewCard.setVisibility(View.GONE);
                mTxtAddMoneyCardDetail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_right, 0);
            } else {
                mLlAddMoneyNewCard.setVisibility(VISIBLE);
                mTxtAddMoneyCardDetail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down, 0);
            }
        }
    }

    private void SavedCardService() {
        if (isOnline(this)) {

            final Call<List<GetUserSavedCards>> transactionResponseCall = apiInterfacePayment.getUserSavedCards(UserId(this));
            transactionResponseCall.enqueue(new Callback<List<GetUserSavedCards>>() {
                @Override
                public void onResponse(Call<List<GetUserSavedCards>> call, Response<List<GetUserSavedCards>> response) {
                    if (response.isSuccessful()) {

                        if (response.body().size() > 0) {
                            mLlSavedCards.setVisibility(View.VISIBLE);
                            mSavedCardList = response.body();
                            addBottomDots(0);
                            mSavedCardViewPager.addOnPageChangeListener(viewPagerPageChangeListener);

                            addmoneySavedCardPagerAdapter = new AddmoneySavedCardPagerAdapter(AddMoneyActivity.this, response.body());
                            mSavedCardViewPager.setAdapter(addmoneySavedCardPagerAdapter);
                        } else {
                            mLlSavedCards.setVisibility(View.GONE);
                            mLlAddMoneyNewCard.setVisibility(View.VISIBLE);
                            mTxtAddMoneyCardDetail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down, 0);
                        }

                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<List<GetUserSavedCards>> call, Throwable t) {
                    myLog("OnFailure", t.toString());
                }
            });
        }
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[mSavedCardList.size()];

        int colorsActive = getResources().getColor(R.color.colorPrimary);
        int colorsInactive = getResources().getColor(R.color.tab_grey);

        mSavedCardLayoutDots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(30);
            dots[i].setTextColor(colorsInactive);
            mSavedCardLayoutDots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    public void mItemClick(GetUserSavedCards getUserSavedCards) {

        mLlAddMoneyNewCard.setVisibility(View.VISIBLE);
        mTxtAddMoneyCardDetail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down, 0);

        mAddMoneyNumber.setText(getUserSavedCards.getNumber());
        mAddMoneyName.setText(getUserSavedCards.getHolder());
        mAddMoneyName.setText(getUserSavedCards.getHolder());

        StringTokenizer mExpiry = new StringTokenizer(getUserSavedCards.getExpiry(), "/");
        String month = mExpiry.nextToken();
        String year = mExpiry.nextToken();

        mAddMoneyMm.setText(month);
        mAddMoneyYy.setText(year);

        mAddMoneyCvv.requestFocus();

    }

    private Boolean validation() {


        if (mLlAddMoneyNewCard.getVisibility() == View.GONE) {
            mLlAddMoneyNewCard.setVisibility(View.VISIBLE);
            mTxtAddMoneyCardDetail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down, 0);
        }

        if (TextUtils.isEmpty(mAddMoneyAmount.getText().toString())) {
            mAddMoneyAmount.requestFocus();
            mAddMoneyAmount.setError(getResources().getString(R.string.enter_your_amount));
            return false;
        } else if (TextUtils.isEmpty(mAddMoneyNumber.getText().toString())) {
            mAddMoneyNumber.requestFocus();
            mAddMoneyNumber.setError(getResources().getString(R.string.enter_your_card_number));
            return false;
        } else if (TextUtils.isEmpty(mAddMoneyName.getText().toString())) {
            mAddMoneyName.requestFocus();
            mAddMoneyName.setError(getResources().getString(R.string.enter_your_name));
            return false;
        } else if (TextUtils.isEmpty(mAddMoneyMm.getText().toString())) {
            mAddMoneyMm.requestFocus();
            mAddMoneyMm.setError(getResources().getString(R.string.mm));
            return false;
        } else if (Integer.parseInt(mAddMoneyMm.getText().toString()) > 12) {
            mAddMoneyMm.requestFocus();
            mAddMoneyMm.setError(getResources().getString(R.string.enter_valid_month));
            return false;
        } else if (TextUtils.isEmpty(mAddMoneyYy.getText().toString())) {
            mAddMoneyYy.requestFocus();
            mAddMoneyYy.setError(getResources().getString(R.string.yy));
            return false;
        } else if (TextUtils.isEmpty(mAddMoneyCvv.getText().toString())) {
            mAddMoneyCvv.requestFocus();
            mAddMoneyCvv.setError(getResources().getString(R.string.enter_your_cvv));
            return false;
        }

        return true;
    }

    private void GetStripeToken() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        showSimpleProgressDialog(this);

        String cardNumber = mAddMoneyNumber.getText().toString();
        Integer month = Integer.parseInt(mAddMoneyMm.getText().toString());
        Integer year = Integer.parseInt(mAddMoneyYy.getText().toString());
        String cvc = mAddMoneyCvv.getText().toString();

        Card card = new Card(cardNumber, month, year, cvc);
        Stripe stripe = new Stripe(this, PUBLISHABLE_KEY);

        stripe.createToken(card, new TokenCallback() {
            @Override
            public void onError(Exception error) {
                removeProgressDialog();
                myToast(AddMoneyActivity.this, error.getMessage());
            }

            @Override
            public void onSuccess(Token token) {
                myLog("token", token.getId());
                GetUtoXService(token.getId());
            }
        });


        /*try {
            Token token = stripe.createTokenSynchronous(card, PUBLISHABLE_KEY);
            GetUtoXService(token.getId());

        } catch (StripeException stripeEx) {
            removeProgressDialog();
            String errorMessage = stripeEx.getLocalizedMessage();
        }*/

    }

    private void GetUtoXService(final String token) {
        if (isOnline(this)) {

            final Call<GetUtoXPojo> transactionResponseCall = apiInterfaceConvertion.getUtoXConvertion();
            transactionResponseCall.enqueue(new Callback<GetUtoXPojo>() {
                @Override
                public void onResponse(Call<GetUtoXPojo> call, Response<GetUtoXPojo> response) {

                    if (response.isSuccessful()) {
                        float XLM = Float.valueOf(response.body().getXLM());
                        float Amount = Float.valueOf(mAddMoneyAmount.getText().toString());
                        float xlmAmount = Amount * XLM;
                        DepositAmount(token, String.valueOf(DecimalConvertion(xlmAmount)));

                    } else if (response.code() == 104) {
                        removeProgressDialog();
                    }
                }

                @Override
                public void onFailure(Call<GetUtoXPojo> call, Throwable t) {
                    removeProgressDialog();
                    myLog("OnFailure", t.toString());
                }
            });
        }
    }

    private void DepositAmount(String token, String xmlAmount) {
        if (isOnline(this)) {

            DepositTransRequest depositTransRequest = new DepositTransRequest();
            depositTransRequest.setStripeToken(token);
            depositTransRequest.setAmount(mAddMoneyAmount.getText().toString());
            depositTransRequest.setUser(UserId(this));
            depositTransRequest.setSaveCard(mCbAddMoneySaveCard.isChecked());
            depositTransRequest.setXlmAmount(xmlAmount);

            CardDetails cardDetails = new CardDetails();
            cardDetails.setUser(UserId(this));
            cardDetails.setNumber(mAddMoneyNumber.getText().toString());
            cardDetails.setHolder(mAddMoneyName.getText().toString());
            cardDetails.setExpiry(mAddMoneyMm.getText().toString() + "/" + mAddMoneyYy.getText().toString());
            depositTransRequest.setCard(cardDetails);

            final Call<DepositTranssResponce> transactionResponseCall = apiInterfacePayment.depositAmount(depositTransRequest);
            transactionResponseCall.enqueue(new Callback<DepositTranssResponce>() {
                @Override
                public void onResponse(Call<DepositTranssResponce> call, Response<DepositTranssResponce> response) {
                    removeProgressDialog();
                    if (response.isSuccessful()) {
                        AddMoneyPopup(response.body());
                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<DepositTranssResponce> call, Throwable t) {
                    removeProgressDialog();
                    myLog("OnFailure", t.toString());
                }
            });
        }
    }

    public void AddMoneyPopup(DepositTranssResponce depositTranssResponce) {

        final Dialog mPopupDoneDialog = new Dialog(this);
        mPopupDoneDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mPopupDoneDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mPopupDoneDialog.setContentView(R.layout.popup_transaction);
        mPopupDoneDialog.setCancelable(false);

        TextView mPopupAmount = mPopupDoneDialog.findViewById(R.id.mPopupAmount);
        TextView mPopupSendAmount = mPopupDoneDialog.findViewById(R.id.mPopupSendAmount);
        TextView mPopupTotalAmount = mPopupDoneDialog.findViewById(R.id.mPopupTotalAmount);
        TextView mPopupDateTime = mPopupDoneDialog.findViewById(R.id.mPopupDateTime);
        Button mPopupDone = mPopupDoneDialog.findViewById(R.id.mPopupDone);


        mPopupAmount.setText(DOLLAR_SYMBOL + depositTranssResponce.getFiat_amount());
        mPopupSendAmount.setText(DOLLAR_SYMBOL + depositTranssResponce.getFiat_amount());
        mPopupTotalAmount.setText(DOLLAR_SYMBOL + depositTranssResponce.getFiat_amount());

        TextView mTxtPopupSendAmount = mPopupDoneDialog.findViewById(R.id.mTxtPopupSendAmount);
        TextView mPopupHeading = mPopupDoneDialog.findViewById(R.id.mPopupHeading);
        RelativeLayout mRlPopupUser = mPopupDoneDialog.findViewById(R.id.mRlPopupUser);
        RelativeLayout mRlTransactionFee = mPopupDoneDialog.findViewById(R.id.mRlTransactionFee);

        mPopupHeading.setText(getResources().getString(R.string.you_have_deposited));
        mTxtPopupSendAmount.setText(getResources().getString(R.string.amount_deposited));
        mRlTransactionFee.setVisibility(View.GONE);
        mRlPopupUser.setVisibility(View.GONE);
        mPopupDateTime.setText(getResources().getString(R.string.deposited_on) + " "
                + PopUpConvertedDateTime(depositTranssResponce.getData().getCreatedTs()));

        mPopupDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupDoneDialog.dismiss();
                finish();
            }
        });

        mPopupDoneDialog.show();

    }

}
