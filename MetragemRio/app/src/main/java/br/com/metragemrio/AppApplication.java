package br.com.metragemrio;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

import java.util.TimeZone;

import io.fabric.sdk.android.Fabric;

public class AppApplication extends Application {

    public static AppApplication instance;
    private static GoogleAnalytics analytics;
    private static Tracker tracker;

    public AppApplication() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());

        TimeZone t = TimeZone.getTimeZone("GMT+0");
        TimeZone.setDefault(t);

        Parse.initialize(this, "BClDmah0fedSTqGmshb4UaCf5JZhkQ8LmTWuDow3", "lYeP9FZTNzmDdQW7RpMErubyuMLdGPZigQGVD9Kg");
        ParseInstallation.getCurrentInstallation().saveInBackground();

        SharedPreferences settings = getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean isFirstOpen = settings.getBoolean("isFirstOpen", true);
        if (isFirstOpen)
            ParsePush.subscribeInBackground("level_6");

        configureAnalytics();

    }


    private void configureAnalytics() {
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker(MainActivity.ANALYTICS_ID);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
    }

    public static Tracker getTracker() {
        return tracker;
    }
}
