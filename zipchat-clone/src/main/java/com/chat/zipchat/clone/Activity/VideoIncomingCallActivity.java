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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chat.zipchat.clone.Common.App;
import com.chat.zipchat.clone.Model.ResultItem;
import com.chat.zipchat.clone.Model.ResultItemDao;
import com.chat.zipchat.clone.R;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import java.util.List;

import static com.chat.zipchat.clone.Common.BaseClass.customNotification;
import static com.chat.zipchat.clone.Common.BaseClass.myToast;

public class VideoIncomingCallActivity extends AppCompatActivity {
    private String callId;
    private Call call;
    private MediaPlayer player;
    private TextView tvCallDuration;
    private TextView tvCallState;
    private TextView tvCallerName;
    private VideoController vc;
    private LinearLayout localView;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_incoming_call);

        Intent intent = getIntent();
        callId = intent.getStringExtra("call_id");

        call = App.getmInstance().sinchClient.getCallClient().getCall(callId);
        call.addCallListener(new SinchCallListener());

        AudioController audioController = App.getmInstance().sinchClient.getAudioController();
        audioController.enableSpeaker();
        player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        player.setLooping(true);
        player.start();

        localView = findViewById(R.id.localVideo);
        tvCallDuration = findViewById(R.id.callDuration);
        tvCallerName = findViewById(R.id.remoteUser);
        tvCallState = findViewById(R.id.callState);
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
        } else {
            tvCallerName.setText(call.getRemoteUserId());
        }

        ivCallReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call.hangup();
                call = null;
                player.stop();
                VideoIncomingCallActivity.this.removeVideoViews();
                VideoIncomingCallActivity.this.getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                VideoIncomingCallActivity.this.finish();
            }
        });

        ivCallAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call.answer();
                player.stop();
                VideoIncomingCallActivity.this.removeVideoViews();
                VideoIncomingCallActivity.this.startActivity(new Intent(VideoIncomingCallActivity.this, VideoCallActivity.class)
                        .putExtra("call_id", call.getCallId()));
                VideoIncomingCallActivity.this.finish();
            }
        });

        vc = App.getmInstance().sinchClient.getVideoController();
        localView.addView(vc.getLocalView());

        customNotification(VideoIncomingCallActivity.this, "Incoming video call..", tvCallerName.getText().toString(), call.getCallId());
    }

    private class SinchCallListener implements VideoCallListener {
        @Override
        public void onCallProgressing(Call call) {

        }

        @Override
        public void onCallEstablished(Call call) {

        }

        @Override
        public void onCallEnded(Call endedcall) {
            if (endedcall.getDetails().getDuration() == 0)
                customNotification(VideoIncomingCallActivity.this, "Missed Video call from ", tvCallerName.getText().toString(), endedcall.getCallId());
            call = null;
            player.stop();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            tvCallState.setText("Call Ended");

            removeVideoViews();

            finish();
            myToast(VideoIncomingCallActivity.this, "Call ended.");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }

        @Override
        public void onVideoTrackAdded(Call call) {

        }

        @Override
        public void onVideoTrackPaused(Call call) {

        }

        @Override
        public void onVideoTrackResumed(Call call) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null)
            player.stop();
        removeVideoViews();
    }

    private void removeVideoViews() {
        if (App.getmInstance().sinchClient == null) {
            return; // early
        }

        if (vc != null) {
            localView.removeView(vc.getLocalView());
        }
    }
}
