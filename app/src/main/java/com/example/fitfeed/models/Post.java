package com.example.fitfeed.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.example.fitfeed.FitFeedApp;
import com.example.fitfeed.models.dto.PostDto;
import com.example.fitfeed.util.GsonHelper;
import com.google.gson.Gson;

import java.io.File;

/**
 * Class that stores data representing a post.
 */
public class Post implements Parcelable {
    private String postText;
    private String postUser;
    private String postFilename;
    private String postImageUrl;
    private Workout postWorkout;
    //private UUID postUserID;
    private transient Drawable postDrawable;

    /**
     * Constructor for Post without an image.
     * @param postText {@link String} representing the body text of the post.
     * @param postUser {@link String} representing the username of the user associated with the post.
     */
    public Post(String postText, String postUser) {
        this(postText, postUser, null, null, null);
    }

    /**
     * Constructor for Post.
     * @implNote A transient {@link Drawable} will be created based on {@link #postFilename} provided.
     * @param postText {@link String} representing the body text of the post.
     * @param postUser {@link String} representing the username of the user associated with the post.
     * @param postFilename {@link String} filename of the image or resource for the post.
     * @param postImageUrl {@link String} public url of the image resource for the post.
     * @param postWorkout {@link Workout} object associated with the post.
     */
    public Post(String postText, String postUser, String postFilename, String postImageUrl, Workout postWorkout) {
        this.postText = postText;
        this.postUser = postUser;
        this.setPostFilename(postFilename);
        this.postImageUrl = postImageUrl;
        this.postWorkout = postWorkout;
    }

    protected Post(Parcel in) {
        this.postText = in.readString();
        this.postUser = in.readString();
        this.setPostFilename(in.readString());
        this.postImageUrl = in.readString();
        this.postWorkout = GsonHelper.getGson().fromJson(in.readString(), Workout.class);
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public Drawable getPostDrawable() {
        return postDrawable;
    }

    public String getPostFilename() {
        return postFilename;
    }

    public String getPostText() {
        return postText;
    }

    public String getPostUser() {
        return postUser;
    }

    public Workout getPostWorkout() {
        return postWorkout;
    }

    /**
     * Sets the {@link #postFilename} and creates a {@link Drawable} if the file is valid.
     * @implNote If the file is invalid or the {@link Drawable} failed to create, {@link #postFilename} is not set.
     * @param postFilename {@link String} filename of the image or resource for the post.
     */
    @SuppressLint("DiscouragedApi")
    public void setPostFilename(String postFilename) {
        if (postFilename != null) {
            // Attempt to create drawable for post.
            Drawable drawable = Drawable.createFromPath(postFilename);
            if (drawable != null) {
                // Set fields if drawable is created
                this.postFilename = postFilename;
                this.postDrawable = drawable;
            } else {
                // Otherwise try to obtain drawable from resources
                Context context = FitFeedApp.getContext();
                Resources resources = (context != null) ? context.getResources() : null;
                drawable = (resources != null) ? ResourcesCompat.getDrawable(resources, resources.getIdentifier(postFilename.substring(postFilename.lastIndexOf('/') + 1), "drawable", FitFeedApp.getContext().getPackageName()), null) : null;
                if (drawable != null) {
                    this.postFilename = postFilename;
                    this.postDrawable = drawable;
                }
            }
        }
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public void setPostFilename(File file) {
        this.setPostFilename(file.getAbsolutePath());
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public void setPostUser(String postUser) {
        this.postUser = postUser;
    }

    public void setPostWorkout(Workout postWorkout) {
        this.postWorkout = postWorkout;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        // Not writing drawable to parcel, so recreate on restore.
        dest.writeString(this.postText);
        dest.writeString(this.postUser);
        dest.writeString(this.postFilename);
        dest.writeString(this.postImageUrl);
        dest.writeString(GsonHelper.getGson().toJson(this.postWorkout));
    }

    public static Post fromDto(PostDto dto) {
        return new Post(
                dto.postText,
                (dto.username != null) ? dto.username : dto.userId.toString(),
                null,
                dto.imageUri,
                (dto.workout != null) ? Workout.fromDto(dto.workout) : null);
    }
}
