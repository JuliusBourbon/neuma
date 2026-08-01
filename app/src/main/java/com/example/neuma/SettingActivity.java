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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.neuma.adapters.AvatarSelectionAdapter;
import com.example.neuma.models.Achievement;
import com.example.neuma.models.AvatarItem;
import com.example.neuma.models.UpdateProfileRequest;
import com.example.neuma.models.User;
import com.example.neuma.network.AchievementApi;
import com.example.neuma.network.UserApi;
import com.example.neuma.utils.ApiClient;
import com.example.neuma.utils.TokenManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.List;
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
    private AchievementApi achievementApi;
    private TokenManager tokenManager;

    private User currentUser;

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
        achievementApi = ApiClient.getAuthClient(this).create(AchievementApi.class);

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
        btnEditAvatarText.setOnClickListener(v -> showAvatarSelectionDialog());

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
                    currentUser = response.body();
                    tvUsername.setText(currentUser.getName());
                    loadCurrentAvatar();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                setLoading(false);
            }
        });
    }

    private void loadCurrentAvatar() {
        if (currentUser == null) return;
        String style = currentUser.getAvatarStyle() != null ? currentUser.getAvatarStyle() : "adventurer";
        String seed = currentUser.getAvatarSeed() != null ? currentUser.getAvatarSeed() : "Felix";
        String avatarUrl = "https://api.dicebear.com/9.x/" + style + "/png?seed=" + seed;

        Glide.with(this)
             .load(avatarUrl)
             .into(ivAvatar);
    }

    private void showAvatarSelectionDialog() {
        if (currentUser == null) {
            Toast.makeText(this, "Tunggu sebentar, profil sedang dimuat...", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        achievementApi.getAchievements().enqueue(new Callback<List<Achievement>>() {
            @Override
            public void onResponse(Call<List<Achievement>> call, Response<List<Achievement>> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Achievement> achievements = response.body();
                    List<AvatarItem> availableAvatars = new ArrayList<>();
                    
                    // Default avatar
                    availableAvatars.add(new AvatarItem("adventurer", "Felix", "Felix (Bawaan)"));

                    // Unlocked achievements avatars
                    for (Achievement a : achievements) {
                        if (a.isUnlocked() && a.getRewardAvatarId() != null && !a.getRewardAvatarId().isEmpty()) {
                            String style = a.getRewardAvatarStyle() != null ? a.getRewardAvatarStyle() : "adventurer";
                            String seed = a.getRewardAvatarSeed();
                            availableAvatars.add(new AvatarItem(style, seed, a.getTitle()));
                        }
                    }

                    displayBottomSheet(availableAvatars);
                } else {
                    Toast.makeText(SettingActivity.this, "Gagal memuat avatar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Achievement>> call, Throwable t) {
                setLoading(false);
                Toast.makeText(SettingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayBottomSheet(List<AvatarItem> avatars) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_avatar_selection, null);
        dialog.setContentView(view);

        RecyclerView rvAvatars = view.findViewById(R.id.rv_avatar_selection);
        Button btnClose = view.findViewById(R.id.btn_close_avatar_selection);

        rvAvatars.setLayoutManager(new GridLayoutManager(this, 3));
        
        String currentSeed = currentUser.getAvatarSeed() != null ? currentUser.getAvatarSeed() : "Felix";
        
        AvatarSelectionAdapter adapter = new AvatarSelectionAdapter(avatars, currentSeed, item -> {
            updateAvatar(item.getStyle(), item.getSeed());
            dialog.dismiss();
        });
        
        rvAvatars.setAdapter(adapter);

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void updateAvatar(String style, String seed) {
        setLoading(true);
        UpdateProfileRequest request = new UpdateProfileRequest(currentUser.getName(), style, seed);
        userApi.updateProfile(request).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    loadCurrentAvatar();
                    Toast.makeText(SettingActivity.this, "Avatar berhasil diubah!", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMsg = "Gagal mengubah avatar";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {}
                    Toast.makeText(SettingActivity.this, "Gagal: " + errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                setLoading(false);
                Toast.makeText(SettingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
