package com.febaisi.activityrecognition.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.febaisi.activityrecognition.Utils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

/**
 * Created by BaisFe01 on 10/5/2016.
 */

public class ActivityRecognitionService extends Service implements ActivityTrackerTrigger {

    GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();

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
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Connect to the Google API client
        Log.i(Utils.TAG, "ActivityRecognitionService - onBind");
        return null;
    }

    @Override
    public void startMonitoringActivity() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Log.i(Utils.TAG, "ActivityRecognitionService - startActivityRecognition. requestActivityUpdates ");
            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, 500,
                    PendingIntent.getService(this, 0,new Intent(this, ActivityRecognitionIntentService.class), PendingIntent.FLAG_UPDATE_CURRENT));
        } else {
            Log.e(Utils.TAG, "ActivityRecognitionService - startActivityRecognition. Unable to start sampling due to disconnected API client");
        }
    }
}
