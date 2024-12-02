package com.example.fitfeed.models.dto;

import java.util.ArrayList;
import java.util.UUID;

public class WorkoutDto {
    public int workoutId;
    public UUID userId;
    public String workoutName;
    public long workoutTimestamp;
    public ArrayList<ExerciseDto> exercises;
}
