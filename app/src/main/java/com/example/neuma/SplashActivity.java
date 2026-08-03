package com.example.neuma;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.neuma.models.Achievement;
import com.example.neuma.models.Level;
import com.example.neuma.models.User;
import com.example.neuma.network.AchievementApi;
import com.example.neuma.network.LevelApi;
import com.example.neuma.network.UserApi;
import com.example.neuma.utils.ApiClient;
import com.example.neuma.utils.DataManager;
import com.example.neuma.utils.TokenManager;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TokenManager tokenManager = new TokenManager(this);

        if (tokenManager.isTokenValid()) {
            // Token is valid -> Prefetch data, then go to MainActivity
            prefetchData();
        } else {
            // No valid token -> Wait 2 seconds, go to Login
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }, 2000);
        }
    }

    private void prefetchData() {
        UserApi userApi = ApiClient.getAuthClient(this).create(UserApi.class);
        LevelApi levelApi = ApiClient.getAuthClient(this).create(LevelApi.class);
        AchievementApi achievementApi = ApiClient.getAuthClient(this).create(AchievementApi.class);

        AtomicInteger pendingRequests = new AtomicInteger(3);
        long startTime = System.currentTimeMillis();

        // 1. Fetch Profile
        userApi.getProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DataManager.getInstance().setCurrentUser(response.body());
                }
                checkCompletion(pendingRequests, startTime);
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Failed to load profile", t);
                checkCompletion(pendingRequests, startTime);
            }
        });

        // 2. Fetch Levels
        levelApi.getLevels().enqueue(new Callback<List<Level>>() {
            @Override
            public void onResponse(Call<List<Level>> call, Response<List<Level>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DataManager.getInstance().setLevels(response.body());
                }
                checkCompletion(pendingRequests, startTime);
            }
            @Override
            public void onFailure(Call<List<Level>> call, Throwable t) {
                Log.e(TAG, "Failed to load levels", t);
                checkCompletion(pendingRequests, startTime);
            }
        });

        // 3. Fetch Achievements
        achievementApi.getAchievements().enqueue(new Callback<List<Achievement>>() {
            @Override
            public void onResponse(Call<List<Achievement>> call, Response<List<Achievement>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DataManager.getInstance().setAchievements(response.body());
                }
                checkCompletion(pendingRequests, startTime);
            }
            @Override
            public void onFailure(Call<List<Achievement>> call, Throwable t) {
                Log.e(TAG, "Failed to load achievements", t);
                checkCompletion(pendingRequests, startTime);
            }
        });
    }

    private synchronized void checkCompletion(AtomicInteger pendingRequests, long startTime) {
        if (pendingRequests.decrementAndGet() == 0) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            long remainingTime = 2000 - elapsedTime; // Ensure splash shows for at least 2 seconds

            if (remainingTime > 0) {
                new Handler(Looper.getMainLooper()).postDelayed(this::navigateToMain, remainingTime);
            } else {
                navigateToMain();
            }
        }
    }

    private void navigateToMain() {
        boolean fromSignup = getIntent().getBooleanExtra("FROM_SIGNUP", false);
        if (fromSignup) {
            startActivity(new Intent(SplashActivity.this, OnboardingActivity.class));
        } else {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        }
        finish();
    }
}

