package com.example.fitfeed.models;

import com.example.fitfeed.models.dto.ExerciseDto;
import com.example.fitfeed.models.dto.WorkoutDto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class to model an individual workout
 */
public class Workout {
    private List<Exercise> exercises = new ArrayList<>();
    @SerializedName("workoutTimestamp")
    private long timestamp;
    private String workoutName;

    public Workout() {}

    public Workout(List<Exercise> exercises, long timestamp, String workoutName) {
        this.exercises = exercises;
        this.timestamp = timestamp;
        this.workoutName = workoutName;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setWorkoutName(String name) {
        this.workoutName = name;
    }

    public void addExercise(String name, int sets, int reps, float weight) {
        exercises.add(new Exercise(name, sets, reps, weight));
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(exercises, timestamp, workoutName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Workout)) return false;
        Workout workout = (Workout) o;
        return timestamp == workout.timestamp && Objects.equals(exercises, workout.exercises) && Objects.equals(workoutName, workout.workoutName);
    }

    /**
     * Class to model an individual exercise
     */
    public static class Exercise {
        @SerializedName("exerciseName")
        private String name;
        private int sets;
        private int reps;
        private float weight;

        public Exercise() {}

        public Exercise(String name, int sets, int reps, float weight) {
            this.name = name;
            this.sets = sets;
            this.reps = reps;
            this.weight = weight;
        }

        public String getName() {
            return name;
        }

        public int getSets() {
            return sets;
        }

        public int getReps() {
            return reps;
        }

        public float getWeight() {
            return weight;
        }

        public static Exercise fromDto(ExerciseDto dto) {
            return new Exercise(dto.exerciseName, dto.sets, dto.reps, dto.weight);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Exercise)) return false;
            Exercise exercise = (Exercise) o;
            return sets == exercise.sets && reps == exercise.reps && Float.compare(weight, exercise.weight) == 0 && Objects.equals(name, exercise.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, sets, reps, weight);
        }
    }

    public static Workout fromDto(WorkoutDto dto) {
        ArrayList<Exercise> exercises = new ArrayList<>();
        if (dto.exercises != null) {
            dto.exercises.forEach(exerciseDto -> {
                exercises.add(Exercise.fromDto(exerciseDto));
            });
        }
        return new Workout(exercises, dto.workoutTimestamp, dto.workoutName);
    }
}