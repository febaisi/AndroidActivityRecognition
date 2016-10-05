package com.febaisi.activityrecognition.util;

import com.google.android.gms.location.DetectedActivity;

/**
 * Created by BaisFe01 on 10/5/2016.
 */

public class ActivityUtil {

    public static String getActivityTypeString(int type) {

        switch (type) {
            case DetectedActivity.IN_VEHICLE:
                return "IN_VEHICLE";
            case DetectedActivity.ON_BICYCLE:
                return "ON_BICYCLE";
            case DetectedActivity.ON_FOOT:
                return "ON_FOOT";
            case DetectedActivity.STILL:
                return "STILL";
            case DetectedActivity.UNKNOWN:
                return "UNKNOWN";
            case DetectedActivity.TILTING:
                return "TILTING";
            case DetectedActivity.WALKING:
                return "WALKING";
            case DetectedActivity.RUNNING:
                return "RUNNING";
            default:
                return "";
        }
    }
}
