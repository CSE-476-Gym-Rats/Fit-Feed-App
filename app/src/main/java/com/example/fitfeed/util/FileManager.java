package com.example.fitfeed.util;


import android.content.Context;
import android.util.Log;

import com.example.fitfeed.models.Workout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Util for writing/reading files
 */
public class FileManager {
    static final String WORKOUTS_FILE_NAME = "workouts.json";

    private FileManager() {}

    /**
     * Add a workout to the workouts.json file
     * @throws IOException
     */
    public static void saveWorkout(Context context, Workout workout) throws Exception {
        // Load existing workouts
        List<Workout> workouts = loadWorkouts(context);

        // Add the new workout to the list
        workouts.add(workout);

        // Convert the updated list to JSON
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(workouts);

        // Write the updated JSON back to the file
        write(context, WORKOUTS_FILE_NAME, json);
    }

    /**
     * Load saved workouts
     * @param context
     * @return List of Workout objects
     * @throws Exception
     */
    public static List<Workout> loadWorkouts(Context context) throws Exception {
        Gson gson = new GsonBuilder().create();
        List<Workout> workouts;

        try (FileInputStream fis = context.openFileInput(WORKOUTS_FILE_NAME);
             Reader reader = new InputStreamReader(fis)) {

            // Declare workout list type for Gson builder
            Type workoutListType = new TypeToken<ArrayList<Workout>>() {}.getType();
            workouts = gson.fromJson(reader, workoutListType);

            if (workouts == null) {
                workouts = new ArrayList<>();
            }

        } catch (Exception e) {
            // If the file doesn't exist, return an empty list
            workouts = new ArrayList<>();
        }

        return workouts;
    }

    /**
     * Write json data to a file
     * @param context
     * @param filename
     * @param json
     * @throws IOException
     */
    private static void write(Context context, String filename, String json) throws Exception {
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(json.getBytes());
        } catch (Exception e) {
            Log.e("FileManager", e.toString());
            throw e;
        }
    }
}
