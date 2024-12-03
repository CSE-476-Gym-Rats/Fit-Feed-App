package com.example.fitfeed.models.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WorkoutDto {
    public long workoutId;
    public UUID userId;
    public String workoutName;
    public long workoutTimestamp;
    public List<ExerciseDto> exercises;
}
