package com.chat.zipchat.clone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chat.zipchat.clone.Common.BaseClass;
import com.chat.zipchat.clone.Model.OtpVerify.OtpVerifyRequest;
import com.chat.zipchat.clone.Model.OtpVerify.OtpVerifyResponse;
import com.chat.zipchat.clone.Model.ResendOtp.ResendOtpRequest;
import com.chat.zipchat.clone.Model.ResendOtp.ResendOtpResponse;
import com.chat.zipchat.clone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.chat.zipchat.clone.Common.BaseClass.apiInterface;
import static com.chat.zipchat.clone.Common.BaseClass.isOnline;
import static com.chat.zipchat.clone.Common.BaseClass.myLog;
import static com.chat.zipchat.clone.Common.BaseClass.myToast;
import static com.chat.zipchat.clone.Common.BaseClass.removeProgressDialog;
import static com.chat.zipchat.clone.Common.BaseClass.showSimpleProgressDialog;
import static com.chat.zipchat.clone.Common.BaseClass.snackbar;

public class OtpActivity extends AppCompatActivity implements View.OnClickListener {

    TextView mTvPhone, mTvEmail, mtvResendOtp, mTvCounten;
    EditText mEtCodeF, mEtCodeS, mEtCodeT, mEtCodeFo, mEtCodeFi, mEtCodeSix;
    LinearLayout mLlResend, mLlCounten;

    String id, number, email;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        mTvPhone = findViewById(R.id.mTvPhone);
        mTvEmail = findViewById(R.id.mTvEmail);

        mEtCodeF = findViewById(R.id.mEtCodeF);
        mEtCodeS = findViewById(R.id.mEtCodeS);
        mEtCodeT = findViewById(R.id.mEtCodeT);
        mEtCodeFo = findViewById(R.id.mEtCodeFo);
        mEtCodeFi = findViewById(R.id.mEtCodeFi);
        mEtCodeSix = findViewById(R.id.mEtCodeSix);


        mtvResendOtp = findViewById(R.id.mtvResendOtp);
        mTvCounten = findViewById(R.id.mTvCounten);
        mLlResend = findViewById(R.id.mLlResend);
        mLlCounten = findViewById(R.id.mLlCounten);


        mTvPhone.setText(number);
        mTvEmail.setText(email);
        mtvResendOtp.setOnClickListener(this);
        counten();

        id = getIntent().getStringExtra("id");
        number = getIntent().getStringExtra("mobile");
        email = getIntent().getStringExtra("email");

        /*mVerificationId = getIntent().getStringExtra("mVerificationId");
        mResendToken = (PhoneAuthProvider.ForceResendingToken) getIntent().getExtras().get("mResendToken");*/

        mAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                removeProgressDialog();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    myToast(OtpActivity.this, "Invalid request");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    myToast(OtpActivity.this, "The SMS quota for the project has been exceeded");
                } else {
                    myToast(OtpActivity.this, "Invalid request");
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                removeProgressDialog();
                mVerificationId = verificationId;
                mResendToken = token;
                myToast(OtpActivity.this, getResources().getString(R.string.otp_resend));
            }
        };

        mEtCodeF.addTextChangedListener(new GenericTextWatcher(mEtCodeF));
        mEtCodeS.addTextChangedListener(new GenericTextWatcher(mEtCodeS));
        mEtCodeT.addTextChangedListener(new GenericTextWatcher(mEtCodeT));
        mEtCodeFo.addTextChangedListener(new GenericTextWatcher(mEtCodeFo));
        mEtCodeFi.addTextChangedListener(new GenericTextWatcher(mEtCodeFi));
        mEtCodeSix.addTextChangedListener(new GenericTextWatcher(mEtCodeSix));

        String otp = String.valueOf(getIntent().getIntExtra("otp", 0));
        mEtCodeF.setText(Character.toString(otp.charAt(0)));
        mEtCodeS.setText(Character.toString(otp.charAt(1)));
        mEtCodeT.setText(Character.toString(otp.charAt(2)));
        mEtCodeFo.setText(Character.toString(otp.charAt(3)));
        mEtCodeFi.setText(Character.toString(otp.charAt(4)));
        mEtCodeSix.setText(Character.toString(otp.charAt(5)));

    }

    private void counten() {
        CountDownTimer mCountDownTimer = new CountDownTimer(20000, 1000) {

            public void onTick(long millisUntilFinished) {
                mTvCounten.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                mLlCounten.setVisibility(View.GONE);
                mLlResend.setVisibility(View.VISIBLE);
            }
        };

        mCountDownTimer.start();
    }

    public class GenericTextWatcher implements TextWatcher {

        private View view;

        public GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {

            String text = editable.toString();
            int i = view.getId();
            if (i == R.id.mEtCodeF) {
                if (text.length() == 1) {
                    mEtCodeS.requestFocus();
                    return;
                } else if (text.length() == 0) {
                    mEtCodeF.requestFocus();
                    return;
                }

                if (text.length() == 1) {
                    mEtCodeT.requestFocus();
                    return;
                } else if (text.length() == 0) {
                    mEtCodeS.requestFocus();
                    return;
                }

                if (text.length() == 1) {
                    mEtCodeFo.requestFocus();
                    return;
                } else if (text.length() == 0) {
                    mEtCodeT.requestFocus();
                    return;
                }

                if (text.length() == 1) {
                    mEtCodeFi.requestFocus();
                    return;
                } else if (text.length() == 0) {
                    mEtCodeFo.requestFocus();
                    return;
                }

                if (text.length() == 1) {
                    mEtCodeSix.requestFocus();
                    return;
                } else if (text.length() == 0) {
                    mEtCodeFi.requestFocus();
                    return;
                }

                if (text.length() == 0) {
                    mEtCodeSix.requestFocus();
                } else if (text.length() == 1) {
                    String digit1 = mEtCodeF.getText().toString();
                    String digit2 = mEtCodeS.getText().toString();
                    String digit3 = mEtCodeT.getText().toString();
                    String digit4 = mEtCodeFo.getText().toString();
                    String digit5 = mEtCodeFi.getText().toString();
                    String digit6 = mEtCodeSix.getText().toString();
                    final String otp_Value = digit1 + digit2 + digit3 + digit4 + digit5 + digit6;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            OtpVerifyService(otp_Value);
                        }
                    }, 2000);
//                        verifyPhoneNumberWithCode(mVerificationId, otp_Value);
                }
            } else if (i == R.id.mEtCodeS) {
                if (text.length() == 1) {
                    mEtCodeT.requestFocus();
                    return;
                } else if (text.length() == 0) {
                    mEtCodeS.requestFocus();
                    return;
                }

                if (text.length() == 1) {
                    mEtCodeFo.requestFocus();
                    return;
                } else if (text.length() == 0) {
                    mEtCodeT.requestFocus();
                    return;
                }

                if (text.length() == 1) {
                    mEtCodeFi.requestFocus();
                    return;
                } else if (text.length() == 0) {
                    mEtCodeFo.requestFocus();
                    return;
                }

                if (text.length() == 1) {
                    mEtCodeSix.requestFocus();
                    return;
                } else if (text.length() == 0) {
                    mEtCodeFi.requestFocus();
                    return;
                }

                if (text.length() == 0) {
                    mEtCodeSix.requestFocus();
                } else if (text.length() == 1) {
                    String digit1 = mEtCodeF.getText().toString();
                    String digit2 = mEtCodeS.getText().toString();
                    String digit3 = mEtCodeT.getText().toString();
                    String digit4 = mEtCodeFo.getText().toString();
                    String digit5 = mEtCodeFi.getText().toString();
                    String digit6 = mEtCodeSix.getText().toString();
                    final String otp_Value = digit1 + digit2 + digit3 + digit4 + digit5 + digit6;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            OtpVerifyService(otp_Value);
                        }
                    }, 2000);
