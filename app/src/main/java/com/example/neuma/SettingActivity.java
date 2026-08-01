package com.example.neuma;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neuma.models.User;
import com.example.neuma.network.UserApi;
import com.example.neuma.utils.ApiClient;
import com.example.neuma.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvUsername, btnEditAvatarText;
    private ImageView ivAvatar;
    private LinearLayout btnMenuEditProfile, btnMenuSupport;
    private Button btnLogout;
    private ProgressBar progressBar;

    private UserApi userApi;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        btnBack = findViewById(R.id.btn_back);
        tvUsername = findViewById(R.id.tv_settings_username);
        ivAvatar = findViewById(R.id.iv_profile_avatar);
        btnEditAvatarText = findViewById(R.id.btn_edit_avatar_text);
        btnMenuEditProfile = findViewById(R.id.btn_menu_edit_profile);
        btnMenuSupport = findViewById(R.id.btn_menu_support);
        btnLogout = findViewById(R.id.btn_logout);
        progressBar = findViewById(R.id.progress_bar_setting);

        tokenManager = new TokenManager(this);
        userApi = ApiClient.getAuthClient(this).create(UserApi.class);

        btnBack.setOnClickListener(v -> finish());

        btnMenuEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Klik "Support"
        btnMenuSupport.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, SupportActivity.class);
            startActivity(intent);
        });

        // Klik Edit Avatar
        btnEditAvatarText.setOnClickListener(v ->
                Toast.makeText(SettingActivity.this, "Ganti Avatar", Toast.LENGTH_SHORT).show()
        );

        // Aksi Logout
        btnLogout.setOnClickListener(v -> performLogout());

        // Load data profil pengguna
        loadProfile();
    }

    private void loadProfile() {
        setLoading(true);
        userApi.getProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    tvUsername.setText(response.body().getName());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                setLoading(false);
            }
        });
    }

    private void performLogout() {
        tokenManager.clearToken();
        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}
