package com.example.fitfeed.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.UUID;

import com.example.fitfeed.models.Post;
import com.example.fitfeed.models.Workout;
import com.example.fitfeed.models.dto.PostDto;
import com.google.gson.Gson;

/**
 * util for making API calls
 */
public class APIManager {
    private APIManager() {}

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();  // Use a single-thread executor for simplicity

    static final String API_URL = "http://api.fitfeed.online:8081";
    //static final String API_URL = "http://10.0.2.2:8081";
    static final String LOGIN_ENDPOINT = "/login";
    static final String REGISTER_ENDPOINT = "/register";
    static final String ADD_WORKOUT_ENDPOINT = "/workout";
    static final String PULL_WORKOUTS_ENDPOINT = "/workouts";
    static final String MAKE_POST_ENDPOINT = "/post";
    static final String GET_POST_ENDPOINT = "/posts";

    static final UUID TEST_USER_ID = UUID.fromString("5d72bb37-a696-450e-b5f4-fd9dd06c5a33");

    /**
     * Send a login request
     * @param username
     * @param password
     * @param context
     * @param callback
     * returns -1 for a connection error, 0 for a failed login, and 1 for a success
     */
    public static void Login(String username, String password, Context context, APICallback callback) {
        executorService.submit(() -> {
            int statusCode = 0; // Default to failure

            try {
                URL url = new URL(API_URL + LOGIN_ENDPOINT);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                // Create JSON payload
                String jsonInputString = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);
                conn.setDoOutput(true);
                conn.getOutputStream().write(jsonInputString.getBytes(StandardCharsets.UTF_8));

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Parse JSON response to get the token
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String accessToken = jsonResponse.getString("access_token");
                    String refreshToken = jsonResponse.getString("refresh_token");
                    int expiresIn = jsonResponse.getInt("expires_in");
                    int refreshExpiresIn = jsonResponse.getInt("refresh_expires_in");

                    TokenManager.saveTokens(accessToken, refreshToken, expiresIn, refreshExpiresIn);

                    statusCode = 1;
                }

            } catch (Exception e) {
                Log.e("TAG", e.toString());
                statusCode = -1;
            }

            int finalStatusCode = statusCode;
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                callback.onResult(finalStatusCode);
            });
        });
    }

    /**
     * Send a register request
     * @param firstName
     * @param lastName
     * @param username
     * @param email
     * @param password
     * @param context
     * @param callback
     * returns -1 for a connection error, 0 for a failed registration, and 1 for a success
     */
    public static void Register(String firstName, String lastName, String username, String email, String password, Context context, APICallback callback) {
        executorService.submit(() -> {
            int statusCode = 0; // Default to failure

            try {
                URL url = new URL(API_URL + REGISTER_ENDPOINT);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject payload = new JSONObject();
                payload.put("firstName", firstName);
                payload.put("lastName", lastName);
                payload.put("username", username);
                payload.put("email", email);
                payload.put("enabled", true);

                JSONArray credentialsArray = new JSONArray();
                JSONObject credentials = new JSONObject();
                credentials.put("type", "password");
                credentials.put("value", password);
                credentials.put("temporary", false);
                credentialsArray.put(credentials);

                payload.put("credentials", credentialsArray);

                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(payload.toString().getBytes(StandardCharsets.UTF_8));
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Registration successful
                    statusCode = 1;
                } else {
                    // Registration failed
                    statusCode = 0;
                }
            } catch (Exception e) {
                Log.e("Register", "Error during registration", e);
                statusCode = -1; // Connection error
            }

            int finalStatusCode = statusCode;
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                callback.onResult(finalStatusCode);
            });
        });
    }

    /**
     * Get a list of post data from the backend
     * @return List of Post data.
     */
    public static MutableLiveData<List<Post>> getPosts() {
        MutableLiveData<List<Post>> postsData = new MutableLiveData<>();

        executorService.submit(() -> {
            int statusCode = 0;

            try {
                URL url = new URL(API_URL + GET_POST_ENDPOINT);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                String user = TokenManager.getAccessToken();
                conn.setRequestProperty("Authorization", "Bearer " + user);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();
                    String json = response.toString();
                    List<Post> posts = parsePosts(json);
                    postsData.postValue(posts);
                    statusCode = 1;
                }
            } catch (Exception e) {
                Log.e("POSTS: GET FAILED", e.toString());
                statusCode = -1;
            }
        });

        return postsData;
    }

    /**
     * Helper function to create a list of posts from a JSON string
     * @param json JSON string of post data to be parsed
     * @return List of Posts
     */
    private static List<Post> parsePosts(String json) {
        Gson gson = new Gson();
        PostDto[] posts = gson.fromJson(json, PostDto[].class);
        ArrayList<Post> result = new ArrayList<>();
        if (posts != null) {
            for (PostDto post : posts) {
                result.add(Post.fromDto(post));
            }
        }
        return result;
    }

    /**
     * Send a post to the server
     * @param post The post to be sent
     * @param context App context
     * @param callback Callback to determine fail/success
     */
    public static void makePost(Post post, Context context, PostCallback callback) {
        executorService.submit(() -> {
            int statusCode = 0;

            try {
                URL url = new URL(API_URL + MAKE_POST_ENDPOINT);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                String user = TokenManager.getAccessToken();
                conn.setRequestProperty("Authorization", "Bearer " + user);
                // Create JSON payload
                // Build exercise json string first
                //StringBuilder postsJson = new StringBuilder();

                String jsonInputString = String.format("{\"usedId\": \"%s\", \"postText\": \"%s\", \"workoutId\": \"%d\", \"imageUri\": \"%s\"}",
                        "TestUser1", post.getPostText(), 1L, post.getPostImageUrl());

                conn.setDoOutput(true);
                conn.getOutputStream().write(jsonInputString.getBytes(StandardCharsets.UTF_8));
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    statusCode = 1;
                }
            } catch (Exception e) {
                Log.e("POSTING FAIL", e.toString());
                statusCode = -1;
            }

            int finalStatusCode = statusCode;
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                callback.onPostResult(finalStatusCode);
            });
        });
    }

    @SuppressLint("DefaultLocale")
    public static void addWorkout(Workout workout, Context context, APICallback callback) {
        executorService.submit(() -> {
            int statusCode = 0; // Default to failure

            try {
                URL url = new URL(API_URL + ADD_WORKOUT_ENDPOINT);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                String user = TokenManager.getAccessToken();
                conn.setRequestProperty("Authorization", "Bearer " + user);
                // Create JSON payload

                // Build exercise json string first
                StringBuilder exercisesJson = new StringBuilder();
                for (Workout.Exercise exercise : workout.getExercises()) {
                    // If not first
                    if (exercisesJson.length() > 0) {
                        exercisesJson.append(", ");
                    }

                    exercisesJson.append(String.format("{\"exerciseName\": \"%s\", \"sets\": %d, \"reps\": %d, \"weight\": %.1f}",
                            exercise.getName(), exercise.getSets(), exercise.getReps(), exercise.getWeight()));
                }
                String jsonInputString = String.format("{\"workoutName\": \"%s\", \"workoutTimestamp\": %d, \"exercises\": [%s]}",
                        workout.getWorkoutName(), workout.getTimestamp(), exercisesJson
                );

                conn.setDoOutput(true);
                conn.getOutputStream().write(jsonInputString.getBytes(StandardCharsets.UTF_8));

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    statusCode = 1;
                }
            } catch (Exception e) {
                Log.e("TAG", e.toString());
                statusCode = -1;
            }

            int finalStatusCode = statusCode;
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                callback.onResult(finalStatusCode);
            });
        });
    }

    public static MutableLiveData<List<Workout>> GetWorkouts()
    {
        MutableLiveData<List<Workout>> liveData = new MutableLiveData<>();  // workouts to load
        executorService.submit(() -> {
            int statusCode = 0; // Default to failure

            try
            {
                URL url = new URL(API_URL + PULL_WORKOUTS_ENDPOINT);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                String user = TokenManager.getAccessToken();
                conn.setRequestProperty("Authorization", "Bearer " + user);

                int responseCode = conn.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK)
                {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();

                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    String json = response.toString();
                    List<Workout> workouts = parseWorkouts(json);
                    liveData.postValue(workouts);

                    statusCode = 1;
                }

            } catch (Exception e) {
                Log.e("WORKOUTS: GET FAILED", e.toString());
                statusCode = -1;
            }

        });

        return liveData;
    }

    private static List<Workout> parseWorkouts(String json) {

        Gson gson = new Gson();
        Workout[] workouts = gson.fromJson(json, Workout[].class);
        ArrayList<Workout> result = new ArrayList<>(List.of(workouts));

        return result;
    }
}
