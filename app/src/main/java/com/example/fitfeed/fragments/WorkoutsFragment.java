package com.example.fitfeed.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fitfeed.R;
import com.example.fitfeed.activities.NewWorkoutActivity;
import com.example.fitfeed.adapters.WorkoutsRecyclerViewAdapter;
import com.example.fitfeed.models.Workout;
import com.example.fitfeed.util.FileManager;
import com.example.fitfeed.adapters.PostsRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkoutsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkoutsFragment extends Fragment {
    private FloatingActionButton newWorkoutButton;
    private RecyclerView workoutRecyclerView;

    public WorkoutsFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WorkoutsFragment.
     */
    public static WorkoutsFragment newInstance() {
        WorkoutsFragment fragment = new WorkoutsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workouts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup RecyclerView
        workoutRecyclerView = view.findViewById(R.id.recyclerViewWorkouts);
        workoutRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load workouts and set adapter
        loadWorkouts();

        // Listener for newWorkoutButton
        newWorkoutButton = getView().findViewById(R.id.newWorkoutButton);
        newWorkoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), NewWorkoutActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWorkouts();
    }

    private void loadWorkouts() {
        try {
            List<Workout> workouts = FileManager.loadWorkouts(getContext());
            WorkoutsRecyclerViewAdapter adapter = new WorkoutsRecyclerViewAdapter(getContext(), workouts);
            workoutRecyclerView.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error loading workouts.", Toast.LENGTH_SHORT).show();
        }
    }
}