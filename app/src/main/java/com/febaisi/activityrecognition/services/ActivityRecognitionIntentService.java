package com.febaisi.activityrecognition.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.febaisi.activityrecognition.Utils;
import com.febaisi.activityrecognition.util.ActivityUtil;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

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
        Log.i(Utils.TAG, "ActivityRecognitionIntentServcice - onHandleIntent");

        if (ActivityRecognitionResult.hasResult(intent)) {
            String message = "";
            Log.i(Utils.TAG, "ActivityRecognitionIntentService - onHandleIntent. HasResult");
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            for (DetectedActivity detectedActivity:result.getProbableActivities()) {
                message += new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) + " || " +
                        String.format("%s: %d", ActivityUtil.getActivityTypeString(detectedActivity.getType()), detectedActivity.getConfidence()) + "\n";
                Log.i(Utils.TAG, "ActivityRecognitionIntentService - onHandleIntent. " + message);
            }

            Utils.writeToSDFile(getApplicationContext(), message, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        }
    }
}
