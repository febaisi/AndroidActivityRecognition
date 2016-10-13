package com.febaisi.activityrecognition.tasks;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.febaisi.activityrecognition.Utils;
import com.febaisi.activityrecognition.util.ActivityUtil;
import com.febaisi.activityrecognition.util.SharedPreferenceUtil;
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

            String target = SharedPreferenceUtil.getStringPreference(this, SharedPreferenceUtil.TARGET_STATE, "");

            for (DetectedActivity detectedActivity:result.getProbableActivities()) {
                message += new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) + " || " +
                        String.format("%s: %d", ActivityUtil.getActivityTypeString(detectedActivity.getType()), detectedActivity.getConfidence()) + "\n";

                if (target.equals(ActivityUtil.getActivityTypeString(detectedActivity.getType())) && detectedActivity.getConfidence() > 60) {
                    SharedPreferenceUtil.saveBooleanPreference(this, SharedPreferenceUtil.MATCH_TARGET, true);
                }

            }
            Log.i(Utils.TAG, message);
            Utils.writeToSDFile(getApplicationContext(), message, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        }
    }
}
