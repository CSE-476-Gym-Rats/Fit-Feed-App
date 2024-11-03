package com.example.fitfeed.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to model an individual workout
 */
public class Workout {
    private List<Exercise> exercises = new ArrayList<>();
    private long timestamp;
    private String workoutName;

    public Workout() {}

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

    /**
     * Class to model an individual exercise
     */
    public static class Exercise {
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
    }
}