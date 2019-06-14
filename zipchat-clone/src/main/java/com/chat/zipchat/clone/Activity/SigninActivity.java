package com.chat.zipchat.clone.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chat.zipchat.clone.Common.BaseClass;
import com.chat.zipchat.clone.Model.Register.RegisterRequest;
import com.chat.zipchat.clone.Model.Register.RegisterResponse;
import com.chat.zipchat.clone.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rilixtech.CountryCodePicker;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.chat.zipchat.clone.Common.BaseClass.DeviceToken;
import static com.chat.zipchat.clone.Common.BaseClass.apiInterface;
import static com.chat.zipchat.clone.Common.BaseClass.isOnline;
import static com.chat.zipchat.clone.Common.BaseClass.myLog;
import static com.chat.zipchat.clone.Common.BaseClass.myToast;
import static com.chat.zipchat.clone.Common.BaseClass.removeProgressDialog;
import static com.chat.zipchat.clone.Common.BaseClass.sessionManager;
import static com.chat.zipchat.clone.Common.BaseClass.showSimpleProgressDialog;
import static com.chat.zipchat.clone.Common.BaseClass.snackbar;
import static com.chat.zipchat.clone.Common.MarshmallowPermission.PERMISSIONS;
import static com.chat.zipchat.clone.Common.MarshmallowPermission.PERMISSION_ALL;
import static com.chat.zipchat.clone.Common.MarshmallowPermission.hasPermissions;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener {

    CountryCodePicker mCountryPicker;
    EditText mPhoneNo, mEmail;
    Button mBtnNext, mBtnCancel, mBtnOk;
    Dialog mConfirmPhoneDialog;
    TextView mDialogNum, mDialogEmail;
    String id = "", newDisplayNumber = "";

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_signin);

        if (!hasPermissions(this)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        sessionManager(SigninActivity.this).checkLogin();

        mCountryPicker = findViewById(R.id.mCountryPicker);
        mPhoneNo = findViewById(R.id.mPhoneNo);
        mEmail = findViewById(R.id.mEmail);
        mBtnNext = findViewById(R.id.mBtnNext);
        mBtnNext.setOnClickListener(this);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                removeProgressDialog();

                myLog("FirebaseException", e.getMessage());

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    myToast(SigninActivity.this, "Invalid request");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    myToast(SigninActivity.this, "The SMS quota for the project has been exceeded");
                } else {
                    myToast(SigninActivity.this, "Invalid request");
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                String mVerificationId = verificationId;
                PhoneAuthProvider.ForceResendingToken mResendToken = token;

                mConfirmPhoneDialog.dismiss();
                removeProgressDialog();

                myToast(SigninActivity.this, getResources().getString(R.string.otp_send));

                Intent intent = new Intent(SigninActivity.this, OtpActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("mobile", newDisplayNumber);
                intent.putExtra("email", mEmail.getText().toString());
                intent.putExtra("mVerificationId", mVerificationId);
                intent.putExtra("mResendToken", mResendToken);
                startActivity(intent);
            }
        };

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.mBtnNext:
                if (validation()) {
                    ConfirmDialog();
                }

                break;

            case R.id.mBtnOk:
                if (validation()) {
                    RegisterService();
                }

                break;

            case R.id.mBtnCancel:
                mConfirmPhoneDialog.dismiss();
                break;
        }
    }

    private Boolean validation() {

        if (TextUtils.isEmpty(mPhoneNo.getText().toString())) {
            myToast(this, getResources().getString(R.string.enter_mobile_number));
            return false;
        } else if (mPhoneNo.getText().toString().length() < 6) {
            myToast(this, getResources().getString(R.string.enter_valid_number));
            return false;
        }/* else if (TextUtils.isEmpty(mEmail.getText().toString())) {
            myToast(this, getResources().getString(R.string.enter_your_email));
            return false;
        } else if (!eMailValidation(mEmail.getText().toString())) {
            myToast(this, getResources().getString(R.string.enter_valid_email));
            return false;
        }*/

        return true;
    }

    private void ConfirmDialog() {
        mConfirmPhoneDialog = new Dialog(this);
        mConfirmPhoneDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mConfirmPhoneDialog.setContentView(R.layout.dialog_phoneno_confirm);
        mDialogNum = mConfirmPhoneDialog.findViewById(R.id.mDialogNum);
        mDialogEmail = mConfirmPhoneDialog.findViewById(R.id.mDialogEmail);
        mBtnCancel = mConfirmPhoneDialog.findViewById(R.id.mBtnCancel);
        mBtnOk = mConfirmPhoneDialog.findViewById(R.id.mBtnOk);

        mDialogNum.setText(mCountryPicker.getSelectedCountryCodeWithPlus() + " " + mPhoneNo.getText().toString());
        mDialogEmail.setText(mEmail.getText().toString());

        mBtnCancel.setOnClickListener(this);
        mBtnOk.setOnClickListener(this);
        mConfirmPhoneDialog.show();
    }

    private void RegisterService() {
        if (isOnline(this)) {

            showSimpleProgressDialog(this);

            RegisterRequest userPojo = new RegisterRequest();
            userPojo.setDeviceToken(DeviceToken());
            userPojo.setDeviceType("ANDROID");
            userPojo.setCountryCode(mCountryPicker.getSelectedCountryCodeWithPlus());
            userPojo.setMobileNumber(mPhoneNo.getText().toString());
            userPojo.setEmail(mEmail.getText().toString());
            userPojo.setMobileWithCountryCode(mCountryPicker.getSelectedCountryCode() + mPhoneNo.getText().toString());

            final Call<RegisterResponse> registerResponseCall = apiInterface.createUser(userPojo);
            registerResponseCall.enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                    if (response.isSuccessful()) {
                        id = response.body().getResult().getId();
                        newDisplayNumber = mDialogNum.getText().toString().replaceAll("[^+0-9]", "");
//                        SendOtp(newDisplayNumber);

                        Intent intent = new Intent(SigninActivity.this, OtpActivity.class);
                        intent.putExtra("id", id);
                        intent.putExtra("mobile", newDisplayNumber);
                        intent.putExtra("email", mEmail.getText().toString());
                        intent.putExtra("otp", response.body().getResult().getOtp());

                        startActivity(intent);

                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    removeProgressDialog();
                    myLog("OnFailure", t.toString());
                }
            });
        } else {
            snackbar(this, mBtnNext, BaseClass.NO_INTERNET);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void SendOtp(String phoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

}
