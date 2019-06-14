package com.chat.zipchat.clone.Activity.Payments;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.bumptech.glide.Glide;
import com.chat.zipchat.clone.Adapter.ViewPagerAdapter.WithdrawSavedCardPagerAdapter;
import com.chat.zipchat.clone.Common.BaseClass;
import com.chat.zipchat.clone.Model.Payments.GetAmountPojo;
import com.chat.zipchat.clone.Model.Payments.GetUtoXPojo;
import com.chat.zipchat.clone.Model.Payments.GetWithdrawSavedCards;
import com.chat.zipchat.clone.Model.Payments.GetXtoUPojo;
import com.chat.zipchat.clone.Model.WithdrawTrans.UploadProofResponse;
import com.chat.zipchat.clone.Model.WithdrawTrans.WithdrawTransRequest;
import com.chat.zipchat.clone.Model.WithdrawTrans.WithdrawTransResponse;
import com.chat.zipchat.clone.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.chat.zipchat.clone.Common.BaseClass.DOLLAR_SYMBOL;
import static com.chat.zipchat.clone.Common.BaseClass.DecimalConvertion;
import static com.chat.zipchat.clone.Common.BaseClass.MY_REQUEST_CODE_IMAGE;
import static com.chat.zipchat.clone.Common.BaseClass.PopUpConvertedDateTime;
import static com.chat.zipchat.clone.Common.BaseClass.UserId;
import static com.chat.zipchat.clone.Common.BaseClass.apiInterfaceConvertion;
import static com.chat.zipchat.clone.Common.BaseClass.apiInterfacePayment;
import static com.chat.zipchat.clone.Common.BaseClass.isOnline;
import static com.chat.zipchat.clone.Common.BaseClass.mImageToString;
import static com.chat.zipchat.clone.Common.BaseClass.myLog;
import static com.chat.zipchat.clone.Common.BaseClass.myToast;
import static com.chat.zipchat.clone.Common.BaseClass.removeProgressDialog;
import static com.chat.zipchat.clone.Common.BaseClass.requestOptionsD;
import static com.chat.zipchat.clone.Common.BaseClass.showSimpleProgressDialog;
import static com.chat.zipchat.clone.Common.BaseClass.snackbar;

