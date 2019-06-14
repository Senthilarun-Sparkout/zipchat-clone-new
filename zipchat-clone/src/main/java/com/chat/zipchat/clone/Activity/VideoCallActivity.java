package com.chat.zipchat.clone.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.chat.zipchat.clone.Common.BaseClass.cancelNotification;
import static com.chat.zipchat.clone.Common.BaseClass.customNotification;
import static com.chat.zipchat.clone.Common.BaseClass.myToast;
import static com.chat.zipchat.clone.Common.BaseClass.playProgressTone;
import static com.chat.zipchat.clone.Common.BaseClass.stopProgressTone;

public class VideoCallActivity extends AppCompatActivity {
    private String TAG = "Krish";
    private String callId;
    private Call call;
    private TextView tvCallDuration;
    private TextView tvCallState;
    private TextView tvCallerName;
    private boolean mVideoViewsAdded = false;
    private LinearLayout view;
    private RelativeLayout localView;
    static final String CALL_START_TIME = "callStartTime";
    private Timer mTimer;
    private long mCallStart = 0;
    private UpdateCallDurationTask mDurationTask;
    private AppCompatImageView ivCallMute, ivCallUnmute, ivCallCameraChange;
    private VideoController vc;
    private Bundle instance;

    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            VideoCallActivity.this.runOnUiThread(new Runnable() {
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
        setContentView(R.layout.activity_video_call);

        Intent intent = getIntent();
        callId = intent.getStringExtra("call_id");

        view = findViewById(R.id.remoteVideo);
        localView = findViewById(R.id.localVideo);
        tvCallDuration = findViewById(R.id.callDuration);
        tvCallerName = findViewById(R.id.remoteUser);
        tvCallState = findViewById(R.id.callState);
        CircleImageView ivUser = findViewById(R.id.iv_user);
        AppCompatImageView ivCallEnd = findViewById(R.id.iv_call_end);
        ivCallMute = findViewById(R.id.iv_call_mute);
        ivCallUnmute = findViewById(R.id.iv_call_unmute);
        ivCallCameraChange = findViewById(R.id.iv_call_camera_change);

        call = App.getmInstance().sinchClient.getCallClient().getCall(callId);
        call.addCallListener(new SinchCallListener());

        AudioController audioController = App.getmInstance().sinchClient.getAudioController();
        audioController.enableSpeaker();

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
                ivUser.setImageResource(R.drawable.defult_user);
            }
        } else {
            tvCallerName.setText(call.getRemoteUserId());
            ivUser.setImageResource(R.drawable.defult_user);
        }

        ivCallEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    stopProgressTone();
                    view.removeView(vc.getLocalView());
                    VideoCallActivity.this.removeVideoViews();
                    call.hangup();
                    call = null;

                    if (mDurationTask != null)
                        mDurationTask.cancel();

                    if (mTimer != null)
                        mTimer.cancel();

                    mCallStart = 0;
                    VideoCallActivity.this.finish();
                    myToast(VideoCallActivity.this, "Call ended.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        vc = App.getmInstance().sinchClient.getVideoController();
        view.addView(vc.getLocalView());

        customNotification(VideoCallActivity.this, "Calling..", tvCallerName.getText().toString(), call.getCallId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDurationTask != null)
            mDurationTask.cancel();

        if (mTimer != null)
            mTimer.cancel();

        mCallStart = 0;
        removeVideoViews();
    }

    private class SinchCallListener implements VideoCallListener {
        @Override
        public void onCallProgressing(Call call) {
            customNotification(VideoCallActivity.this, "Outgoing video", tvCallerName.getText().toString(), call.getCallId());
            tvCallState.setText("Ringing..");
            playProgressTone(VideoCallActivity.this);
            view.removeView(vc.getLocalView());
            view.addView(vc.getLocalView());
        }

        @Override
        public void onCallEstablished(Call call) {
            stopProgressTone();
            customNotification(VideoCallActivity.this, "Ongoing Video call", tvCallerName.getText().toString(), call.getCallId());
            if (instance == null) {
                mCallStart = System.currentTimeMillis();
            }
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            tvCallState.setText("Call Connected");
            AudioController audioController = App.getmInstance().sinchClient.getAudioController();
            audioController.enableSpeaker();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            mTimer = new Timer();
            mDurationTask = new UpdateCallDurationTask();
            mTimer.schedule(mDurationTask, 0, 500);
        }

        @Override
        public void onCallEnded(Call endedcall) {
            stopProgressTone();
            view.removeView(vc.getLocalView());
            removeVideoViews();
            call = null;
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            tvCallState.setText("Call Ended");

            if (mDurationTask != null)
                mDurationTask.cancel();

            if (mTimer != null)
                mTimer.cancel();

            mCallStart = 0;

            finish();
            myToast(VideoCallActivity.this, "Call ended.");
            cancelNotification(VideoCallActivity.this);
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }

        @Override
        public void onVideoTrackAdded(Call call) {
            if (mVideoViewsAdded || App.getmInstance().sinchClient == null) {
                return; //early
            }

            if (vc != null) {
                view.removeView(vc.getLocalView());
                localView.addView(vc.getLocalView());

                ivCallCameraChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //this toggles the front camera to rear camera and vice versa
                        vc.toggleCaptureDevicePosition();
                    }
                });

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

                view.addView(vc.getRemoteView());
                mVideoViewsAdded = true;
            }
        }

        @Override
        public void onVideoTrackPaused(Call call) {

        }

        @Override
        public void onVideoTrackResumed(Call call) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void removeVideoViews() {
        if (App.getmInstance().sinchClient == null) {
            return; // early
        }

        if (vc != null) {
            view.removeView(vc.getRemoteView());

            localView.removeView(vc.getLocalView());
            mVideoViewsAdded = false;
        }
    }
}
