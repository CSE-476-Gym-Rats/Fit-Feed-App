package com.example.fitfeed.util;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
}
