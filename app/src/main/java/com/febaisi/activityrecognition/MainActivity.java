package com.febaisi.activityrecognition;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.febaisi.activityrecognition.services.ActivityRecognitionService;
import com.febaisi.activityrecognition.services.RecordingManager;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    private Context mContext;
    private Button mRecordingButton;
    private RecordingManager mRecordingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();

        final Intent intent = new Intent(mContext, ActivityRecognitionService.class);
        mContext.bindService(intent, this, Context.BIND_AUTO_CREATE);

        mRecordingButton = (Button) findViewById(R.id.activity_main_record_button);
        mRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecordingManager.isConnected()) {
                    mRecordingManager.stopRecording();
                    setRecordingButtonText(false);
                } else {
                    mContext.startService(intent);
                    setRecordingButtonText(true);
                }
            }
        });
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mRecordingManager = ((ActivityRecognitionService.RecordingController) service).getRecordingListener();
        setRecordingButtonText(mRecordingManager.isConnected());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        setRecordingButtonText(false);
    }

    private void setRecordingButtonText(boolean recordingState) {
        String message;
        if (recordingState) {
            message = mContext.getString(R.string.stop_record);
        } else {
            message = mContext.getString(R.string.start_record);
        }
        mRecordingButton.setText(message);
    }
}
