package com.febaisi.activityrecognition.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.febaisi.activityrecognition.Utils;
import com.febaisi.activityrecognition.util.ActivityUtil;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.Locale;

/**
 * Created by BaisFe01 on 10/5/2016.
 */

public class ActivityRecognitionIntentService extends IntentService {

    public ActivityRecognitionIntentService() {
        super("ActivityRecognitionIntentService");
        Log.i(Utils.TAG, "ActivityRecognitionIntentService - constructor");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(Utils.TAG, "ActivityRecognitionIntentService - onHandleIntent");

        if (ActivityRecognitionResult.hasResult(intent)) {
            Log.i(Utils.TAG, "ActivityRecognitionIntentService - onHandleIntent. HasResult");

            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            for (DetectedActivity detectedActivity:result.getProbableActivities()) {
                String message = String.format("%s: %d", ActivityUtil.getActivityTypeString(detectedActivity.getType()), detectedActivity.getConfidence());
                Log.i(Utils.TAG, "ActivityRecognitionIntentService - onHandleIntent. " + message);
            }
        }
    }
}