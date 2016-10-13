package com.febaisi.activityrecognition;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.febaisi.activityrecognition.services.ActivityRecognitionService;
import com.febaisi.activityrecognition.services.RecordingListener;
import com.febaisi.activityrecognition.services.RecordingManager;
import com.febaisi.activityrecognition.util.SharedPreferenceUtil;

import java.text.SimpleDateFormat;


public class MainActivity extends AppCompatActivity implements ServiceConnection, View.OnClickListener, RecordingListener {

    private Context mContext;
    private Button mStopRecordButton;
    private RecordingManager mRecordingManager;
    private Intent mServiceIntent;
    private LinearLayout mStartsButtonsLayout;
    private TextView mCurrentTime;
    private TextView mTargetStateDescription;
    private TextView mRecordingStatusText;
    private boolean mIsRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();

        mServiceIntent = new Intent(mContext, ActivityRecognitionService.class);
        mContext.bindService(mServiceIntent, this, Context.BIND_AUTO_CREATE);

        mStopRecordButton = (Button) findViewById(R.id.activity_main_stop_record_button);
        mStopRecordButton.setOnClickListener(this);
        ((Button) findViewById(R.id.activity_main_in_vehicle)).setOnClickListener(this);
        ((Button) findViewById(R.id.activity_main_on_foot)).setOnClickListener(this);

        mTargetStateDescription = (TextView) findViewById(R.id.activity_main_target_state_text);
        mCurrentTime = (TextView) findViewById(R.id.activity_main_time);
        mRecordingStatusText = (TextView) findViewById(R.id.activity_main_recording_text);
        mStartsButtonsLayout = (LinearLayout) findViewById(R.id.activity_main_layout_starts);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mRecordingManager = ((ActivityRecognitionService.RecordingController) service).getRecordingListener();
        mRecordingManager.registerListener(this);
        mIsRecording = mRecordingManager.isRecording();
        enableButtons(mIsRecording);
        enableTicking(mIsRecording);
        enableButtons(mIsRecording);
        checkTargetState(mIsRecording);
    }

    private void checkTargetState(boolean mIsRecording) {
        if (!mIsRecording){
            SharedPreferenceUtil.saveStringPreference(this,
                    SharedPreferenceUtil.TARGET_STATE, getString(R.string.empty));
        }
        updateTargetDescription();
    }

    private void updateTargetDescription() {
        String targetMessage = SharedPreferenceUtil.getStringPreference(this,
                SharedPreferenceUtil.TARGET_STATE, getString(R.string.empty));
        if (targetMessage.isEmpty()) {
            mTargetStateDescription.setText(getString(R.string.empty));
        } else {
            mTargetStateDescription.setText(targetMessage);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mIsRecording = false;
        enableButtons(false);
    }

    private void enableButtons(boolean recordingState) {
        if (recordingState) {
            mStartsButtonsLayout.setVisibility(View.INVISIBLE);
            mRecordingStatusText.setVisibility(View.VISIBLE);
            mStopRecordButton.setVisibility(View.VISIBLE);
        } else {
            mRecordingStatusText.setVisibility(View.GONE);
            mStartsButtonsLayout.setVisibility(View.VISIBLE);
            mStopRecordButton.setVisibility(View.INVISIBLE);
            mCurrentTime.setText("00:00:00");
        }
    }

    private void enableTicking(boolean enable) {
        if (enable) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        final SimpleDateFormat databaseDateTimeFormate = new SimpleDateFormat("mm:ss:SS");
                        while (mIsRecording) {
                            Thread.sleep(10);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mCurrentTime.setText(databaseDateTimeFormate.format(mRecordingManager.getCurrentRecordingTime()));
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        Log.e(Utils.TAG, "Exception to ticking the clock!");
                    }
                }
            };
            t.start();
        }
    }

    private void stopRecording() {
        updateTargetDescription();
        mIsRecording = false;
        mRecordingManager.stopRecording();
        changeViewsReset(true);
        enableTicking(false);
    }

    private void changeViewsReset(boolean resetStatus) {
        if (resetStatus) {
            mRecordingStatusText.setText(getString(R.string.match));
            mStopRecordButton.setText(getString(R.string.reset));
        } else {
            mRecordingStatusText.setText(getString(R.string.recording));
            mStopRecordButton.setText(getString(R.string.stop_record));
            enableButtons(false);
        }
    }

    private void startRecording() {
        mIsRecording = true;
        mContext.startService(mServiceIntent);
        enableButtons(true);
        enableTicking(true);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.activity_main_stop_record_button:
                if (mIsRecording) {
                    stopRecording();
                } else {
                    changeViewsReset(false);
                }

                break;
            case R.id.activity_main_on_foot:
                SharedPreferenceUtil.saveStringPreference(this, SharedPreferenceUtil.TARGET_STATE,
                        getString(R.string.on_foot));
                updateTargetDescription();
                startRecording();
                break;
            case R.id.activity_main_in_vehicle:
                SharedPreferenceUtil.saveStringPreference(this, SharedPreferenceUtil.TARGET_STATE,
                        getString(R.string.in_vehicle));
                updateTargetDescription();
                startRecording();
                break;
        }
    }

    @Override
    public void onRecordStop() {
        Log.i(Utils.TAG, "MainActivity - onRecordStop");
        stopRecording();
    }

}
