package com.example.fitfeed.util;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    static final String MAKE_POST_ENDPOINT = "/makepost";
    static final String GET_POST_ENDPOINT = "/getposts";

    /**
     * Send a login request
     * @param username
     * @param password
     * @param context
     * @param callback
     * returns -1 for a connection error, 0 for a failed login, and 1 for a success
     */
    public static void Login(String username, String password, Context context, LoginCallback callback) {
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
                callback.onLoginResult(finalStatusCode);
            });
        });
    }

    /**
     * Send a register request
     */
    public static Boolean Register() {
        return true;
    }


    // Temporary testing lines. Friends are not implemented, so I have no way to test getting posts from friends
    // Also awaiting approval for changes to backend for Post support
    private static List<String> testUserFriends = new ArrayList<>();
    private static Dictionary<String, List<Post>> testPosts = new Hashtable<>();

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

        PostWorkout("friend1", "just did my first workout!", w1, null);
        PostWorkout("friend1", "just benched 150!", w3, null);
        PostWorkout("friend20", "Leg day today", w2, null);

        // Non-friend
        PostWorkout("friend0", "Not friend testing", w4, null);
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
    public static void PostWorkout(String user, String text, Workout workout, String img) {
        // create a Post that includes a workout, sample user ID/username, and text
        //String user = TokenManager.getAccessToken();

        // todo: add image support to post

        Post newPost = new Post(text, user, img, workout);

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
