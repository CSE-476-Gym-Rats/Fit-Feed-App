package com.example.fitfeed.util;

import com.example.fitfeed.models.dto.WorkoutDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface FitFeedAPI {
    @GET("/workouts")
    Call<List<WorkoutDto>> getWorkouts(@Header("Authorization") String auth);
}