//                        verifyPhoneNumberWithCode(mVerificationId, otp_Value);
                }
            } else if (i == R.id.mEtCodeT) {
                if (text.length() == 1) {
                    mEtCodeFo.requestFocus();
                    return;
                } else if (text.length() == 0) {
                    mEtCodeT.requestFocus();
                    return;
                }

                if (text.length() == 1) {
                    mEtCodeFi.requestFocus();
                    return;
                } else if (text.length() == 0) {
                    mEtCodeFo.requestFocus();
                    return;
                }

                if (text.length() == 1) {
                    mEtCodeSix.requestFocus();
                    return;
                } else if (text.length() == 0) {
                    mEtCodeFi.requestFocus();
                    return;
                }

                if (text.length() == 0) {
                    mEtCodeSix.requestFocus();
                } else if (text.length() == 1) {
                    String digit1 = mEtCodeF.getText().toString();
                    String digit2 = mEtCodeS.getText().toString();
                    String digit3 = mEtCodeT.getText().toString();
                    String digit4 = mEtCodeFo.getText().toString();
                    String digit5 = mEtCodeFi.getText().toString();
                    String digit6 = mEtCodeSix.getText().toString();
                    final String otp_Value = digit1 + digit2 + digit3 + digit4 + digit5 + digit6;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            OtpVerifyService(otp_Value);
                        }
                    }, 2000);
//                        verifyPhoneNumberWithCode(mVerificationId, otp_Value);
                }
            } else if (i == R.id.mEtCodeFo) {
                if (text.length() == 1) {
                    mEtCodeFi.requestFocus();
                    return;
                } else if (text.length() == 0) {
                    mEtCodeFo.requestFocus();
                    return;
                }

                if (text.length() == 1) {
                    mEtCodeSix.requestFocus();
                    return;
                } else if (text.length() == 0) {
                    mEtCodeFi.requestFocus();
                    return;
                }

                if (text.length() == 0) {
                    mEtCodeSix.requestFocus();
                } else if (text.length() == 1) {
                    String digit1 = mEtCodeF.getText().toString();
                    String digit2 = mEtCodeS.getText().toString();
                    String digit3 = mEtCodeT.getText().toString();
                    String digit4 = mEtCodeFo.getText().toString();
                    String digit5 = mEtCodeFi.getText().toString();
                    String digit6 = mEtCodeSix.getText().toString();
                    final String otp_Value = digit1 + digit2 + digit3 + digit4 + digit5 + digit6;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            OtpVerifyService(otp_Value);
                        }
                    }, 2000);
//                        verifyPhoneNumberWithCode(mVerificationId, otp_Value);
                }
            } else if (i == R.id.mEtCodeFi) {
                if (text.length() == 1) {
                    mEtCodeSix.requestFocus();
                    return;
                } else if (text.length() == 0) {
                    mEtCodeFi.requestFocus();
                    return;
                }

                if (text.length() == 0) {
                    mEtCodeSix.requestFocus();
                } else if (text.length() == 1) {
                    String digit1 = mEtCodeF.getText().toString();
                    String digit2 = mEtCodeS.getText().toString();
                    String digit3 = mEtCodeT.getText().toString();
                    String digit4 = mEtCodeFo.getText().toString();
                    String digit5 = mEtCodeFi.getText().toString();
                    String digit6 = mEtCodeSix.getText().toString();
                    final String otp_Value = digit1 + digit2 + digit3 + digit4 + digit5 + digit6;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            OtpVerifyService(otp_Value);
                        }
                    }, 2000);
