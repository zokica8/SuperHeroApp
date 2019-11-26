package com.nsweb.heroapp.application;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class SuperHeroApplication extends Application {

    private static SuperHeroApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        if(instance == null) {
            instance = this;
        }
    }

    public static SuperHeroApplication getInstance() {
        return instance;
    }

    public static boolean hasNetwork() {
        return instance.isNetworkConnected();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
