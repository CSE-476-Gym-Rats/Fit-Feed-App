package com.example.fitfeed.util;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.UUID;

import com.example.fitfeed.models.Workout;
import com.example.fitfeed.models.Post;
//import com.example.fitfeed.util.TokenManager;

/**
 * util for making API calls
 */
public class APIManager {
    private APIManager() {}

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();  // Use a single-thread executor for simplicity

    static final String API_URL = "http://api.fitfeed.online:8081";
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
                conn.getOutputStream().write(jsonInputString.getBytes("UTF-8"));

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
                    os.write(payload.toString().getBytes("UTF-8"));
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



    // Temporary testing lines. Friends are not implemented, so I have no way to test getting posts from friends
    // Also awaiting approval for changes to backend for Post support
    private static List<String> testUserFriends = new ArrayList<>();
    private static Dictionary<String, List<Post>> testPosts = new Hashtable<>();

    public static ArrayList<Post> getPosts(String user) {
        //executorService.submit(() -> {
        ArrayList<Post> result = new ArrayList<>();
            int statusCode = 0; // Default to failure

            try {
                URL url = new URL(API_URL + GET_POST_ENDPOINT);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.connect();

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

                    for (Post post : posts) {
                        result.add(post);
                    }
                }

            } catch (Exception e) {
                Log.e("TAG", e.toString());
                statusCode = -1;
            }
            int finalStatusCode = statusCode;

        //});
        return result;
    }

    private static List<Post> parsePosts(String json) {
        List<Post> posts = new ArrayList<>();

        json = json.substring(1, json.length() - 1);

        String[] objects = json.split("},\\{");

        for (String object : objects) {
            object = object.replace("{", "").replace("}", "");
            String[] fields = object.split(",");

            String text = "";
            String user = "";
            long workoutid = 0;
            String imgUri = "";
            for (String field : fields) {
                String[] keyValue = field.split(":");

                String key = keyValue[0].trim().replace("\"", "");
                String value = keyValue[1].trim().replace("\"", "");

                switch (key) {
                    case "postText":
                        text = value;
                        break;
                    case "userId":
                        user = value;
                        break;
                    case "workoutId":
                        workoutid = Long.parseLong(value);
                        break;
                    case "imageUri":
                        imgUri = value;
                        break;
                }
            }
            posts.add(new Post(text, user));
        }

        return posts;
    }

    public static void makePost(Post post, Context context) {
        int statusCode = 0; // Default to failure

        try {
            URL url = new URL(API_URL + MAKE_POST_ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            // Create JSON payload
            // todo: change workoutid argument from timestamp to actual workout id and add user id
            String jsonInputString = String.format("{\"userId\": \"%s\", \"postText\": \"%s\", \"workoutId\": \"%d\", \"imageUri\": \"%s\"}",
                    TEST_USER_ID, post.getPostText(), post.getPostWorkout().getTimestamp(), "text uri");
            conn.setDoOutput(true);
            conn.getOutputStream().write(jsonInputString.getBytes("UTF-8"));

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

            }
        } catch (Exception e) {
            Log.e("TAG", e.toString());
            statusCode = -1;
        }
    }

    /**
     * Sets up data to simulate added friends and workout posts to load.
     */
    public static void setupTestPosts() {
        testUserFriends.add("friend1");
        testUserFriends.add("friend20");

        Workout w1 = new Workout();
        w1.addExercise("Push Ups", 5, 8, 10f);
        w1.addExercise("Bicep Curls", 5, 10, 50f);

        Workout w2 = new Workout();
        w2.addExercise("Squats", 3, 5, 200f);

        Workout w3 = new Workout();
        w3.addExercise("Bench", 1, 5, 150f);

        Workout w4 = new Workout();
        w4.addExercise("lazy", 10, 0, 15f);

        PostWorkout("friend1", "just did my first workout!", w1, null, null);
        PostWorkout("friend1", "just benched 150!", w3, null, null);
        PostWorkout("friend20", "Leg day today", w2, null, null);

        // Non-friend
        PostWorkout("friend0", "Not friend testing", w4, null, null);
    }

    /**
     * Get workout posts from user's friends.
     */
    public static ArrayList<Post> GetWorkoutPosts() {
        /*executorService.submit(() -> {
            int statusCode = 0; // Default to failure

            try {
                URL url = new URL(API_URL + PULL_WORKOUTS_ENDPOINT);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
            } catch (Exception e) {
                Log.e("TAG", e.toString());
                statusCode = -1;
            }
        });*/

        ArrayList<Post> result = new ArrayList<>();  // posts to load
        List<String> friends = new ArrayList<>();  // List of friends
        Enumeration<String> globalUsers = testPosts.keys();

        // search thru database of users to find friends
        while (globalUsers.hasMoreElements()) {
            String nextUser = globalUsers.nextElement();
            if (testUserFriends.contains(nextUser)) {
                friends.add(nextUser);
            }
        }

        // search thru friends to get posts to load
        for (String friend : friends) {
            if (testPosts.get(friend) != null) {  // if the friend has posts
                // get their posts and put into result!
                result.addAll(testPosts.get(friend));
            }
        }

        // return list of posts to load
        return result;
    }

    /**
     * Create a post (currently simulated due to github request not yet approved)
     * @param text the post's text
     * @param workout workout that goes with the post
     */
    public static void PostWorkout(String user, String text, Workout workout, String img, String imgUrl) {
        // create a Post that includes a workout, sample user ID/username, and text
        //String user = TokenManager.getAccessToken();

        // todo: add image support to post

        Post newPost = new Post(text, user, img, imgUrl, workout);

        // add post to test dictionary (will be database when friends implemented)
        if (testPosts.get(user) != null) {
            // if user exists, add the post to them
            testPosts.get(user).add(newPost);
        }
        else {
            // else, add user and posts to dictionary
            List<Post> l = new ArrayList<>();
            l.add(newPost);
            testPosts.put(user, l);
        }
    }
}