//                        verifyPhoneNumberWithCode(mVerificationId, otp_Value);
                }
            } else if (i == R.id.mEtCodeSix) {
                if (text.length() == 0) {
                    mEtCodeSix.requestFocus();
                } else if (text.length() == 1) {
                    String digit1 = mEtCodeF.getText().toString();
                    String digit2 = mEtCodeS.getText().toString();
                    String digit3 = mEtCodeT.getText().toString();
                    String digit4 = mEtCodeFo.getText().toString();
                    String digit5 = mEtCodeFi.getText().toString();
                    String digit6 = mEtCodeSix.getText().toString();
                    final String otp_Value = digit1 + digit2 + digit3 + digit4 + digit5 + digit6;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            OtpVerifyService(otp_Value);
                        }
                    }, 2000);
//                        verifyPhoneNumberWithCode(mVerificationId, otp_Value);
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mtvResendOtp) {
            mLlCounten.setVisibility(View.VISIBLE);
            mLlResend.setVisibility(View.GONE);
            counten();
            if (isOnline(this)) {
//                    resendVerificationCode(number, mResendToken);
                ResendOtpService();

            } else {
                snackbar(this, mLlCounten, BaseClass.NO_INTERNET);
            }
        }
    }

    private void OtpVerifyService(String otp) {
        if (isOnline(this)) {
            showSimpleProgressDialog(this);
            OtpVerifyRequest otpVerifyRequest = new OtpVerifyRequest();
            otpVerifyRequest.setUser(id);
            otpVerifyRequest.setOtp(otp);

            Call<OtpVerifyResponse> registerResponseCall = apiInterface.otpVerify(otpVerifyRequest);
            registerResponseCall.enqueue(new Callback<OtpVerifyResponse>() {
                @Override
                public void onResponse(Call<OtpVerifyResponse> call, Response<OtpVerifyResponse> response) {
                    removeProgressDialog();
                    if (response.isSuccessful()) {
                        myToast(OtpActivity.this, getResources().getString(R.string.otp_verified));
                        Intent intent = new Intent(OtpActivity.this, VerifyActivity.class);
                        intent.putExtra("id", response.body().getResult().getId());
                        startActivity(intent);
                        finish();

                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<OtpVerifyResponse> call, Throwable t) {
                    removeProgressDialog();
                    myLog("OnFailure", t.toString());
                }
            });
        } else {
            snackbar(this, mLlCounten, BaseClass.NO_INTERNET);
        }
    }

    private void ResendOtpService() {
        if (isOnline(this)) {
            showSimpleProgressDialog(this);

            ResendOtpRequest resendOtpRequest = new ResendOtpRequest();
            resendOtpRequest.setUser(id);
            resendOtpRequest.setMobile(number);

            Call<ResendOtpResponse> registerResponseCall = apiInterface.resendOtp(resendOtpRequest);
            registerResponseCall.enqueue(new Callback<ResendOtpResponse>() {
                @Override
                public void onResponse(Call<ResendOtpResponse> call, Response<ResendOtpResponse> response) {
                    removeProgressDialog();
                    if (response.isSuccessful()) {
                        myToast(OtpActivity.this, getResources().getString(R.string.otp_resend));
                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<ResendOtpResponse> call, Throwable t) {
                    removeProgressDialog();
                    myLog("OnFailure", t.toString());
                }
            });
        } else {
            snackbar(this, mLlCounten, BaseClass.NO_INTERNET);
        }
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {

        if (isOnline(this)) {
            showSimpleProgressDialog(this);
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithPhoneAuthCredential(credential);
        } else {
            snackbar(this, mLlCounten, BaseClass.NO_INTERNET);
        }


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            myToast(OtpActivity.this, getResources().getString(R.string.otp_verified));
                            Intent intent = new Intent(OtpActivity.this, VerifyActivity.class);
                            intent.putExtra("id", id);
                            startActivity(intent);
                            finish();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                myToast(OtpActivity.this, getResources().getString(R.string.invalid_otp));
                            }
                        }
                    }
                });
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {

        showSimpleProgressDialog(this);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

}
