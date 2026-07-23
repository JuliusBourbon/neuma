package com.example.neuma;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.neuma.utils.TokenManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TokenManager tokenManager = new TokenManager(this);

        // Tunda 2 detik untuk menampilkan splash screen
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (tokenManager.isTokenValid()) {
                // Token valid -> arahkan ke Homepage
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                // Tidak ada token / tidak valid -> arahkan ke Login Screen
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            finish();
        }, 2000);
    }
}
