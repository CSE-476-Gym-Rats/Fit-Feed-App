package com.example.fitfeed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitfeed.R;
import com.example.fitfeed.models.Workout;
import com.example.fitfeed.util.GsonHelper;

import java.util.List;

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
        holder.titleTextView.setText(data.get(position).getWorkoutName());
        holder.workoutJsonTextView.setText(GsonHelper.getGson().toJson(data.get(position)));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView workoutJsonTextView; //todo gui instead of json

        ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.workoutTextView);
            workoutJsonTextView = itemView.findViewById(R.id.workoutTextView2);
        }
    }
}
