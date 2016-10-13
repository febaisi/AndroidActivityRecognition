package com.febaisi.activityrecognition.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.febaisi.activityrecognition.Utils;
import com.febaisi.activityrecognition.tasks.ActivityRecognitionIntentService;
import com.febaisi.activityrecognition.util.SharedPreferenceUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

/**
 * Created by BaisFe01 on 10/5/2016.
 */

public class ActivityRecognitionService extends Service implements ActivityTrackerTrigger, RecordingManager,
        SharedPreferences.OnSharedPreferenceChangeListener {

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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SharedPreferenceUtil.MATCH_TARGET)) {
            if (SharedPreferenceUtil.getBooleanPreference(this, key, false)) {
                stopRecording();
            }
        }
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
            getSharedPreferences(this.getPackageName(), Context.MODE_PRIVATE).registerOnSharedPreferenceChangeListener(this);
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
        unregisterActivityRecognition();
        mRecordingState = false;
        stopSelf();
        try {
            finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        SharedPreferenceUtil.saveBooleanPreference(this, SharedPreferenceUtil.MATCH_TARGET, false);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(Utils.TAG, "ActivityRecognitionService - onTaskRemoved");
        unregisterActivityRecognition();
        super.onTaskRemoved(rootIntent);

    }

    private void unregisterActivityRecognition() {
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient,
                PendingIntent.getService(this, 0,new Intent(this, ActivityRecognitionIntentService.class), PendingIntent.FLAG_UPDATE_CURRENT));
    }

    @Override
    public void onDestroy() {
        Log.i(Utils.TAG, "ActivityRecognitionService - onDestroy");
    }
}