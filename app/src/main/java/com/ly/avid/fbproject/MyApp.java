package com.ly.avid.fbproject;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.hola.sdk.HolaAnalysis;

/**
 * Created by Holaverse on 2017/1/22.
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        HolaAnalysis.init(getApplicationContext(), "999999", "999999");

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}
