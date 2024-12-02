package com.example.fitfeed.util;

import com.example.fitfeed.models.Post;
import com.example.fitfeed.models.Workout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

/**
 * Helper class for Gson.
 */
public class GsonHelper {
    private static Gson gson;

    /**
     * Custom deserializer for {@link Post} class to allow rebuilding the transient drawable on deserialization.
     * @implNote Calls constructor {@link Post#Post(String, String, String, String, Workout)} on deserialization.
     */
    static class PostDeserializer implements JsonDeserializer<Post> {
        @Override
        public Post deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            JsonElement workoutJson = jsonObject.get("postWorkout");
            Workout workout = (workoutJson != null) ? new Gson().fromJson(workoutJson.getAsJsonObject(), Workout.class) : null;

            JsonElement postFilenameJson = jsonObject.get("postFilename");
            JsonElement postImageUrlJson = jsonObject.get("postImageUrl");

            return new Post(
                    jsonObject.get("postText").getAsString(),
                    jsonObject.get("postUser").getAsString(),
                    (postFilenameJson != null) ? postFilenameJson.getAsString() : null,
                    (postImageUrlJson != null) ? postImageUrlJson.getAsString() : null,
                    workout
            );
        }
    }

    /**
     * Helper function to get a {@link Gson} instance with default settings.
     * @return {@link Gson} object with default settings for app.
     */
    public static Gson getGson() {
        if (gson == null) {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Post.class, new PostDeserializer());
            gson = builder.create();
        }
        return gson;
    }
}
