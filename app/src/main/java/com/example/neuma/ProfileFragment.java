package com.example.neuma;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.neuma.models.User;
import com.example.neuma.network.UserApi;
import com.example.neuma.utils.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName;
    private ImageButton btnSetting;
    private ImageButton btnAdmin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvProfileName = view.findViewById(R.id.tv_profile_name);
        btnSetting = view.findViewById(R.id.btn_setting);

        // Aksi Tombol Setting Ke SettingActivity
        if (btnSetting != null) {
            btnSetting.setOnClickListener(v -> {
                Intent intent = new Intent(requireActivity(), SettingActivity.class);
                startActivity(intent);
            });
        }

        btnAdmin = view.findViewById(R.id.btn_admin);
        if (btnAdmin != null) {
            com.example.neuma.utils.TokenManager tokenManager = new com.example.neuma.utils.TokenManager(requireContext());
            if ("neumaadmin".equals(tokenManager.getUsername())) {
                btnAdmin.setVisibility(View.VISIBLE);
                btnAdmin.setOnClickListener(v -> {
                    Intent intent = new Intent(requireActivity(), AdminActivity.class);
                    startActivity(intent);
                });
            }
        }

        loadProfileData();
    }

    private void loadProfileData() {
        com.example.neuma.models.User user = com.example.neuma.utils.DataManager.getInstance().getCurrentUser();
        
        if (isAdded() && user != null) {
            tvProfileName.setText(user.getName());

            String style = user.getAvatarStyle() != null ? user.getAvatarStyle() : "adventurer";
            String seed = user.getAvatarSeed() != null ? user.getAvatarSeed() : "Felix";
            String avatarUrl = "https://api.dicebear.com/9.x/" + style + "/png?seed=" + seed;

            ImageView ivAvatar = getView().findViewById(R.id.iv_avatar);
            if (ivAvatar != null) {
                com.bumptech.glide.Glide.with(requireContext())
                    .load(avatarUrl)
                    .into(ivAvatar);
            }

            // Render Stats
            TextView tvStreak = getView().findViewById(R.id.tv_stat_streak);
            TextView tvPoints = getView().findViewById(R.id.tv_stat_points);
            TextView tvPencapaian = getView().findViewById(R.id.tv_stat_pencapaian);
            TextView tvLevel = getView().findViewById(R.id.tv_stat_level);

            if (tvStreak != null) tvStreak.setText(user.getStreak() + " hari");
            if (tvPoints != null) tvPoints.setText(user.getPoints() + " Poin");
            if (tvPencapaian != null) tvPencapaian.setText(user.getAchievementsCount() + " Pencapaian");
            if (tvLevel != null) tvLevel.setText(user.getLevelsCompleted() + " Level");

            // Render Recent Activities
            android.widget.LinearLayout container = getView().findViewById(R.id.container_recent_activities);
            TextView tvNoActivity = getView().findViewById(R.id.tv_no_activity);
            if (container != null && tvNoActivity != null) {
                // Bersihkan container dari item sebelumnya (kecuali tv_no_activity)
                container.removeAllViews();
                container.addView(tvNoActivity);

                java.util.List<User.ActivityItem> activities = user.getRecentActivities();
                if (activities == null || activities.isEmpty()) {
                    tvNoActivity.setVisibility(View.VISIBLE);
                } else {
                    tvNoActivity.setVisibility(View.GONE);
                    for (User.ActivityItem item : activities) {
                        View activityView = LayoutInflater.from(requireContext()).inflate(R.layout.item_recent_activity, container, false);

                        ImageView ivIcon = activityView.findViewById(R.id.iv_activity_icon);
                        TextView tvTitle = activityView.findViewById(R.id.tv_activity_title);
                        TextView tvTime = activityView.findViewById(R.id.tv_activity_time);

                        tvTitle.setText(item.getTitle());

                        // Format time (simplified for now)
                        try {
                            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
                            format.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                            java.util.Date date = format.parse(item.getTimestamp());
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
                                String url = "https://api.dicebear.com/9.x/" + item.getAvatarStyle() + "/png?seed=" + item.getAvatarSeed();
                                com.bumptech.glide.Glide.with(requireContext()).load(url).into(ivIcon);
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
        } else if (isAdded()) {
            tvProfileName.setText("Gagal Memuat");
        }
    }
}
