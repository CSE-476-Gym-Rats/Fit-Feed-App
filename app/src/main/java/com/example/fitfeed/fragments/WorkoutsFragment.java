package com.example.fitfeed.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fitfeed.R;
import com.example.fitfeed.activities.CameraActivity;
import com.example.fitfeed.activities.FriendsActivity;
import com.example.fitfeed.activities.NewWorkoutActivity;
import com.example.fitfeed.adapters.WorkoutsRecyclerViewAdapter;
import com.example.fitfeed.models.Post;
import com.example.fitfeed.models.Workout;
import com.example.fitfeed.util.APIManager;
import com.example.fitfeed.util.FileManager;
import com.example.fitfeed.adapters.PostsRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkoutsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkoutsFragment extends Fragment {
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

    /** @noinspection deprecation*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /** @noinspection deprecation*/
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.workout_menu, menu);
    }

    /** @noinspection deprecation*/
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.workoutMenuNewWorkout) {
            Intent intent = new Intent(getContext(), NewWorkoutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWorkouts();
    }

    private void loadWorkouts() {
        // Live data: API request takes a second, so it will auto update once the response comes in from the server
        MutableLiveData<List<Workout>> liveData = APIManager.GetWorkouts();
        liveData.observe(getViewLifecycleOwner(), workouts -> {
            if(!workouts.isEmpty())
            {
                WorkoutsRecyclerViewAdapter adapter = new WorkoutsRecyclerViewAdapter(getContext(), workouts);
                workoutRecyclerView.setAdapter(adapter);
            }
            else
            {
                Toast.makeText(getContext(), "Error loading workouts.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}