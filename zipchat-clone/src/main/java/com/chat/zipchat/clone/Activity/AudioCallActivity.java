package com.chat.zipchat.clone.Activity;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chat.zipchat.clone.Common.App;
import com.chat.zipchat.clone.Model.ResultItem;
import com.chat.zipchat.clone.Model.ResultItemDao;
import com.chat.zipchat.clone.R;/*
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;*/

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.chat.zipchat.clone.Common.BaseClass.cancelNotification;
import static com.chat.zipchat.clone.Common.BaseClass.customNotification;
import static com.chat.zipchat.clone.Common.BaseClass.myToast;
import static com.chat.zipchat.clone.Common.BaseClass.playProgressTone;
import static com.chat.zipchat.clone.Common.BaseClass.stopProgressTone;

public class AudioCallActivity extends AppCompatActivity {
   /* private TextView tvCallState, tvCallDuration, tvCallerName;
    private Call call;
    private AppCompatImageView ivCallMute;
    private AppCompatImageView ivCallUnmute;
    private AppCompatImageView ivCallSpeakerOn;
    private AppCompatImageView ivCallSpeakerOff;
    static final String CALL_START_TIME = "callStartTime";
    private Timer mTimer;
    private long mCallStart = 0;
    private UpdateCallDurationTask mDurationTask;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private int field = 0x00000020;
    private Bundle instance;

    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            AudioCallActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCallDuration();
                }
            });
        }
    }

    //method to update live duration of the call
    private void updateCallDuration() {
        if (mCallStart > 0) {
            if (tvCallDuration != null)
                tvCallDuration.setText(formatTimespan(System.currentTimeMillis() - mCallStart));
        }
    }

    private String formatTimespan(long timespan) {
        long totalSeconds = timespan / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong(CALL_START_TIME, mCallStart);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCallStart = savedInstanceState.getLong(CALL_START_TIME);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = savedInstanceState;
        setContentView(R.layout.activity_audio_call);

        String strCallId = getIntent().getStringExtra("call_id");

        call = App.getmInstance().sinchClient.getCallClient().getCall(strCallId);
        call.addCallListener(new SinchCallListener());

        try {
            field = PowerManager.class.getClass().getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
        } catch (Throwable ignored) {
        }

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(field, getLocalClassName());

        if (!wakeLock.isHeld()) {
            wakeLock.acquire();
        }

        AudioController audioController = App.getmInstance().sinchClient.getAudioController();
        audioController.disableSpeaker();

        AppCompatImageView ivUser = findViewById(R.id.iv_user);
        tvCallerName = findViewById(R.id.tv_caller_name);
        tvCallDuration = findViewById(R.id.callDuration);
        tvCallState = findViewById(R.id.tv_call_state);
        AppCompatImageView ivCallEnd = findViewById(R.id.iv_call_end);
        ivCallMute = findViewById(R.id.iv_call_mute);
        ivCallUnmute = findViewById(R.id.iv_call_unmute);
        ivCallSpeakerOn = findViewById(R.id.iv_call_speaker_on);
        ivCallSpeakerOff = findViewById(R.id.iv_call_speaker_off);

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

        ivCallEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopProgressTone();
                call.hangup();
                call = null;

                if (wakeLock.isHeld())
                    wakeLock.release();

                if (mDurationTask != null)
                    mDurationTask.cancel();

                if (mTimer != null)
                    mTimer.cancel();

                mCallStart = 0;

                finish();
                myToast(AudioCallActivity.this, "Call ended.");
            }
        });

        customNotification(AudioCallActivity.this, "Calling..", tvCallerName.getText().toString(), call.getCallId());
    }

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallProgressing(Call call) {
            customNotification(AudioCallActivity.this, "Outgoing voice", tvCallerName.getText().toString(), call.getCallId());
            tvCallState.setText("Ringing..");
            playProgressTone(AudioCallActivity.this);

            ivCallMute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivCallMute.setVisibility(View.GONE);
                    ivCallUnmute.setVisibility(View.VISIBLE);
                    AudioController audioController = App.getmInstance().sinchClient.getAudioController();
                    audioController.mute();
                }
            });

            ivCallUnmute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivCallUnmute.setVisibility(View.GONE);
                    ivCallMute.setVisibility(View.VISIBLE);
                    AudioController audioController = App.getmInstance().sinchClient.getAudioController();
                    audioController.unmute();
                }
            });

            ivCallSpeakerOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivCallSpeakerOn.setVisibility(View.GONE);
                    ivCallSpeakerOff.setVisibility(View.VISIBLE);
                    AudioController audioController = App.getmInstance().sinchClient.getAudioController();
                    audioController.enableSpeaker();
                }
            });

            ivCallSpeakerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivCallSpeakerOff.setVisibility(View.GONE);
                    ivCallSpeakerOn.setVisibility(View.VISIBLE);
                    AudioController audioController = App.getmInstance().sinchClient.getAudioController();
                    audioController.disableSpeaker();
                }
            });
        }

        @Override
        public void onCallEstablished(Call call) {
            stopProgressTone();
            customNotification(AudioCallActivity.this, "Ongoing voice", tvCallerName.getText().toString(), call.getCallId());
            if (instance == null) {
                mCallStart = System.currentTimeMillis();
            }
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            tvCallState.setText("Call Connected");
            AudioController audioController = App.getmInstance().sinchClient.getAudioController();
            audioController.disableSpeaker();
            getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            mTimer = new Timer();
            mDurationTask = new UpdateCallDurationTask();
            mTimer.schedule(mDurationTask, 0, 500);

            ivCallMute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivCallMute.setVisibility(View.GONE);
                    ivCallUnmute.setVisibility(View.VISIBLE);
                    AudioController audioController = App.getmInstance().sinchClient.getAudioController();
                    audioController.mute();
                }
            });

            ivCallUnmute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivCallUnmute.setVisibility(View.GONE);
                    ivCallMute.setVisibility(View.VISIBLE);
                    AudioController audioController = App.getmInstance().sinchClient.getAudioController();
                    audioController.unmute();
                }
            });

            ivCallSpeakerOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivCallSpeakerOn.setVisibility(View.GONE);
                    ivCallSpeakerOff.setVisibility(View.VISIBLE);
                    AudioController audioController = App.getmInstance().sinchClient.getAudioController();
                    audioController.enableSpeaker();
                }
            });

            ivCallSpeakerOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivCallSpeakerOff.setVisibility(View.GONE);
                    ivCallSpeakerOn.setVisibility(View.VISIBLE);
                    AudioController audioController = App.getmInstance().sinchClient.getAudioController();
                    audioController.disableSpeaker();
                }
            });
        }

        @Override
        public void onCallEnded(Call endedcall) {
            stopProgressTone();
            call = null;
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            tvCallState.setText("Call Ended");

            if (wakeLock.isHeld())
                wakeLock.release();

            if (mDurationTask != null)
                mDurationTask.cancel();

            if (mTimer != null)
                mTimer.cancel();

            mCallStart = 0;

            finish();
            myToast(AudioCallActivity.this, "Call ended.");
            cancelNotification(AudioCallActivity.this);
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (wakeLock.isHeld())
            wakeLock.release();

        if (mDurationTask != null)
            mDurationTask.cancel();

        if (mTimer != null)
            mTimer.cancel();

        mCallStart = 0;
    }*/
}
