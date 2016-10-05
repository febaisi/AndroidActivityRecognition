package com.febaisi.activityrecognition.services;

import android.os.Bundle;
import android.util.Log;

import com.febaisi.activityrecognition.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;


/**
 * Created by BaisFe01 on 10/5/2016.
 */

public class Connector implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    ActivityTrackerTrigger mActivityTrackerTrigger;

    public Connector (ActivityTrackerTrigger activityTrackerTrigger) {
        this.mActivityTrackerTrigger = activityTrackerTrigger;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(Utils.TAG, "Connector - onConnected");
        if (mActivityTrackerTrigger!=null) {
            mActivityTrackerTrigger.startMonitoringActivity();
        } else {
            Log.i(Utils.TAG, "Connector - onConnected. Ups! We couldn't start to monitoring user activity!");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(Utils.TAG, "Connector - onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(Utils.TAG, "Connector - onConnectionFailed");
    }
}
