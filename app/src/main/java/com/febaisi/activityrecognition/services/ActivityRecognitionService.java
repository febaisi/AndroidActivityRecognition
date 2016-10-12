package com.febaisi.activityrecognition.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.febaisi.activityrecognition.Utils;
import com.febaisi.activityrecognition.tasks.ActivityRecognitionIntentService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

/**
 * Created by BaisFe01 on 10/5/2016.
 */

public class ActivityRecognitionService extends Service implements ActivityTrackerTrigger, RecordingManager {

    private GoogleApiClient mGoogleApiClient;
    private Boolean mRecordingState = false;
    private RecordingController mRecordingController = new RecordingController();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(Utils.TAG, "ActivityRecognitionService - onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(Utils.TAG, "ActivityRecognitionService - onStartCommand");

        Connector connector = new Connector(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(connector)
                .addOnConnectionFailedListener(connector)
                .build();

        Log.i(Utils.TAG, "ActivityRecognitionService - onCreate");

        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            Log.i(Utils.TAG, "ActivityRecognitionService - onCreate. Requesting connections");
            mGoogleApiClient.connect();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public class RecordingController extends Binder {
        public RecordingManager getRecordingListener() {
            return ActivityRecognitionService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Connect to the Google API client
        Log.i(Utils.TAG, "ActivityRecognitionService - onBind");
        return mRecordingController;
    }

    @Override
    public void startMonitoringActivity() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mRecordingState = true;
            Log.i(Utils.TAG, "ActivityRecognitionService - startActivityRecognition. requestActivityUpdates ");
            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, 500,
                    PendingIntent.getService(this, 0,new Intent(this, ActivityRecognitionIntentService.class), PendingIntent.FLAG_UPDATE_CURRENT));
        } else {
            Log.e(Utils.TAG, "ActivityRecognitionService - startActivityRecognition. Unable to start sampling due to disconnected API client");
        }
    }

    @Override
    public boolean isRecording() {
        return mRecordingState;
    }

    @Override
    public void stopRecording() {
        Log.i(Utils.TAG, "ActivityRecognitionService - stopRecording");
        unregisterActivitiryRecognition();
        mRecordingState = false;
        stopSelf();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(Utils.TAG, "ActivityRecognitionService - onTaskRemoved");
        unregisterActivitiryRecognition();
        super.onTaskRemoved(rootIntent);

    }

    private void unregisterActivitiryRecognition() {
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient,
                PendingIntent.getService(this, 0,new Intent(this, ActivityRecognitionIntentService.class), PendingIntent.FLAG_UPDATE_CURRENT));
    }

    @Override
    public void onDestroy() {
        Log.i(Utils.TAG, "ActivityRecognitionService - onDestroy");
    }
}