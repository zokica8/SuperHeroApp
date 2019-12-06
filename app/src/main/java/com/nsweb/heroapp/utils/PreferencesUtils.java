package com.nsweb.heroapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;


public class PreferencesUtils {

    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setCount(Context context, int count) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        sharedPreferences.edit().putInt("counter", count).apply();
    }

    public static int getCount(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getInt("counter", 0);
    }
}
