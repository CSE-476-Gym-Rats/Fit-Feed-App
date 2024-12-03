package com.example.fitfeed;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.fitfeed.activities.MainActivity;
import com.example.fitfeed.models.Workout;
import com.example.fitfeed.models.dto.WorkoutDto;
import com.example.fitfeed.util.APIManager;
import com.example.fitfeed.util.CloudinaryHelper;
import com.example.fitfeed.util.FileManager;
import com.example.fitfeed.util.FitFeedAPI;
import com.example.fitfeed.util.GsonHelper;
import com.example.fitfeed.util.RetrofitService;
import com.example.fitfeed.util.TokenManager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class FitFeedApp extends Application {
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

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
        executorService.execute(this::loadWorkoutsFromServerToFile);
        super.onCreate();
    }

    private void clearPosts() {
        // Clear shared prefs holding posts for dev purposes
        SharedPreferences preferences = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    private void loadWorkoutsFromServerToFile() {
        String authHeader = "Bearer " + TokenManager.getAccessToken();
        RetrofitService retrofitService = new RetrofitService();
        FitFeedAPI api = retrofitService.getRetrofit().create(FitFeedAPI.class);
        try {
            Response<List<WorkoutDto>> workouts = api.getWorkouts(authHeader).execute();
            if (workouts.body() == null || workouts.body().isEmpty()) return;
            List<Workout> savedWorkouts = FileManager.loadWorkouts(getContext());
            workouts.body().forEach(w -> {
                Workout workout = Workout.fromDto(w);
                if (!savedWorkouts.contains(workout)) {
                    try {
                        FileManager.saveWorkout(getContext(), workout);
                    } catch (Exception e) {
                        Log.e("loadWorkoutsFromServerToFile", "Failed to save workout to file on app start", e);
                    }
                }
            });
        } catch (Exception e) {
            Log.e("loadWorkoutsFromServerToFile", "Failed to load workouts on app start", e);
        }
    }
}
