package com.example.neuma;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.neuma.adapters.AvatarSelectionAdapter;
import com.example.neuma.models.Achievement;
import com.example.neuma.models.AvatarItem;
import com.example.neuma.models.UpdatePasswordRequest;
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
    private LinearLayout btnMenuNama, btnMenuPassword;
    private Button btnLogout;
    private View progressBar;

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
        btnMenuNama = findViewById(R.id.btn_menu_nama);
        btnMenuPassword = findViewById(R.id.btn_menu_password);
        btnLogout = findViewById(R.id.btn_logout);
        progressBar = findViewById(R.id.progress_bar_setting);

        tokenManager = new TokenManager(this);
        userApi = ApiClient.getAuthClient(this).create(UserApi.class);
        achievementApi = ApiClient.getAuthClient(this).create(AchievementApi.class);

        btnBack.setOnClickListener(v -> finish());

        btnMenuNama.setOnClickListener(v -> showUpdateNameDialog());
        btnMenuPassword.setOnClickListener(v -> showUpdatePasswordDialog());

        // Klik Edit Avatar
        btnEditAvatarText.setOnClickListener(v -> showAvatarSelectionDialog());

        // Aksi Logout
        btnLogout.setOnClickListener(v -> performLogout());

        // Load data profil pengguna
        loadProfile();
    }

    private void loadProfile() {
        currentUser = com.example.neuma.utils.DataManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            tvUsername.setText(currentUser.getName());
            loadCurrentAvatar();
        }
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

        List<Achievement> achievements = com.example.neuma.utils.DataManager.getInstance().getAchievements();
        if (achievements != null) {
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
            Toast.makeText(SettingActivity.this, "Data avatar belum tersedia", Toast.LENGTH_SHORT).show();
        }
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
                    com.example.neuma.utils.DataManager.getInstance().setCurrentUser(currentUser);
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

    private void showUpdateNameDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_name, null);
        EditText etNama = view.findViewById(R.id.et_dialog_nama);
        Button btnBatal = view.findViewById(R.id.btn_dialog_batal_nama);
        Button btnSimpan = view.findViewById(R.id.btn_dialog_simpan_nama);

        if (currentUser != null) {
            etNama.setText(currentUser.getName());
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnBatal.setOnClickListener(v -> dialog.dismiss());
        btnSimpan.setOnClickListener(v -> {
            String newName = etNama.getText().toString().trim();
            if (!newName.isEmpty()) {
                performUpdateName(newName);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void performUpdateName(String newName) {
        setLoading(true);
        userApi.updateProfile(new UpdateProfileRequest(newName, null, null)).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    com.example.neuma.utils.DataManager.getInstance().setCurrentUser(currentUser);
                    tvUsername.setText(currentUser.getName());
                    Toast.makeText(SettingActivity.this, "Nama berhasil diubah!", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMsg = "Gagal mengubah nama";
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

    private void showUpdatePasswordDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_password, null);
        EditText etOldPass = view.findViewById(R.id.et_dialog_old_password);
        EditText etNewPass = view.findViewById(R.id.et_dialog_new_password);
        Button btnBatal = view.findViewById(R.id.btn_dialog_batal_password);
        Button btnSimpan = view.findViewById(R.id.btn_dialog_simpan_password);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnBatal.setOnClickListener(v -> dialog.dismiss());
        btnSimpan.setOnClickListener(v -> {
            String oldPass = etOldPass.getText().toString();
            String newPass = etNewPass.getText().toString();
            if (!oldPass.isEmpty() && !newPass.isEmpty()) {
                performUpdatePassword(oldPass, newPass);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void performUpdatePassword(String oldPass, String newPass) {
        setLoading(true);
        userApi.updatePassword(new UpdatePasswordRequest(oldPass, newPass)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(SettingActivity.this, "Password berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMsg = "Gagal! Password lama salah?";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {}
                    Toast.makeText(SettingActivity.this, "Gagal: " + errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                setLoading(false);
                Toast.makeText(SettingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performLogout() {
        tokenManager.clearToken();
        com.example.neuma.utils.DataManager.getInstance().clear();
        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}
