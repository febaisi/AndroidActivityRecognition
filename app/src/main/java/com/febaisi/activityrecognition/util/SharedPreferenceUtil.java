package com.febaisi.activityrecognition.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by BaisFe01 on 10/12/2016.
 */

public class SharedPreferenceUtil {

    public static String TARGET_STATE = "TARGET_STATE";
    public static String START_TIME = "START_TIME";

    public static void saveStringPreference(Context context, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        prefs.edit().putString(key, value).apply();
    }

    public static void saveLongPreference(Context context, String key, long value) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        prefs.edit().putLong(key, value).apply();
    }

    public static String getStringPreference(Context context, String key, String defValue) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getString(key, defValue);
    }

    public static long getLongPreference(Context context, String key, long defValue) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getLong(key, defValue);
    }


}
