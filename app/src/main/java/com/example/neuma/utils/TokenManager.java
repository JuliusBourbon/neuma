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
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            Log.e("TokenManager", "Error creating EncryptedSharedPreferences", e);
        }
    }

    public void saveToken(String token) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(KEY_TOKEN, token).apply();
        }
    }

    public String getToken() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(KEY_TOKEN, null);
        }
        return null;
    }

    public void clearToken() {
        if (sharedPreferences != null) {
            sharedPreferences.edit().remove(KEY_TOKEN).apply();
        }
    }

    public boolean isTokenValid() {
        String token = getToken();
        return token != null && !token.trim().isEmpty();
        // (Opsional) Jika perlu, bisa ditambah logic decode JWT untuk cek expiration date (exp)
    }
}
