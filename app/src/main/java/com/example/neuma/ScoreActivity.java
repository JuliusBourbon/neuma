package com.example.neuma;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.neuma.models.Achievement;
import com.example.neuma.models.FinishAttemptResponse;
import com.example.neuma.models.Level;
import com.example.neuma.models.User;
import com.example.neuma.network.AchievementApi;
import com.example.neuma.network.LevelApi;
import com.example.neuma.network.UserApi;
import com.example.neuma.utils.ApiClient;
import com.example.neuma.utils.DataManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ScoreActivity extends AppCompatActivity {

    private TextView tvTotalScore;
    private LinearLayout layoutAchievements, layoutAchievementList;
    private Button btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        // Silent refetch data in background
        refetchData();

        tvTotalScore = findViewById(R.id.tv_total_score);
        layoutAchievementList = findViewById(R.id.layout_achievement_list);
        layoutAchievements = findViewById(R.id.layout_achievements);
        btnHome = findViewById(R.id.btn_home);

        int totalScore = getIntent().getIntExtra("TOTAL_SCORE", 0);
        tvTotalScore.setText(String.valueOf(totalScore));

        String achievementsJson = getIntent().getStringExtra("NEW_ACHIEVEMENTS");
        if (achievementsJson != null && !achievementsJson.isEmpty()) {
            try {
                Type listType = new TypeToken<List<FinishAttemptResponse.Achievement>>() {}.getType();
                List<FinishAttemptResponse.Achievement> achievements = new Gson().fromJson(achievementsJson, listType);
                
                if (achievements != null && !achievements.isEmpty()) {
                    layoutAchievements.setVisibility(View.VISIBLE);
                    for (FinishAttemptResponse.Achievement a : achievements) {
                        View itemView = getLayoutInflater().inflate(R.layout.item_score_achievement, layoutAchievementList, false);
                        
                        android.widget.TextView tvTitle = itemView.findViewById(R.id.tv_achievement_title);
                        android.widget.ImageView ivAvatar = itemView.findViewById(R.id.iv_achievement_avatar);
                        
                        tvTitle.setText(a.getTitle());
                        
                        String style = a.getAvatarRewardStyle() != null ? a.getAvatarRewardStyle() : "adventurer";
                        String seed = a.getAvatarRewardSeed() != null ? a.getAvatarRewardSeed() : "Felix";
                        String avatarUrl = "https://api.dicebear.com/9.x/" + style + "/png?seed=" + seed;
                        
                        com.bumptech.glide.Glide.with(ScoreActivity.this)
                            .load(avatarUrl)
                            .circleCrop()
                            .into(ivAvatar);
                            
                        layoutAchievementList.addView(itemView);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(ScoreActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void refetchData() {
        UserApi userApi = ApiClient.getAuthClient(this).create(UserApi.class);
        LevelApi levelApi = ApiClient.getAuthClient(this).create(LevelApi.class);
        AchievementApi achievementApi = ApiClient.getAuthClient(this).create(AchievementApi.class);

        userApi.getProfile().enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(retrofit2.Call<User> call, retrofit2.Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DataManager.getInstance().setCurrentUser(response.body());
                }
            }
            @Override
            public void onFailure(retrofit2.Call<User> call, Throwable t) { }
        });

        levelApi.getLevels().enqueue(new retrofit2.Callback<List<Level>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Level>> call, retrofit2.Response<List<Level>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DataManager.getInstance().setLevels(response.body());
                }
            }
            @Override
            public void onFailure(retrofit2.Call<List<Level>> call, Throwable t) { }
        });

        achievementApi.getAchievements().enqueue(new retrofit2.Callback<List<Achievement>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Achievement>> call, retrofit2.Response<List<Achievement>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DataManager.getInstance().setAchievements(response.body());
                }
            }
            @Override
            public void onFailure(retrofit2.Call<List<Achievement>> call, Throwable t) { }
        });
    }
}
