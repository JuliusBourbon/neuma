package com.example.neuma;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neuma.models.User;
import com.example.neuma.network.UserApi;
import com.example.neuma.utils.ApiClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtherProfileActivity extends AppCompatActivity {

    private TextView tvProfileName;
    private ImageButton btnSetting;
    private ImageButton btnAdmin;
    private String userId;
    private View loadingView;
    private View profileContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        loadingView = findViewById(R.id.loading_view);
        profileContent = findViewById(R.id.profile_content);

        userId = getIntent().getStringExtra("USER_ID");
        if (userId == null) {
            Toast.makeText(this, "ID Pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvProfileName = findViewById(R.id.tv_profile_name);
        btnSetting = findViewById(R.id.btn_setting);
        btnAdmin = findViewById(R.id.btn_admin);

        // Hide setting and admin buttons
        if (btnSetting != null) btnSetting.setVisibility(View.GONE);
        if (btnAdmin != null) btnAdmin.setVisibility(View.GONE);

        // Add back button behavior if there's a back button in the layout, wait, fragment_profile doesn't have one.
        // I will just load the data.

        loadProfileData();
    }

    private void loadProfileData() {
        loadingView.setVisibility(View.VISIBLE);
        profileContent.setVisibility(View.GONE);

        UserApi api = ApiClient.getAuthClient(this).create(UserApi.class);
        Call<User> call = api.getUserProfile(userId);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                loadingView.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    profileContent.setVisibility(View.VISIBLE);
                    renderProfile(response.body());
                } else {
                    Toast.makeText(OtherProfileActivity.this, "Gagal memuat profil", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                loadingView.setVisibility(View.GONE);
                Toast.makeText(OtherProfileActivity.this, "Error koneksi", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void renderProfile(User user) {
        tvProfileName.setText(user.getName());

        String style = user.getAvatarStyle() != null ? user.getAvatarStyle() : "adventurer";
        String seed = user.getAvatarSeed() != null ? user.getAvatarSeed() : "Felix";
        String avatarUrl = "https://api.dicebear.com/10.x/" + style + "/png?seed=" + seed;

        ImageView ivAvatar = findViewById(R.id.iv_avatar);
        if (ivAvatar != null) {
            com.bumptech.glide.Glide.with(this)
                .load(avatarUrl)
                .into(ivAvatar);
        }

        TextView tvStreak = findViewById(R.id.tv_stat_streak);
        TextView tvPoints = findViewById(R.id.tv_stat_points);
        TextView tvPencapaian = findViewById(R.id.tv_stat_pencapaian);
        TextView tvLevel = findViewById(R.id.tv_stat_level);

        if (tvStreak != null) tvStreak.setText(user.getStreak() + " hari");
        if (tvPoints != null) tvPoints.setText(user.getPoints() + " Poin");
        if (tvPencapaian != null) tvPencapaian.setText(user.getAchievementsCount() + " Pencapaian");
        if (tvLevel != null) tvLevel.setText(user.getLevelsCompleted() + " Level");

        LinearLayout container = findViewById(R.id.container_recent_activities);
        TextView tvNoActivity = findViewById(R.id.tv_no_activity);

        if (container != null && tvNoActivity != null) {
            container.removeAllViews();
            container.addView(tvNoActivity);

            List<User.ActivityItem> activities = user.getRecentActivities();
            if (activities == null || activities.isEmpty()) {
                tvNoActivity.setVisibility(View.VISIBLE);
            } else {
                tvNoActivity.setVisibility(View.GONE);
                for (User.ActivityItem item : activities) {
                    View activityView = LayoutInflater.from(this).inflate(R.layout.item_recent_activity, container, false);

                    ImageView ivIcon = activityView.findViewById(R.id.iv_activity_icon);
                    TextView tvTitle = activityView.findViewById(R.id.tv_activity_title);
                    TextView tvTime = activityView.findViewById(R.id.tv_activity_time);

                    tvTitle.setText(item.getTitle());

                    try {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                        format.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Date date = format.parse(item.getTimestamp());
                        long diff = System.currentTimeMillis() - date.getTime();

                        if (diff < 60 * 1000) {
                            tvTime.setText("Baru saja");
                        } else if (diff < 60 * 60 * 1000) {
                            tvTime.setText((diff / (60 * 1000)) + " menit yang lalu");
                        } else if (diff < 24 * 60 * 60 * 1000) {
                            tvTime.setText((diff / (60 * 60 * 1000)) + " jam yang lalu");
                        } else {
                            tvTime.setText((diff / (24 * 60 * 60 * 1000)) + " hari yang lalu");
                        }
                    } catch (Exception e) {
                        tvTime.setText(item.getTimestamp());
                    }

                    if ("achievement".equals(item.getType())) {
                        if (item.getAvatarSeed() != null && item.getAvatarStyle() != null) {
                            String url = "https://api.dicebear.com/10.x/" + item.getAvatarStyle() + "/png?seed=" + item.getAvatarSeed();
                            com.bumptech.glide.Glide.with(this).load(url).into(ivIcon);
                        } else {
                            ivIcon.setImageResource(R.drawable.ic_medal);
                        }
                    } else {
                        ivIcon.setImageResource(R.drawable.ic_star_blue);
                    }

                    container.addView(activityView);
                }
            }
        }
    }
}
