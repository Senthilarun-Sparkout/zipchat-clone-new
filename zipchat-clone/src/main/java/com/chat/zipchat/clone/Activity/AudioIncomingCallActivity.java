package com.chat.zipchat.clone.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chat.zipchat.clone.Common.App;
import com.chat.zipchat.clone.Model.ResultItem;
import com.chat.zipchat.clone.Model.ResultItemDao;
import com.chat.zipchat.clone.R;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

import static com.chat.zipchat.clone.Common.BaseClass.customNotification;
import static com.chat.zipchat.clone.Common.BaseClass.myToast;

public class AudioIncomingCallActivity extends AppCompatActivity {
    private MediaPlayer player;
    private TextView tvCallState;
    private TextView tvCallerName;
    private Call call;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_incoming_call);

        String strCallId = getIntent().getStringExtra("call_id");

        call = App.getmInstance().sinchClient.getCallClient().getCall(strCallId);
        call.addCallListener(new SinchCallListener());

        AudioController audioController = App.getmInstance().sinchClient.getAudioController();
        audioController.enableSpeaker();
        player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        player.setLooping(true);
        player.start();

        AppCompatImageView ivUser = findViewById(R.id.iv_user);
        tvCallerName = findViewById(R.id.tv_caller_name);
        tvCallState = findViewById(R.id.tv_call_state);
        AppCompatImageView ivCallReject = findViewById(R.id.iv_call_end);
        AppCompatImageView ivCallAccept = findViewById(R.id.iv_call_accept);

        tvCallState.setText("Incoming Call..");

        List<ResultItem> resultItems = App.getmInstance().resultItemDao.queryBuilder().where(
                ResultItemDao.Properties.Id.eq(call.getRemoteUserId())).list();

        if (resultItems.size() > 0) {

            if (null != resultItems.get(0).getName()) {
                tvCallerName.setText(resultItems.get(0).getName());
            } else {
                tvCallerName.setText(call.getRemoteUserId());
            }

            if (null != resultItems.get(0).getProfile_picture()) {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.splash);
                requestOptions.error(R.drawable.splash);
                Glide.with(this)
                        .setDefaultRequestOptions(requestOptions)
                        .load(resultItems.get(0).getProfile_picture())
                        .into(ivUser);
            } else {
                ivUser.setImageResource(R.drawable.splash);
            }
        } else {
            tvCallerName.setText(call.getRemoteUserId());
            ivUser.setImageResource(R.drawable.splash);
        }

        ivCallReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call.hangup();
                player.stop();
                AudioIncomingCallActivity.this.getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                call = null;

                AudioIncomingCallActivity.this.finish();
            }
        });

        ivCallAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call.answer();
                player.stop();
                AudioIncomingCallActivity.this.startActivity(new Intent(AudioIncomingCallActivity.this, AudioCallActivity.class)
                        .putExtra("call_id", call.getCallId()));
                AudioIncomingCallActivity.this.finish();
            }
        });

        customNotification(AudioIncomingCallActivity.this, "Incoming voice call..", tvCallerName.getText().toString(), call.getCallId());
    }

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallProgressing(Call call) {

        }

        @Override
        public void onCallEstablished(Call call) {

        }

        @Override
        public void onCallEnded(Call endedcall) {
            if (endedcall.getDetails().getDuration() == 0)
                customNotification(AudioIncomingCallActivity.this, "Missed Voice call from ", tvCallerName.getText().toString(), endedcall.getCallId());

            call = null;
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            tvCallState.setText("Disconnected");
            if (player != null)
                player.stop();

            finish();
            myToast(AudioIncomingCallActivity.this, "Call ended.");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null)
            player.stop();
    }
}
