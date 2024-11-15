package com.example.fitfeed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitfeed.FitFeedApp;
import com.example.fitfeed.R;
import com.example.fitfeed.models.Post;
import com.example.fitfeed.models.Workout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerViewAdapter for Social tab's feed.
 */
public class PostsRecyclerViewAdapter extends RecyclerView.Adapter<PostsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Post> posts;
    private final LayoutInflater inflater;

    public PostsRecyclerViewAdapter(Context context, List<Post> posts) {
        this.inflater = LayoutInflater.from(context);
        this.posts = (ArrayList<Post>) posts;
    }

    public void restorePostsState(List<Post> posts) {
        int oldSize = this.posts.size();
        this.posts = new ArrayList<>();
        notifyItemRangeRemoved(0, oldSize);
        this.posts.addAll(posts);
        notifyItemRangeInserted(0, this.posts.size());
    }

    public void addPosts(List<Post> posts) {
        this.posts.addAll(0, posts);
        notifyItemRangeInserted(0, posts.size());
    }

    public void addPost(Post post) {
        this.posts.add(0, post);
        notifyItemInserted(0);
    }

    public ArrayList<Post> getPosts() {
        return this.posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_row_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post post = posts.get(position);

        // Set post text and image
        holder.postTextView.setText(post.getPostText());
        holder.postImageView.setImageDrawable(post.getPostDrawable());

        // Format and display workout details
        Workout workout = post.getPostWorkout();
        if (workout != null) {
            StringBuilder workoutDetails = new StringBuilder();
            workoutDetails.append(String.format("Workout: %s\n", workout.getWorkoutName()));
            for (Workout.Exercise exercise : workout.getExercises()) {
                workoutDetails.append(String.format(Locale.getDefault(),
                        "- %s: %d sets x %d reps at %.1f lbs\n",
                        exercise.getName(), exercise.getSets(), exercise.getReps(), exercise.getWeight()));
            }
            holder.postTextView2.setText(workoutDetails.toString().trim());
        } else {
            holder.postTextView2.setText("No workout details available.");
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView postTextView;
        ImageView postImageView;
        TextView postTextView2;

        ViewHolder(View itemView) {
            super(itemView);
            postTextView = itemView.findViewById(R.id.postTextView);
            postImageView = itemView.findViewById(R.id.postImageView);
            postTextView2 = itemView.findViewById(R.id.postTextView2);
        }
    }
}
