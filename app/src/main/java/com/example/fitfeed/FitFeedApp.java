package com.example.fitfeed;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.fitfeed.activities.MainActivity;
import com.example.fitfeed.util.CloudinaryHelper;
import com.example.fitfeed.util.TokenManager;

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
        Log.d("FitFeedApp.onCreate", "onCreate running");
        instance = this;
        clearPosts();   // todo comment out
        CloudinaryHelper.setMediaManager(this);
        TokenManager.init(this);
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
