package com.example.fitfeed.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitfeed.R;
import com.example.fitfeed.R.string;
import com.example.fitfeed.models.Workout;
import com.example.fitfeed.util.APIManager;
import com.example.fitfeed.util.FileManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NewWorkoutActivity extends AppCompatActivity {

    private Button addExerciseButton;
    private FloatingActionButton cancelButton;
    private Button deleteExerciseButton;
    private FloatingActionButton saveButton;
    private LinearLayout exerciseRows;
    private Workout workout = new Workout();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_workout);

        // Listener for addExerciseButton
        exerciseRows = findViewById(R.id.linearLayoutContainer);
        addExerciseButton = findViewById(R.id.addExerciseButton);
        addExerciseButton.setOnClickListener(v -> addNewExerciseRow());
        deleteExerciseButton = findViewById(R.id.deleteExerciseButton); // Initialize delete button
        deleteExerciseButton.setOnClickListener(v -> deleteLastExerciseRow()); // Set delete button listener

        // Listener for cancel
        cancelButton = this.findViewById(R.id.cancelNewWorkout);
        cancelButton.setOnClickListener(v -> finish());

        // Listener for save
        saveButton = this.findViewById(R.id.saveNewWorkout);
        saveButton.setOnClickListener(v -> saveWorkout());
    }

    /**
     * Insert a new exercise row
     * Called when "Add Exercise" is pressed
     */
    private void addNewExerciseRow() {
        if (!validateExerciseFields()) {
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View newRow = inflater.inflate(R.layout.exercise_row, null);

        // Insert the new row above the "Add Exercise" button
        int index = exerciseRows.indexOfChild(addExerciseButton);
        exerciseRows.addView(newRow, index);

        // Update the visibility of the delete button
        updateDeleteButtonVisibility();
    }

    /**
     * Deletes the most recent exercise row when "Delete Last Exercise" is pressed.
     */
    private void deleteLastExerciseRow() {
        int childCount = exerciseRows.getChildCount();

        // Ensure there is more than one exercise row to delete
        if (childCount > 3) { // More than one exercise row plus the two buttons
            // Get the last exercise row before the "Add Exercise" button
            View lastRow = exerciseRows.getChildAt(childCount - 3); // The last actual exercise row
            exerciseRows.removeView(lastRow);
        } else {
            Toast.makeText(this, "At least one exercise row must remain.", Toast.LENGTH_SHORT).show();
        }

        // Update the visibility of the delete button
        updateDeleteButtonVisibility();
    }

    /**
     * Updates the visibility of the "Delete Last Exercise" button based on the number of exercise rows.
     */
    private void updateDeleteButtonVisibility() {
        // If there's more than one exercise row, show the delete button; otherwise, hide it
        deleteExerciseButton.setVisibility(exerciseRows.getChildCount() > 3 ? View.VISIBLE : View.GONE);
    }

    /**
     * Validate all exercise fields are populated
     */
    private Boolean validateExerciseFields() {
        int i = exerciseRows.indexOfChild(addExerciseButton) - 1; // Index of current row
        View exerciseRow = exerciseRows.getChildAt(i);
        EditText editExerciseName = exerciseRow.findViewById(R.id.editExerciseName);
        EditText editSets = exerciseRow.findViewById(R.id.editSets);
        EditText editReps = exerciseRow.findViewById(R.id.editReps);
        EditText editWeight = exerciseRow.findViewById(R.id.editWeight);

        // Check name
        if (editExerciseName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(string.exercise_placeholder) + " " + getString(R.string.empty_exercise_message), Toast.LENGTH_SHORT).show();
            return false;
        }
        // Check sets
        if (editSets.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(string.sets_placeholder) + " " + getString(R.string.empty_exercise_message), Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check reps
        if (editReps.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(string.reps_placeholder) + " " + getString(R.string.empty_exercise_message), Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check weight
        if (editWeight.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(string.weight_placeholder) + " " +getString(R.string.empty_exercise_message), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Save a workout to json format
     * Called when saveButton is pressed
     */
    private void saveWorkout() {
        try {

            if (!validateExerciseFields()) {
                return;
            }

            // Get workout name
            EditText editWorkoutName = this.findViewById(R.id.editWorkoutName);

            // Assert workout name is set
            if (editWorkoutName.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, getString(string.workout_name_placeholder) + " " + getString(R.string.empty_exercise_message), Toast.LENGTH_SHORT).show();
                return;
            }

            workout.setWorkoutName(editWorkoutName.getText().toString());

            // Set workout timestamp
            long timestamp = System.currentTimeMillis();
            workout.setTimestamp(timestamp);

            // Loop through the exercise rows
            for (int i = 0; i < exerciseRows.getChildCount(); i++) {
                View exerciseRow = exerciseRows.getChildAt(i);
                if (exerciseRow instanceof LinearLayout) {

                    EditText editExerciseName = exerciseRow.findViewById(R.id.editExerciseName);
                    EditText editSets = exerciseRow.findViewById(R.id.editSets);
                    EditText editReps = exerciseRow.findViewById(R.id.editReps);
                    EditText editWeight = exerciseRow.findViewById(R.id.editWeight);

                    String name = editExerciseName.getText().toString();
                    int sets = Integer.parseInt(editSets.getText().toString());
                    int reps = Integer.parseInt(editReps.getText().toString());
                    float weight = Float.parseFloat(editWeight.getText().toString());

                    workout.addExercise(name, sets, reps, weight);
                }
            }

            // Save workout to file
            FileManager.saveWorkout(this, workout);
            Toast.makeText(this, getString(string.workout_saved_successfully), Toast.LENGTH_SHORT).show();

            // Save workout to API
            APIManager.addWorkout(workout, this , success -> {
                switch (success) {
                    case -1: {
                        NewWorkoutActivity.this.newWorkoutError();
                        break;
                    }
                    case 0: {
                        NewWorkoutActivity.this.newWorkoutFail();
                        break;
                    }
                    case 1: {
                        NewWorkoutActivity.this.newWorkoutSuccess();
                        break;
                    }
                }
            });

        } catch (Exception e) {
            Log.e("NewWorkoutActivity", e.toString());
            Toast.makeText(this, getString(string.error_saving_workout) + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void newWorkoutSuccess() {
        Log.e("NewWorkoutActivity", "Success");
    }

    private void newWorkoutFail() {
        Log.e("NewWorkoutActivity", "Fail");

    }

    private void newWorkoutError() {
        Log.e("NewWorkoutActivity", "Error");

    }
}