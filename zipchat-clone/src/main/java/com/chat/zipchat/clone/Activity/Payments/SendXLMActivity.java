package com.chat.zipchat.clone.Activity.Payments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;

import com.chat.zipchat.clone.Adapter.Payment.MobileNumberAdapter;
import com.chat.zipchat.clone.Model.GetMobileNumbers.GetMobileNumberResponse;
import com.chat.zipchat.clone.R;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.chat.zipchat.clone.Common.BaseClass.apiInterfacePayment;
import static com.chat.zipchat.clone.Common.BaseClass.isOnline;
import static com.chat.zipchat.clone.Common.BaseClass.myLog;

public class SendXLMActivity extends AppCompatActivity {

    Toolbar mToolbarSendXlm;
    EditText mNumberSendXLM;
    RecyclerView mRvSendXLM;
    MobileNumberAdapter mobileNumberAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_xlm);

        mToolbarSendXlm = findViewById(R.id.mToolbarSendXlm);
        setSupportActionBar(mToolbarSendXlm);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.send_xlm));
        mToolbarSendXlm.setTitleTextColor(getResources().getColor(R.color.white));
        mToolbarSendXlm.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        mNumberSendXLM = findViewById(R.id.mNumberSendXLM);
        mRvSendXLM = findViewById(R.id.mRvSendXLM);
        mRvSendXLM.setLayoutManager(new LinearLayoutManager(this));

        mNumberSendXLM.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                GetMobileNumber(s.toString());
            }
        });

    }

    private void GetMobileNumber(String number) {
        if (isOnline(this)) {

            final Call<List<GetMobileNumberResponse>> transactionResponseCall = apiInterfacePayment.getMobileNumber(number);
            transactionResponseCall.enqueue(new Callback<List<GetMobileNumberResponse>>() {
                @Override
                public void onResponse(Call<List<GetMobileNumberResponse>> call, Response<List<GetMobileNumberResponse>> response) {
                    if (response.isSuccessful()) {
                        mRvSendXLM.removeAllViewsInLayout();
                        if (response.body().size() > 0) {
                            mobileNumberAdapter = new MobileNumberAdapter(SendXLMActivity.this, response.body());
                            mRvSendXLM.setAdapter(mobileNumberAdapter);
                        }
                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<List<GetMobileNumberResponse>> call, Throwable t) {
                    myLog("OnFailure", t.toString());
                }
            });
        }
    }

    public void mItemClick(HashMap<String, String> hashMap) {
        Intent intent = new Intent();
        intent.putExtra("userDetails", hashMap);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
        }

        return true;
    }

}
