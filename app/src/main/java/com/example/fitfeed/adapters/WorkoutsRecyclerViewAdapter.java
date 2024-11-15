package com.example.fitfeed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitfeed.R;
import com.example.fitfeed.models.Workout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkoutsRecyclerViewAdapter extends RecyclerView.Adapter<WorkoutsRecyclerViewAdapter.ViewHolder> {

    private List<Workout> data;
    private LayoutInflater inflater;

    public WorkoutsRecyclerViewAdapter(Context context, List<Workout> data) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_row_workout, parent, false);
        return new WorkoutsRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Workout workout = data.get(position);

        // Set workout name
        holder.titleTextView.setText(workout.getWorkoutName());

        // Format the timestamp to a readable date and time format
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(new Date(workout.getTimestamp()));
        holder.workoutDateTextView.setText(formattedDate);

        // Clear previous exercises in case of reuse
        holder.exerciseDetailsContainer.removeAllViews();

        // Dynamically add each exercise detail as a TextView
        for (Workout.Exercise exercise : workout.getExercises()) {
            TextView exerciseDetail = new TextView(inflater.getContext());
            exerciseDetail.setText(String.format(Locale.getDefault(),
                    "%s: %d sets x %d reps at %.1f lbs",
                    exercise.getName(), exercise.getSets(), exercise.getReps(), exercise.getWeight()));
            exerciseDetail.setTextSize(14);
            exerciseDetail.setPadding(8, 4, 8, 4); // Add padding around each exercise for spacing
            exerciseDetail.setTextColor(inflater.getContext().getResources().getColor(R.color.navy));
            holder.exerciseDetailsContainer.addView(exerciseDetail);
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView workoutDateTextView;
        LinearLayout exerciseDetailsContainer;

        ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.workoutTextView);
            workoutDateTextView = itemView.findViewById(R.id.workoutDateTextView);
            exerciseDetailsContainer = itemView.findViewById(R.id.exerciseDetailsContainer);
        }
    }
}
