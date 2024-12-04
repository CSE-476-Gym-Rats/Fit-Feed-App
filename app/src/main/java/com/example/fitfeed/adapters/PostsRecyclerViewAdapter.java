package com.example.fitfeed.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fitfeed.R;
import com.example.fitfeed.models.Post;
import com.example.fitfeed.models.Workout;

import org.apache.commons.validator.routines.UrlValidator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerViewAdapter for Social tab's feed.
 */
public class PostsRecyclerViewAdapter extends RecyclerView.Adapter<PostsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Post> posts;
    private final LayoutInflater inflater;
    private final UrlValidator urlValidator = new UrlValidator();
    private final DecimalFormat weightFormat = new DecimalFormat("0.#");

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
        // set text and drawable for each post
        holder.imageView.setVisibility(View.VISIBLE);
        holder.textView.setText(posts.get(position).getPostText());
        holder.textViewUsername.setText(posts.get(position).getPostUser());
        if (posts.get(position).getPostImageUrl() != null && urlValidator.isValid(posts.get(position).getPostImageUrl())) {
            Log.d("PostsRecyclerViewAdapter.onBindViewHolder", "Loading post image from url.");
            Glide.with(inflater.getContext())
                    .load(posts.get(position).getPostImageUrl())
                    .centerCrop()
                    .into(holder.imageView);
        } else if (posts.get(position).getPostDrawable() != null) {
            Log.d("PostsRecyclerViewAdapter.onBindViewHolder", "Loading post image from filename.");
            holder.imageView.setImageDrawable(posts.get(position).getPostDrawable());
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
        Workout postWorkout = posts.get(position).getPostWorkout();
        if (postWorkout != null) {
            List<Workout.Exercise> exercises = postWorkout.getExercises();
            String formattedText = "Workout exercises:\n";

            for (Workout.Exercise exercise : exercises) {
                String formattedExercise = String.format(
                        "%s, %d sets, %d reps, weight: %s\n",
                        exercise.getName(),
                        exercise.getSets(),
                        exercise.getReps(),
                        weightFormat.format(exercise.getWeight())
                );
                formattedText = formattedText + formattedExercise;
            }
            holder.textView2.setText(
                    /*String.format(
                            FitFeedApp.getContext().getResources().getString(R.string.post_workout_json_format),
                            GsonHelper.getGson().toJson(posts.get(position).getPostWorkout())
                    )*/
                    formattedText
            );
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView textViewUsername;
        ImageView imageView;
        TextView textView2;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.postTextView);
            textViewUsername = itemView.findViewById(R.id.postUserTextView);
            imageView = itemView.findViewById(R.id.postImageView);
            textView2 = itemView.findViewById(R.id.postTextView2);
        }
    }
}
