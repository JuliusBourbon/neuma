package com.example.neuma.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class TokenManager {
    private static final String PREFS_NAME = "secure_prefs";
    private static final String KEY_TOKEN = "jwt_token";
    private SharedPreferences sharedPreferences;

    public TokenManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(KEY_TOKEN, token).commit();
        }
    }

    public void saveUsername(String username) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString("username", username).commit();
        }
    }

    public String getToken() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(KEY_TOKEN, null);
        }
        return null;
    }

    public String getUsername() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString("username", null);
        }
        return null;
    }

    public void clearToken() {
        if (sharedPreferences != null) {
            sharedPreferences.edit().remove(KEY_TOKEN).remove("username").commit();
        }
    }

    public boolean isTokenValid() {
        String token = getToken();
        return token != null && !token.trim().isEmpty();
        // (Opsional) Jika perlu, bisa ditambah logic decode JWT untuk cek expiration date (exp)
    }

    public boolean isFirstTimeTutorial() {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean("first_time_tutorial", false);
        }
        return false;
    }

    public void setFirstTimeTutorial(boolean isFirstTime) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putBoolean("first_time_tutorial", isFirstTime).apply();
        }
    }
}
