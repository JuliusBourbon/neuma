package com.example.neuma;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.neuma.models.UpdatePasswordRequest;
import com.example.neuma.models.UpdateProfileRequest;
import com.example.neuma.models.User;
import com.example.neuma.network.UserApi;
import com.example.neuma.utils.ApiClient;
import com.example.neuma.utils.TokenManager;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingActivity extends AppCompatActivity {

    private ImageView ivAvatar;
    private TextView tvName;
    private TextInputEditText etNewName, etOldPassword, etNewPassword;
    private Button btnUpdateProfile, btnUpdatePassword, btnLogout;
    private ProgressBar progressBar;

    private UserApi userApi;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ivAvatar = findViewById(R.id.iv_profile_avatar);
        tvName = findViewById(R.id.tv_profile_name);
        etNewName = findViewById(R.id.et_new_name);
        etOldPassword = findViewById(R.id.et_old_password);
        etNewPassword = findViewById(R.id.et_new_password);
        btnUpdateProfile = findViewById(R.id.btn_update_profile);
        btnUpdatePassword = findViewById(R.id.btn_update_password);
        btnLogout = findViewById(R.id.btn_logout);
        progressBar = findViewById(R.id.progress_bar_setting);

        tokenManager = new TokenManager(this);
        userApi = ApiClient.getAuthClient(this).create(UserApi.class);

        btnUpdateProfile.setOnClickListener(v -> updateProfile());
        btnUpdatePassword.setOnClickListener(v -> updatePassword());
        btnLogout.setOnClickListener(v -> logout());

        loadProfile();
    }

    private void loadProfile() {
        setLoadingState(true);
        userApi.getProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                setLoadingState(false);
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    tvName.setText(user.getName());
                    etNewName.setText(user.getName());

                    if (user.getAvatarSeed() != null && user.getAvatarStyle() != null) {
                        String url = "https://api.dicebear.com/9.x/" + user.getAvatarStyle() + "/png?seed=" + user.getAvatarSeed();
                        Glide.with(SettingActivity.this).load(url).into(ivAvatar);
                    }
                } else {
                    Toast.makeText(SettingActivity.this, "Gagal memuat profil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                setLoadingState(false);
                Toast.makeText(SettingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        String newName = etNewName.getText().toString().trim();
        if (newName.isEmpty()) {
            Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoadingState(true);
        userApi.updateProfile(new UpdateProfileRequest(newName)).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                setLoadingState(false);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(SettingActivity.this, "Profil berhasil diupdate", Toast.LENGTH_SHORT).show();
                    tvName.setText(response.body().getName());
                } else {
                    Toast.makeText(SettingActivity.this, "Gagal update profil (Mungkin nama sudah dipakai)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                setLoadingState(false);
                Toast.makeText(SettingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePassword() {
        String oldPass = etOldPassword.getText().toString();
        String newPass = etNewPassword.getText().toString();

        if (oldPass.isEmpty() || newPass.isEmpty()) {
            Toast.makeText(this, "Password lama dan baru harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoadingState(true);
        userApi.updatePassword(new UpdatePasswordRequest(oldPass, newPass)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                setLoadingState(false);
                if (response.isSuccessful()) {
                    Toast.makeText(SettingActivity.this, "Password berhasil diubah", Toast.LENGTH_SHORT).show();
                    etOldPassword.setText("");
                    etNewPassword.setText("");
                } else {
                    Toast.makeText(SettingActivity.this, "Gagal mengubah password (Password lama salah?)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                setLoadingState(false);
                Toast.makeText(SettingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        tokenManager.clearToken();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoadingState(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnUpdateProfile.setEnabled(!isLoading);
        btnUpdatePassword.setEnabled(!isLoading);
        btnLogout.setEnabled(!isLoading);
    }
}
