package com.nsweb.heroapp.application;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.nsweb.heroapp.ui.activities.MainActivity;

import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.configuration.Configuration;

public class SuperHeroApplication extends Application {

    private static SuperHeroApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        if(instance == null) {
            instance = this;
        }

        createApplicationScope();
    }

    private void createApplicationScope() {
        Toothpick.setConfiguration(Configuration.forDevelopment());
        Scope appScope = Toothpick.openScope(this);
        Toothpick.inject(this, appScope);
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
