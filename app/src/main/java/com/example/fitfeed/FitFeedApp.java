package com.example.fitfeed;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.fitfeed.activities.MainActivity;

public class FitFeedApp extends Application {
    private static FitFeedApp instance;

    public static FitFeedApp getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        clearPosts();   // todo comment out
        super.onCreate();
    }

    private void clearPosts() {
        // Clear shared prefs holding posts for dev purposes
        SharedPreferences preferences = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