public class WithdrawMoneyActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar mToolbarWithdrawMoney;
    EditText mWithdrawAmount, mWithdrawUserFirstName, mWithdrawUserLastName, mWithdrawAccountNumber, mWithdrawAccountHolder,
            mWithdrawRoutingNumber, mWithdrawAddressLine1, mWithdrawAddressLine2,
            mWithdrawPostal, mWithdrawCity, mWithdrawState,
            mWithdrawMobileNumber, mWithdrawSecurityNumber, mWithdrawDate, mWithdrawMonth, mWithdrawYear;
    LinearLayout mWithdrawUploadProof, mLlUploadProof;
    Button mBtnWithdrawProceed;
    TextView mAvailableBalance;

    CircleImageView mImgUploadProof;
    CheckBox mCbWithdrawSaveCard;

    private TextView[] dots;
    LinearLayout mLlSavedCards, mSavedCardLayoutDots;
    ViewPager mSavedCardViewPager;
    WithdrawSavedCardPagerAdapter withdrawnTransAdapter;
    List<GetWithdrawSavedCards> mSavedCardList;

    float myXLMBalance = 0, myAvailableBalance = 0;
    String url;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_money);

        mToolbarWithdrawMoney = findViewById(R.id.mToolbarWithdrawMoney);
        setSupportActionBar(mToolbarWithdrawMoney);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.withdraw_money));
        mToolbarWithdrawMoney.setTitleTextColor(getResources().getColor(R.color.white));
        mToolbarWithdrawMoney.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        mWithdrawAmount = findViewById(R.id.mWithdrawAmount);
        mWithdrawUserFirstName = findViewById(R.id.mWithdrawUserFirstName);
        mWithdrawUserLastName = findViewById(R.id.mWithdrawUserLastName);
        mWithdrawAccountNumber = findViewById(R.id.mWithdrawAccountNumber);
        mWithdrawAccountHolder = findViewById(R.id.mWithdrawAccountHolder);
        mWithdrawRoutingNumber = findViewById(R.id.mWithdrawRoutingNumber);
        mWithdrawAddressLine1 = findViewById(R.id.mWithdrawAddressLine1);
        mWithdrawAddressLine2 = findViewById(R.id.mWithdrawAddressLine2);
        mWithdrawPostal = findViewById(R.id.mWithdrawPostal);
        mWithdrawCity = findViewById(R.id.mWithdrawCity);
        mWithdrawState = findViewById(R.id.mWithdrawState);
        mWithdrawMobileNumber = findViewById(R.id.mWithdrawMobileNumber);
        mWithdrawSecurityNumber = findViewById(R.id.mWithdrawSecurityNumber);
        mCbWithdrawSaveCard = findViewById(R.id.mCbWithdrawSaveCard);
        mImgUploadProof = findViewById(R.id.mImgUploadProof);
        mAvailableBalance = findViewById(R.id.mAvailableBalance);

        mWithdrawDate = findViewById(R.id.mWithdrawDate);
        mWithdrawMonth = findViewById(R.id.mWithdrawMonth);
        mWithdrawYear = findViewById(R.id.mWithdrawYear);

        mWithdrawUploadProof = findViewById(R.id.mWithdrawUploadProof);
        mBtnWithdrawProceed = findViewById(R.id.mBtnWithdrawProceed);

        mLlUploadProof = findViewById(R.id.mLlUploadProof);

        mLlSavedCards = findViewById(R.id.mLlSavedCards);
        mSavedCardLayoutDots = findViewById(R.id.mSavedCardLayoutDots);
        mSavedCardViewPager = findViewById(R.id.mSavedCardViewPager);
        mSavedCardViewPager.getLayoutParams().height = (int) getResources().getDimension(R.dimen.withdraw_saved_card);
        mSavedCardList = new ArrayList<>();

        mWithdrawUploadProof.setOnClickListener(this);
        mBtnWithdrawProceed.setOnClickListener(this);

        GetAmountService();

        SavedCardService();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mWithdrawUploadProof:
                imagePicker();
                break;
            case R.id.mBtnWithdrawProceed:
                if (validation()) {
                    GetUtoXService();
                }
                break;
        }
    }

    private void SavedCardService() {
        if (isOnline(this)) {

            final Call<List<GetWithdrawSavedCards>> transactionResponseCall = apiInterfacePayment.getWithdrawSavedCards(UserId(this));
            transactionResponseCall.enqueue(new Callback<List<GetWithdrawSavedCards>>() {
                @Override
                public void onResponse(Call<List<GetWithdrawSavedCards>> call, Response<List<GetWithdrawSavedCards>> response) {
                    if (response.isSuccessful()) {

                        if (response.body().size() > 0) {
                            mLlSavedCards.setVisibility(View.VISIBLE);
                            mSavedCardList = response.body();
                            addBottomDots(0);
                            mSavedCardViewPager.addOnPageChangeListener(viewPagerPageChangeListener);

                            withdrawnTransAdapter = new WithdrawSavedCardPagerAdapter(WithdrawMoneyActivity.this, response.body());
                            mSavedCardViewPager.setAdapter(withdrawnTransAdapter);
                        } else {
                            mLlSavedCards.setVisibility(View.GONE);
                        }

                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<List<GetWithdrawSavedCards>> call, Throwable t) {
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

    public void mWithdrawCardClick(GetWithdrawSavedCards getWithdrawSavedCards) {

        mWithdrawUserFirstName.setText(getWithdrawSavedCards.getFirstname());
        mWithdrawUserLastName.setText(getWithdrawSavedCards.getLastName());
        mWithdrawAddressLine1.setText(getWithdrawSavedCards.getLine1());
        mWithdrawAddressLine2.setText(getWithdrawSavedCards.getLine2());
        mWithdrawPostal.setText(getWithdrawSavedCards.getPostalCode());
        mWithdrawCity.setText(getWithdrawSavedCards.getCity());
        mWithdrawState.setText(getWithdrawSavedCards.getState());
        mWithdrawMobileNumber.setText(getWithdrawSavedCards.getPhoneNumber());
        mWithdrawAccountNumber.setText(getWithdrawSavedCards.getAccountNumber());
        mWithdrawAccountHolder.setText(getWithdrawSavedCards.getAccountHolder());
        mWithdrawSecurityNumber.setText(getWithdrawSavedCards.getSsn());
        mWithdrawDate.setText(getWithdrawSavedCards.getDay());
        mWithdrawMonth.setText(getWithdrawSavedCards.getMonth());
        mWithdrawYear.setText(getWithdrawSavedCards.getYear());

        mLlUploadProof.setVisibility(View.GONE);
        mImgUploadProof.setVisibility(View.VISIBLE);
        Glide.with(WithdrawMoneyActivity.this).setDefaultRequestOptions(requestOptionsD()).load(url).into(mImgUploadProof);
        url = getWithdrawSavedCards.getVerificationFile();
        Glide.with(WithdrawMoneyActivity.this).setDefaultRequestOptions(requestOptionsD()).load(url).into(mImgUploadProof);

    }

    private Boolean validation() {

        if (myXLMBalance < 1) {
            myToast(this, getResources().getString(R.string.insufficient_balance));
            return false;
        } else if (TextUtils.isEmpty(mWithdrawAmount.getText().toString())) {
            mWithdrawAmount.requestFocus();
            mWithdrawAmount.setError(getResources().getString(R.string.enter_your_amount));
            return false;
        } else if (TextUtils.isEmpty(mWithdrawUserFirstName.getText().toString())) {
            mWithdrawUserFirstName.requestFocus();
            mWithdrawUserFirstName.setError(getResources().getString(R.string.first_name));
            return false;
        } else if (TextUtils.isEmpty(mWithdrawUserLastName.getText().toString())) {
            mWithdrawUserLastName.requestFocus();
            mWithdrawUserLastName.setError(getResources().getString(R.string.last_name));
            return false;
        } else if (TextUtils.isEmpty(mWithdrawAccountNumber.getText().toString())) {
            mWithdrawAccountNumber.requestFocus();
            mWithdrawAccountNumber.setError(getResources().getString(R.string.account_number));
            return false;
        } else if (TextUtils.isEmpty(mWithdrawAccountHolder.getText().toString())) {
            mWithdrawAccountHolder.requestFocus();
            mWithdrawAccountHolder.setError(getResources().getString(R.string.account_holder));
            return false;
        } else if (TextUtils.isEmpty(mWithdrawRoutingNumber.getText().toString())) {
            mWithdrawRoutingNumber.requestFocus();
            mWithdrawRoutingNumber.setError(getResources().getString(R.string.routing_number));
            return false;
        } else if (TextUtils.isEmpty(mWithdrawAddressLine1.getText().toString())) {
            mWithdrawAddressLine1.requestFocus();
            mWithdrawAddressLine1.setError(getResources().getString(R.string.address_line1));
            return false;
        } else if (TextUtils.isEmpty(mWithdrawAddressLine2.getText().toString())) {
            mWithdrawAddressLine2.requestFocus();
            mWithdrawAddressLine2.setError(getResources().getString(R.string.address_line2));
            return false;
        } else if (TextUtils.isEmpty(mWithdrawPostal.getText().toString())) {
            mWithdrawPostal.requestFocus();
            mWithdrawPostal.setError(getResources().getString(R.string.postal_code));
            return false;
        } else if (TextUtils.isEmpty(mWithdrawCity.getText().toString())) {
            mWithdrawCity.requestFocus();
            mWithdrawCity.setError(getResources().getString(R.string.city));
            return false;
        } else if (TextUtils.isEmpty(mWithdrawState.getText().toString())) {
            mWithdrawState.requestFocus();
            mWithdrawState.setError(getResources().getString(R.string.state));
            return false;
        } else if (TextUtils.isEmpty(mWithdrawMobileNumber.getText().toString())) {
            mWithdrawMobileNumber.requestFocus();
            mWithdrawMobileNumber.setError(getResources().getString(R.string.enter_mobile_number));
            return false;
        } else if (TextUtils.isEmpty(mWithdrawSecurityNumber.getText().toString())) {
            mWithdrawSecurityNumber.requestFocus();
            mWithdrawSecurityNumber.setError(getResources().getString(R.string.social_security_number));
            return false;
        } else if (TextUtils.isEmpty(mWithdrawDate.getText().toString())) {
            mWithdrawDate.requestFocus();
            mWithdrawDate.setError(getResources().getString(R.string.date));
            return false;
        } else if (TextUtils.isEmpty(mWithdrawMonth.getText().toString())) {
            mWithdrawMonth.requestFocus();
            mWithdrawMonth.setError(getResources().getString(R.string.mm));
            return false;
        } else if (TextUtils.isEmpty(mWithdrawYear.getText().toString())) {
            mWithdrawYear.requestFocus();
            mWithdrawYear.setError(getResources().getString(R.string.yy));
            return false;
        } else if (Integer.parseInt(mWithdrawDate.getText().toString()) > 31) {
            mWithdrawDate.requestFocus();
            mWithdrawDate.setError(getResources().getString(R.string.date));
            return false;
        } else if (Integer.parseInt(mWithdrawMonth.getText().toString()) > 12) {
            mWithdrawMonth.requestFocus();
            mWithdrawMonth.setError(getResources().getString(R.string.mm));
            return false;
        } else if (TextUtils.isEmpty(url)) {
            myToast(this, getResources().getString(R.string.upload_proof));
            return false;
        } else if (myAvailableBalance < Float.valueOf(mWithdrawAmount.getText().toString())) {
            myToast(this, getResources().getString(R.string.low_amount));
            return false;
        }

        return true;
    }

    private void imagePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_REQUEST_CODE_IMAGE);
            } else {
                getImageFromGallery();
            }
        } else {
            getImageFromGallery();
        }
    }

    private void getImageFromGallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    UpdateProfileImageService(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void UpdateProfileImageService(Bitmap bitmap) {
        if (isOnline(this)) {

            showSimpleProgressDialog(this);
            final Call<UploadProofResponse> registerResponseCall = apiInterfacePayment.uploadProofImageform(mImageToString(bitmap));
            registerResponseCall.enqueue(new Callback<UploadProofResponse>() {
                @Override
                public void onResponse(Call<UploadProofResponse> call, Response<UploadProofResponse> response) {
                    removeProgressDialog();
                    if (response.isSuccessful()) {
                        mLlUploadProof.setVisibility(View.GONE);
                        mImgUploadProof.setVisibility(View.VISIBLE);
                        url = response.body().getUrl();
                        Glide.with(WithdrawMoneyActivity.this).setDefaultRequestOptions(requestOptionsD()).load(url).into(mImgUploadProof);
                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<UploadProofResponse> call, Throwable t) {
                    removeProgressDialog();
                    myLog("OnFailure", t.toString());
                }
            });
        } else {
            snackbar(this, mBtnWithdrawProceed, BaseClass.NO_INTERNET);
        }
    }

    private void GetUtoXService() {
        if (isOnline(this)) {
            showSimpleProgressDialog(this);
            final Call<GetUtoXPojo> transactionResponseCall = apiInterfaceConvertion.getUtoXConvertion();
            transactionResponseCall.enqueue(new Callback<GetUtoXPojo>() {
                @Override
                public void onResponse(Call<GetUtoXPojo> call, Response<GetUtoXPojo> response) {
                    if (response.isSuccessful()) {
                        float XLM = Float.valueOf(response.body().getXLM());
                        GetXtoUService(XLM, 2);

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

    private void GetXtoUService(final Float XLM, final int value) {
        if (isOnline(this)) {

            final Call<GetXtoUPojo> transactionResponseCall = apiInterfaceConvertion.getXtoUConvertion();
            transactionResponseCall.enqueue(new Callback<GetXtoUPojo>() {
                @Override
                public void onResponse(Call<GetXtoUPojo> call, Response<GetXtoUPojo> response) {
                    if (response.isSuccessful()) {
                        float USD = Float.valueOf(response.body().getUSD());
                        if (value == 1) {

                            myAvailableBalance = USD * XLM;
                            mAvailableBalance.setText(getResources().getString(R.string.available_balance) + ": $ " + (USD * XLM));

                        } else if (value == 2) {
                            WithdrawAmount(XLM, USD);
                        }
                    } else if (response.code() == 104) {
                        removeProgressDialog();
                    }
                }

                @Override
                public void onFailure(Call<GetXtoUPojo> call, Throwable t) {
                    removeProgressDialog();
                    myLog("OnFailure", t.toString());
                }
            });
        }
    }

    private void WithdrawAmount(Float XLM, Float USD) {
        if (isOnline(this)) {


            /**
             *
             * Rate calculation: (XLM * sellRate)/100
             * updatedAmount = XLM + rate
             * fee calculation = (updatedAmount * sellTransactionFee)/100
             * WalletFee = (rateCalculation + feeCalculation) * currentUsdRate
             * Total = XLM + Rate calculation + fee calculation
             */

            float sellTransactionFee = 2;
            float sellRate = 2;

            float XlmAmount = XLM * (Float.valueOf(mWithdrawAmount.getText().toString()));

            float Rate = (XlmAmount * sellRate) / 100;
            float updatedAmount = XlmAmount + Rate;
            float Fee = (updatedAmount * sellTransactionFee) / 100;
            float WalletFee = (Rate + Fee) * USD;
            float Total = XlmAmount + Rate + Fee;

            WithdrawTransRequest withdrawTransRequest = new WithdrawTransRequest();
            withdrawTransRequest.setUser(UserId(this));
            withdrawTransRequest.setFirstName(mWithdrawUserFirstName.getText().toString());
            withdrawTransRequest.setLastName(mWithdrawUserLastName.getText().toString());
            withdrawTransRequest.setSsn(mWithdrawSecurityNumber.getText().toString());
            withdrawTransRequest.setDay(mWithdrawDate.getText().toString());
            withdrawTransRequest.setMonth(mWithdrawMonth.getText().toString());
            withdrawTransRequest.setYear(mWithdrawYear.getText().toString());
            withdrawTransRequest.setCity(mWithdrawCity.getText().toString());
            withdrawTransRequest.setLine1(mWithdrawAddressLine1.getText().toString());
            withdrawTransRequest.setLine2(mWithdrawAddressLine2.getText().toString());
            withdrawTransRequest.setPostalCode(mWithdrawPostal.getText().toString());
            withdrawTransRequest.setState(mWithdrawState.getText().toString());
            withdrawTransRequest.setPhoneNumber(mWithdrawMobileNumber.getText().toString());
            withdrawTransRequest.setAccountHolder(mWithdrawAccountHolder.getText().toString());
            withdrawTransRequest.setAccountNumber(mWithdrawAccountNumber.getText().toString());
            withdrawTransRequest.setRoutingNumber(mWithdrawRoutingNumber.getText().toString());
            withdrawTransRequest.setSaveDetails(mCbWithdrawSaveCard.isChecked());
            withdrawTransRequest.setVerificationFile(url);

            withdrawTransRequest.setUsd(mWithdrawAmount.getText().toString());
            withdrawTransRequest.setXlm(String.valueOf(DecimalConvertion(Total)));
            withdrawTransRequest.setFee(String.valueOf(DecimalConvertion(Fee)));
            withdrawTransRequest.setRate(String.valueOf(DecimalConvertion(Rate)));
            withdrawTransRequest.setWalletFee(String.valueOf(DecimalConvertion(WalletFee)));

            final Call<WithdrawTransResponse> transactionResponseCall = apiInterfacePayment.withdrawAmount(withdrawTransRequest, UserId(this));
            transactionResponseCall.enqueue(new Callback<WithdrawTransResponse>() {
                @Override
                public void onResponse(Call<WithdrawTransResponse> call, Response<WithdrawTransResponse> response) {
                    removeProgressDialog();
                    if (response.isSuccessful()) {
                        mPopupDone(response.body());
                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<WithdrawTransResponse> call, Throwable t) {
                    removeProgressDialog();
                    myLog("OnFailure", t.toString());
                }
            });
        }
    }

    public void mPopupDone(WithdrawTransResponse withdrawTransResponse) {

        final Dialog mPopupDoneDialog = new Dialog(this);
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

        mPopupAmount.setText(DOLLAR_SYMBOL + withdrawTransResponse.getAmount());
        mPopupNumber.setText(withdrawTransResponse.getReceiver());
        mPopupSendAmount.setText(DOLLAR_SYMBOL + withdrawTransResponse.getAmount());
        mPopupTransactionFee.setText(DOLLAR_SYMBOL + withdrawTransResponse.getWalletFee());

        float total = Float.valueOf(withdrawTransResponse.getAmount()) + Float.valueOf(withdrawTransResponse.getWalletFee());
        mPopupTotalAmount.setText(DOLLAR_SYMBOL + String.valueOf(total));

        mPopupDateTime.setText(getResources().getString(R.string.withdrawn_on) + " "
                + PopUpConvertedDateTime(withdrawTransResponse.getCreatedTs()));

        mPopupDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupDoneDialog.dismiss();
                finish();
            }
        });

        mPopupDoneDialog.show();

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
                            GetXtoUService(Float.valueOf(response.body().get(0).getBalance()), 1);
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

}
