package com.example.fitfeed.util;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

/**
 * Interface for setting/getting access tokens thru EncryptedSharedPreferences
 */
public class TokenManager {
    private static final String PREF_NAME = "secure_auth_pref";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_EXPIRES_IN = "expires_in";
    private static final String KEY_REFRESH_EXPIRES_IN = "refresh_expires_in";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private static SharedPreferences sharedPreferences;

    /**
     * Create shared pref to store tokens
     * @param context
     */
    public static void init(Context context) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            sharedPreferences = EncryptedSharedPreferences.create(
                    PREF_NAME,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            Log.e("TAG", "Error getting token: " + e.getMessage());
        }
    }

    /**
     * Save the tokens to sharedpref
     * @param accessToken
     * @param refreshToken
     * @param expiresIn
     * @param refreshExpiresIn
     */
    public static void saveTokens(String accessToken, String refreshToken, int expiresIn, int refreshExpiresIn) {
        sharedPreferences.edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .putInt(KEY_EXPIRES_IN, expiresIn)
                .putInt(KEY_REFRESH_EXPIRES_IN, refreshExpiresIn)
                .apply();
    }

    /**
     * Save the username & password
     * @param username
     * @param password
     */
    public static void rememberMe(String username, String password) {
        sharedPreferences.edit()
                .putString(USERNAME, username)
                .putString(PASSWORD, password)
                .apply();
    }

    public static String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public static String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    public static int getExpiresIn() {
        return sharedPreferences.getInt(KEY_EXPIRES_IN, 0);
    }

    public static int getRefreshExpiresIn() {
        return sharedPreferences.getInt(KEY_REFRESH_EXPIRES_IN, 0);
    }

    public static void clearTokens() {
        sharedPreferences.edit().clear().apply();
    }

    public static String getUsername() {
        return sharedPreferences.getString(USERNAME, null);
    }

    public static String getPassword() {
        return sharedPreferences.getString(PASSWORD, null);
    }

}